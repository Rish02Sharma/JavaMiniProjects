package org.example.WebhookDesign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IdempotencyRepositoryImpl Tests - In-Memory State Machine & Concurrency")
class IdempotencyRepositoryImplTest {

    private IdempotencyRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new IdempotencyRepositoryImpl();
    }

    @Nested
    @DisplayName("Single-Threaded State Transitions")
    class StateTransitionTests {

        @Test
        @DisplayName("Should return ACQUIRED when an eventId is seen for the first time")
        void shouldReturnAcquiredOnFirstAttempt() {
            String eventId = "evt-100";

            IdempotencyRepository.LockStatus status = repository.tryAcquire(eventId);

            assertThat(status).isEqualTo(IdempotencyRepository.LockStatus.ACQUIRED);
        }

        @Test
        @DisplayName("Should return ALREADY_IN_PROGRESS on subsequent attempts when processing is unfinished")
        void shouldReturnAlreadyInProgressOnSubsequentAttempt() {
            String eventId = "evt-101";

            repository.tryAcquire(eventId); // First acquire -> ACQUIRED
            IdempotencyRepository.LockStatus status = repository.tryAcquire(eventId); // Second acquire

            assertThat(status).isEqualTo(IdempotencyRepository.LockStatus.ALREADY_IN_PROGRESS);
        }

        @Test
        @DisplayName("Should return ALREADY_COMPLETED after markCompleted is called")
        void shouldReturnAlreadyCompletedAfterMarkCompleted() {
            String eventId = "evt-102";
            String downstreamCallId = "bm-call-999";

            repository.tryAcquire(eventId);
            repository.markCompleted(eventId, downstreamCallId);

            IdempotencyRepository.LockStatus status = repository.tryAcquire(eventId);

            assertThat(status).isEqualTo(IdempotencyRepository.LockStatus.ALREADY_COMPLETED);
        }

        @Test
        @DisplayName("Should allow re-acquiring lock after releaseLockOnFailure is called")
        void shouldAllowReAcquireAfterReleaseLockOnFailure() {
            String eventId = "evt-103";

            repository.tryAcquire(eventId); // In progress
            repository.releaseLockOnFailure(eventId); // Released

            IdempotencyRepository.LockStatus status = repository.tryAcquire(eventId);

            assertThat(status).isEqualTo(IdempotencyRepository.LockStatus.ACQUIRED);
        }
    }

    @Nested
    @DisplayName("Multi-Threaded Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Simulate race condition: Exactly 1 thread must acquire lock among 20 concurrent threads")
        void shouldAllowOnlyOneThreadToAcquireLockConcurrently() throws InterruptedException {
            // Arrange
            String eventId = "evt-race-condition";
            int threadCount = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            AtomicInteger acquiredCount = new AtomicInteger(0);
            AtomicInteger alreadyInProgressCount = new AtomicInteger(0);

            // Act: Schedule all threads to fire simultaneously
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    readyLatch.countDown();
                    try {
                        startLatch.await(); // wait for the starting gun
                        IdempotencyRepository.LockStatus status = repository.tryAcquire(eventId);
                        if (status == IdempotencyRepository.LockStatus.ACQUIRED) {
                            acquiredCount.incrementAndGet();
                        } else if (status == IdempotencyRepository.LockStatus.ALREADY_IN_PROGRESS) {
                            alreadyInProgressCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            readyLatch.await(2, TimeUnit.SECONDS);
            startLatch.countDown(); // Fire all threads at once
            boolean completed = doneLatch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // Assert
            assertThat(completed).isTrue();
            assertThat(acquiredCount.get()).as("Only 1 thread should successfully acquire lock").isEqualTo(1);
            assertThat(alreadyInProgressCount.get()).as("Remaining 19 threads should see IN_PROGRESS").isEqualTo(threadCount - 1);
        }
    }
}

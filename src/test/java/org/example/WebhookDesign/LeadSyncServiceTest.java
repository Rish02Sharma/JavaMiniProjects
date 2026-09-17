package org.example.WebhookDesign;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadSyncService Unit Tests with Mockito")
class LeadSyncServiceTest {

    @Mock
    private IdempotencyRepository idempotencyRepo;

    @Mock
    private BlueMachinesCampaignClient campaignClient;

    @InjectMocks
    private LeadSyncService leadSyncService;

    @Captor
    private ArgumentCaptor<RequestDto> eventCaptor;

    @Captor
    private ArgumentCaptor<String> eventIdCaptor;

    @Captor
    private ArgumentCaptor<String> downstreamCallIdCaptor;

    private RequestDto createSampleEvent(String eventId) {
        return new RequestDto(
                eventId,
                "cust-123",
                "cand-456",
                "+14155552671",
                "en",
                "hiring_campaign",
                Instant.now(),
                Map.of("key", "val")
        );
    }

    @Nested
    @DisplayName("processEvent - Idempotency & Workflow Execution")
    class ProcessEventWorkflowTests {

        @Test
        @DisplayName("Happy Path: When lock is ACQUIRED, should trigger campaign and mark event as COMPLETED")
        void shouldProcessEventSuccessfullyWhenLockAcquired() {
            // Arrange
            String eventId = "evt-happy-path";
            RequestDto event = createSampleEvent(eventId);
            when(idempotencyRepo.tryAcquire(eventId)).thenReturn(IdempotencyRepository.LockStatus.ACQUIRED);
            when(campaignClient.triggerCampaignWithRetry(event))
                    .thenReturn(new BlueMachinesCampaignClient.CampaignResponse("bm-camp-777", "SCHEDULED"));

            // Act
            leadSyncService.processEvent(event);

            // Assert
            verify(idempotencyRepo, times(1)).tryAcquire(eventId);
            verify(campaignClient, times(1)).triggerCampaignWithRetry(eventCaptor.capture());
            assertThat(eventCaptor.getValue()).isEqualTo(event);

            verify(idempotencyRepo, times(1)).markCompleted(eventIdCaptor.capture(), downstreamCallIdCaptor.capture());
            assertThat(eventIdCaptor.getValue()).isEqualTo(eventId);
            assertThat(downstreamCallIdCaptor.getValue()).isEqualTo("bm-camp-777");

            verify(idempotencyRepo, never()).releaseLockOnFailure(any());
        }

        @Test
        @DisplayName("Conflict: When lock is ALREADY_IN_PROGRESS, should throw 409 Conflict without triggering campaign")
        void shouldThrowConflictWhenLockIsAlreadyInProgress() {
            // Arrange
            String eventId = "evt-concurrent";
            RequestDto event = createSampleEvent(eventId);
            when(idempotencyRepo.tryAcquire(eventId)).thenReturn(IdempotencyRepository.LockStatus.ALREADY_IN_PROGRESS);

            // Act & Assert
            assertThatThrownBy(() -> leadSyncService.processEvent(event))
                    .isInstanceOf(ResponseStatusException.class)
                    .satisfies(ex -> {
                        ResponseStatusException rse = (ResponseStatusException) ex;
                        assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                        assertThat(rse.getReason()).contains("Concurrent duplicate event processing");
                    });

            verify(idempotencyRepo, times(1)).tryAcquire(eventId);
            verifyNoInteractions(campaignClient);
            verify(idempotencyRepo, never()).markCompleted(any(), any());
            verify(idempotencyRepo, never()).releaseLockOnFailure(any());
        }

        @Test
        @DisplayName("Idempotent Suppression: When lock is ALREADY_COMPLETED, should suppress side effects and return cleanly")
        void shouldSuppressSideEffectsWhenLockIsAlreadyCompleted() {
            // Arrange
            String eventId = "evt-completed";
            RequestDto event = createSampleEvent(eventId);
            when(idempotencyRepo.tryAcquire(eventId)).thenReturn(IdempotencyRepository.LockStatus.ALREADY_COMPLETED);

            // Act
            leadSyncService.processEvent(event);

            // Assert
            verify(idempotencyRepo, times(1)).tryAcquire(eventId);
            verifyNoInteractions(campaignClient);
            verify(idempotencyRepo, never()).markCompleted(any(), any());
            verify(idempotencyRepo, never()).releaseLockOnFailure(any());
        }

        @Test
        @DisplayName("Failure Rollback: When downstream campaign client throws exception, should release lock and rethrow")
        void shouldReleaseLockOnFailureWhenDownstreamClientFails() {
            // Arrange
            String eventId = "evt-downstream-err";
            RequestDto event = createSampleEvent(eventId);
            when(idempotencyRepo.tryAcquire(eventId)).thenReturn(IdempotencyRepository.LockStatus.ACQUIRED);
            RuntimeException downstreamError = new RuntimeException("BlueMachines upstream 503 Gateway Timeout");
            when(campaignClient.triggerCampaignWithRetry(any())).thenThrow(downstreamError);

            // Act & Assert
            assertThatThrownBy(() -> leadSyncService.processEvent(event))
                    .isSameAs(downstreamError);

            // Assert Rollback behavior
            verify(idempotencyRepo, times(1)).releaseLockOnFailure(eq(eventId));
            verify(idempotencyRepo, never()).markCompleted(any(), any());
        }
    }

    @Nested
    @DisplayName("maskPhoneNumber - PII Sanitization")
    class MaskPhoneNumberTests {

        @ParameterizedTest
        @CsvSource({
                "+14155552671, +14****2671",
                "+919876543210, +91****3210",
                "+441234567890, +44****7890",
                "1234567, 123****4567"
        })
        @DisplayName("Should mask middle digits of valid phone numbers >= 7 characters")
        void shouldMaskMiddleDigitsForNormalPhones(String input, String expected) {
            String masked = leadSyncService.maskPhoneNumber(input);
            assertThat(masked).isEqualTo(expected);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"123", "123456"})
        @DisplayName("Should return '***' when phone is null, empty, or less than 7 characters")
        void shouldReturnTripleAsterisksForShortOrNullPhones(String input) {
            String masked = leadSyncService.maskPhoneNumber(input);
            assertThat(masked).isEqualTo("***");
        }
    }
}

package org.example.JavaPractice.concurrency;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * =============================================================================================
 *   MASTERCLASS: THREADS, LOCKS, EXECUTORS, FUTURES, COMPLETABLEFUTURE & VIRTUAL THREADS
 * =============================================================================================
 *
 * This file serves as an end-to-end, runnable guide and masterclass covering Java threads from
 * base fundamentals to advanced asynchronous orchestration and modern Java 21 features:
 *
 * TABLE OF CONTENTS:
 *  1. Thread Fundamentals & The 6 Thread Lifecycle States (NEW to TERMINATED)
 *  2. Thread Control Primitives (sleep vs wait, join, yield, daemon vs user threads)
 *  3. Cooperative Interruption Model (Why stop() is deadly, InterruptedException handling)
 *  4. Classic Wait / Notify / NotifyAll & Guarded Suspension (Why while loop is mandatory)
 *  5. Modern Synchronization Utilities (CountDownLatch, CyclicBarrier, Semaphore)
 *  6. Explicit Locks Framework (ReentrantLock, tryLock, Condition Variables, Fair vs Non-fair)
 *  7. Read-Write Locks & StampedLock (Optimistic Read Locking)
 *  8. ExecutorService & Scheduled Tasks (The 7 parameters, Rejection Policies & ScheduledPool)
 *  9. Future<V> & The Blocking Asynchronous Model (submit, get, cancel, invokeAll, invokeAny)
 * 10. CompletableFuture Deep Dive (supplyAsync, thenApply, thenCompose, thenCombine, allOf, timeouts)
 * 11. ThreadLocal Storage & The Thread Pool Memory Leak Trap
 * 12. Deadlock Simulation, Detection (ThreadMXBean) & Prevention Strategies
 * 13. Java 21 Virtual Threads (Project Loom, Carrier Threads, M:N Model & Pinning Gotcha)
 * =============================================================================================
 */
public class ThreadMastery {

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("  JAVA THREADS, EXECUTORS, COMPLETABLEFUTURE & VIRTUAL THREADS     ");
        System.out.println("===================================================================\n");

        section1_ThreadFundamentalsAndLifecycle();
        section2_ThreadControlPrimitives();
        section3_CooperativeInterruption();
        section4_WaitNotifyGuardedSuspension();
        section5_ModernSynchronizationUtilities();
        section6_ExplicitLocksAndConditions();
        section7_ReadWriteAndStampedLocks();
        section8_ExecutorServiceAndScheduledTasks();
        section9_FutureAndBlockingAsync();
        section10_CompletableFutureDeepDive();
        section11_ThreadLocalAndMemoryLeaks();
        section12_DeadlockDetectionAndPrevention();
        section13_Java21VirtualThreads();

        System.out.println("\n===================================================================");
        System.out.println("              ALL THREAD MASTERY LESSONS COMPLETE!                 ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: THREAD FUNDAMENTALS & THE 6 LIFECYCLE STATES
    // =========================================================================================
    private static void section1_ThreadFundamentalsAndLifecycle() throws InterruptedException {
        printHeader("1. THREAD FUNDAMENTALS & THE 6 LIFECYCLE STATES");
        /*
         * PROCESS VS. THREAD:
         *  - Process: An executing program with its own isolated address space, heap, and OS descriptors.
         *  - Thread:  A "lightweight process" executing within a Process. All threads share the
         *             SAME heap memory, but each thread has its own private:
         *               - Thread Stack (stack frames, local variables)
         *               - Program Counter (PC register pointing to current bytecode instruction)
         *
         * THE 6 THREAD STATES (Thread.State enum):
         * -------------------------------------------------------------------------------------
         *  1. NEW           : Created with `new Thread()`, but `start()` has not been called yet.
         *  2. RUNNABLE      : Executing in the JVM or ready to run waiting for OS CPU time.
         *  3. BLOCKED       : Waiting to acquire an intrinsic monitor lock (synchronized block).
         *  4. WAITING       : Waiting indefinitely for another thread (wait(), join(), park()).
         *  5. TIMED_WAITING : Waiting for a specified time (sleep(ms), wait(ms), join(ms)).
         *  6. TERMINATED    : Completed run() method (or exited abruptly due to an uncaught exception).
         * -------------------------------------------------------------------------------------
         */

        Object monitor = new Object();

        // Demonstrate thread states live:
        Thread worker = new Thread(() -> {
            try {
                // Will enter TIMED_WAITING during sleep:
                Thread.sleep(100);
                // Will enter BLOCKED if monitor is held:
                synchronized (monitor) {
                    // Do nothing
                }
            } catch (InterruptedException ignored) {}
        }, "StateDemo-Worker");

        System.out.println("State immediately after creation:           " + worker.getState()); // NEW

        synchronized (monitor) {
            worker.start();
            Thread.sleep(30); // Give worker time to run and enter sleep()
            System.out.println("State while sleeping (sleep):              " + worker.getState()); // TIMED_WAITING

            Thread.sleep(100); // Wait for sleep to finish and hit synchronized block
            System.out.println("State waiting for monitor (synchronized):  " + worker.getState()); // BLOCKED
        } // Monitor released here!

        worker.join(); // Wait for completion
        System.out.println("State after completion:                     " + worker.getState()); // TERMINATED
    }

    // =========================================================================================
    // SECTION 2: THREAD CONTROL PRIMITIVES
    // =========================================================================================
    private static void section2_ThreadControlPrimitives() throws InterruptedException {
        printHeader("2. THREAD CONTROL PRIMITIVES (sleep vs wait, join, daemon threads)");
        /*
         *  Thread.sleep() VS. Object.wait() (CRUCIAL INTERVIEW QUESTION):
         *  -----------------------------------------------------------------------------------
         *  Feature             | Thread.sleep(millis)         | Object.wait()
         *  --------------------|------------------------------|-------------------------------
         *  Class               | java.lang.Thread (static)    | java.lang.Object (instance)
         *  Lock Handling       | KEEPS ALL LOCKS HELD!        | RELEASES the monitor lock!
         *  Synchronized Req    | Can be called anywhere       | MUST be inside synchronized
         *  Wakeup Condition    | Time expires or interrupted  | notify(), notifyAll(), timeout
         *  -----------------------------------------------------------------------------------
         *
         *  DAEMON THREADS VS. USER THREADS:
         *   - The JVM process terminates when ALL non-daemon (user) threads have finished.
         *   - Daemon threads are background service threads (e.g. Garbage Collector, finalizer).
         *   - When the last user thread terminates, all daemon threads are ABRUPTLY KILLED!
         *     (finally blocks in daemon threads may NEVER execute!).
         */

        // Demonstrating Thread.join() (Caller waits for target thread to finish):
        long startTime = System.currentTimeMillis();
        Thread calculationThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        });
        calculationThread.start();
        calculationThread.join(); // Main thread pauses until calculationThread finishes!
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Thread.join() waited successfully (~" + elapsed + " ms)");

        // Demonstrating Daemon thread behavior:
        Thread daemonThread = new Thread(() -> {
            while (true) {
                // Background housekeeping
            }
        });
        daemonThread.setDaemon(true); // Must be set BEFORE start()!
        daemonThread.start();
        System.out.println("Daemon thread running in background? " + daemonThread.isDaemon());
    }

    // =========================================================================================
    // SECTION 3: COOPERATIVE INTERRUPTION MODEL
    // =========================================================================================
    private static void section3_CooperativeInterruption() throws InterruptedException {
        printHeader("3. COOPERATIVE INTERRUPTION MODEL (Why stop() is Deadly)");
        /*
         * WHY Thread.stop(), Thread.suspend() & Thread.resume() ARE DEPRECATED:
         *  - Thread.stop() abruptly kills a thread and unlocks all monitors it held.
         *  - This leaves shared data structures in partially-mutated, corrupted states!
         *  - Thread.suspend() freezes a thread while keeping its locks held -> Instant Deadlock!
         *
         * JAVA'S COOPERATIVE INTERRUPTION:
         *  - Interruption is a REQUEST, not a command! A thread must cooperatively check its flag.
         *  - thread.interrupt(): Sets the thread's interrupt status flag.
         *  - If the thread is blocked in sleep(), wait(), or join(), the method immediately wakes
         *    up, CLEARS the interrupt flag, and throws InterruptedException!
         *
         * GOLDEN RULE: NEVER SWALLOW InterruptedException!
         *   Catching InterruptedException clears the flag. You MUST either:
         *     1. Re-throw it up the call stack, OR
         *     2. Restore the flag: Thread.currentThread().interrupt();
         */

        Thread cooperativeWorker = new Thread(() -> {
            System.out.println("   Worker started. Doing work...");
            long count = 0;
            // Check interrupt flag in long-running CPU loops:
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 10_000_000 == 0) {
                    try {
                        Thread.sleep(10); // Simulated I/O
                    } catch (InterruptedException e) {
                        System.out.println("   Worker caught InterruptedException during sleep!");
                        // Restore interrupt status:
                        Thread.currentThread().interrupt();
                        break; // Exit loop cleanly
                    }
                }
            }
            System.out.println("   Worker cooperatively shut down cleanly. Count: " + count);
        });

        cooperativeWorker.start();
        Thread.sleep(50);
        System.out.println("Sending interrupt() signal to worker...");
        cooperativeWorker.interrupt();
        cooperativeWorker.join();
    }

    // =========================================================================================
    // SECTION 4: CLASSIC WAIT / NOTIFY & GUARDED SUSPENSION
    // =========================================================================================
    private static void section4_WaitNotifyGuardedSuspension() throws InterruptedException {
        printHeader("4. CLASSIC WAIT / NOTIFY & GUARDED SUSPENSION");
        /*
         * WHY IS `while (!condition)` MANDATORY FOR wait()?
         * (NEVER use `if (!condition) wait();`)
         *
         * Reason 1: SPURIOUS WAKEUPS
         *   Operating systems can wake up a waiting thread even if NO notify() was called!
         * Reason 2: STALE CONDITIONS (LATE ARRIVAL)
         *   Between the moment thread A is notified and the moment it re-acquires the lock,
         *   thread B might sneak in and consume the resource!
         *
         * notify() vs. notifyAll():
         *   - notify(): Wakes up ONE arbitrary waiting thread.
         *     DANGER: If multiple threads wait for different conditions, notify() might wake
         *     the wrong thread, resulting in a permanent deadlock/lost signal!
         *   - notifyAll(): Wakes up ALL waiting threads. Safe default choice!
         */

        // Classic Bounded Buffer Queue using wait() and notifyAll():
        class BoundedBlockingQueue<T> {
            private final Queue<T> queue = new LinkedList<>();
            private final int capacity;

            BoundedBlockingQueue(int capacity) { this.capacity = capacity; }

            synchronized void put(T item) throws InterruptedException {
                while (queue.size() == capacity) { // Loop is mandatory!
                    wait(); // Releases lock and sleeps
                }
                queue.add(item);
                notifyAll(); // Wake up any waiting consumers
            }

            synchronized T take() throws InterruptedException {
                while (queue.isEmpty()) { // Loop is mandatory!
                    wait(); // Releases lock and sleeps
                }
                T item = queue.poll();
                notifyAll(); // Wake up any waiting producers
                return item;
            }
        }

        BoundedBlockingQueue<Integer> bbQueue = new BoundedBlockingQueue<>(2);

        Thread consumer = new Thread(() -> {
            try {
                int val = bbQueue.take();
                System.out.println("   Consumer received: " + val);
            } catch (InterruptedException ignored) {}
        });

        consumer.start();
        Thread.sleep(20); // Ensure consumer is waiting
        bbQueue.put(42);
        consumer.join();
    }

    // =========================================================================================
    // SECTION 5: MODERN SYNCHRONIZATION UTILITIES (LATCH, BARRIER, SEMAPHORE)
    // =========================================================================================
    private static void section5_ModernSynchronizationUtilities() throws InterruptedException {
        printHeader("5. MODERN SYNCHRONIZATION UTILITIES (LATCH, BARRIER, SEMAPHORE)");
        /*
         *  Utility         | Description
         *  ----------------|-------------------------------------------------------------------
         *  CountDownLatch  | One-shot gate. One or more threads wait until N countdown events occur.
         *                  | CANNOT be reset or reused!
         *  CyclicBarrier   | Reusable barrier. N threads wait for EACH OTHER to arrive at the barrier
         *                  | before all proceeding together. Can run an optional barrier action!
         *  Semaphore       | Manages a pool of permits. Controls access to finite resources (rate limiting).
         */

        // 1. CountDownLatch: Starter pistol / Service readiness
        CountDownLatch serviceReadyLatch = new CountDownLatch(3);
        for (int i = 1; i <= 3; i++) {
            Thread.ofPlatform().start(serviceReadyLatch::countDown);
        }
        serviceReadyLatch.await(1, TimeUnit.SECONDS);
        System.out.println("1. CountDownLatch: All 3 services reported ready!");

        // 2. CyclicBarrier: Multi-phase game / batch coordination
        CyclicBarrier barrier = new CyclicBarrier(3, () ->
                System.out.println("   [Barrier Action] All 3 workers completed Phase! Proceeding to next phase..."));

        List<Thread> workers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Thread t = Thread.ofPlatform().start(() -> {
                try {
                    barrier.await(); // Wait for all 3 workers
                } catch (Exception ignored) {}
            });
            workers.add(t);
        }
        for (Thread t : workers) t.join();

        // 3. Semaphore: Rate-limiting to 2 concurrent database connections
        Semaphore connectionPool = new Semaphore(2);
        System.out.println("3. Semaphore initial available permits: " + connectionPool.availablePermits());
        connectionPool.acquire(); // Takes 1 permit
        System.out.println("   Acquired 1 permit, remaining: " + connectionPool.availablePermits());
        connectionPool.release(); // Returns permit
    }

    // =========================================================================================
    // SECTION 6: EXPLICIT LOCKS FRAMEWORK (ReentrantLock & Conditions)
    // =========================================================================================
    private static void section6_ExplicitLocksAndConditions() throws InterruptedException {
        printHeader("6. EXPLICIT LOCKS (ReentrantLock, tryLock & Condition Variables)");
        /*
         * WHY EXPLICIT LOCKS OVER synchronized?
         *  1. Non-blocking tryLock(): Attempt lock without hanging forever if unavailable.
         *  2. Timed tryLock(timeout): Wait up to X milliseconds for a lock.
         *  3. Interruptible locking: lockInterruptibly() allows responding to cancel signals.
         *  4. Fairness policy: ReentrantLock(true) grants lock in FIFO order of waiting threads.
         *  5. Multiple Condition variables: Unlike synchronized which has ONE wait-set,
         *     a Lock can have multiple Condition objects (e.g. `notFull` and `notEmpty`).
         *
         * CRUCIAL RULE: ALWAYS USE try-finally WITH EXPLICIT LOCKS!
         *   lock.lock();
         *   try {
         *       // Critical section
         *   } finally {
         *       lock.unlock(); // Guaranteed to release lock even if an exception is thrown!
         *   }
         */

        ReentrantLock lock = new ReentrantLock();

        // tryLock demonstration (Non-blocking):
        lock.lock();
        try {
            // Another thread attempts to grab the lock without blocking:
            Thread observer = new Thread(() -> {
                boolean acquired = lock.tryLock();
                System.out.println("   Observer tryLock() acquired while held by main thread? " + acquired);
                if (acquired) {
                    lock.unlock();
                }
            });
            observer.start();
            observer.join();
        } finally {
            lock.unlock();
        }

        // Multiple Condition Variables Demonstration:
        Condition notFull = lock.newCondition();
        Condition notEmpty = lock.newCondition();
        System.out.println("ReentrantLock created separate 'notFull' and 'notEmpty' Conditions successfully.");
    }

    // =========================================================================================
    // SECTION 7: READ-WRITE LOCKS & StampedLock
    // =========================================================================================
    private static void section7_ReadWriteAndStampedLocks() {
        printHeader("7. READ-WRITE LOCKS & StampedLock (OPTIMISTIC LOCKING)");
        /*
         *  ReentrantReadWriteLock:
         *   - Allows multiple concurrent READERS (Shared Lock).
         *   - Allows only one WRITER (Exclusive Lock).
         *   - Great for Read-Heavy workloads (Reads >> Writes).
         *   - Downside: Write starvation (if readers continuously acquire lock, writers starve).
         *
         *  StampedLock (Java 8+):
         *   - Faster alternative to ReadWriteLock.
         *   - Provides OPTIMISTIC READ LOCKING:
         *     1. Does NOT acquire a lock at all! Returns an integer `stamp`.
         *     2. Reads the fields directly without blocking writers.
         *     3. Calls `lock.validate(stamp)`.
         *        If true -> No write occurred! Data read is consistent!
         *        If false -> A write happened concurrently! Fall back to a standard read lock!
         */

        StampedLock stampedLock = new StampedLock();
        class Point {
            private double x = 1.0, y = 2.0;

            double distanceFromOriginOptimistic() {
                // 1. Optimistic read (Zero lock acquisition overhead!):
                long stamp = stampedLock.tryOptimisticRead();
                double currentX = x;
                double currentY = y;

                // 2. Validate if a writer intervened:
                if (!stampedLock.validate(stamp)) {
                    // Fall back to pessimistic read lock:
                    stamp = stampedLock.readLock();
                    try {
                        currentX = x;
                        currentY = y;
                    } finally {
                        stampedLock.unlockRead(stamp);
                    }
                }
                return Math.sqrt(currentX * currentX + currentY * currentY);
            }
        }

        Point p = new Point();
        System.out.println("StampedLock optimistic distance calculation: " + p.distanceFromOriginOptimistic());
    }

    // =========================================================================================
    // SECTION 8: ExecutorService & SCHEDULED TASKS
    // =========================================================================================
    private static void section8_ExecutorServiceAndScheduledTasks() throws InterruptedException {
        printHeader("8. ExecutorService & SCHEDULED TASKS (The 7 Parameters & Rejection)");
        /*
         * WHY NEVER USE Executors.newFixedThreadPool() IN PRODUCTION?
         *  - `newFixedThreadPool(n)` uses an UNBOUNDED `LinkedBlockingQueue` (Integer.MAX_VALUE)!
         *  - If tasks arrive faster than worker threads can process them, the queue grows infinitely
         *    until the JVM dies with `OutOfMemoryError: Java heap space`!
         *
         * THE 7 PARAMETERS OF ThreadPoolExecutor:
         *  1. corePoolSize    : Number of core threads kept alive even when idle.
         *  2. maximumPoolSize : Max threads allowed when queue becomes completely full.
         *  3. keepAliveTime   : Time excess idle threads (above core) wait before termination.
         *  4. unit            : TimeUnit for keepAliveTime.
         *  5. workQueue       : BlockingQueue holding tasks before execution (e.g. ArrayBlockingQueue).
         *  6. threadFactory   : Custom naming & daemon settings for worker threads.
         *  7. handler         : RejectedExecutionHandler when pool and queue are saturated!
         *
         * THE 4 REJECTION POLICIES:
         *  - AbortPolicy (Default) : Throws RejectedExecutionException immediately.
         *  - CallerRunsPolicy      : The CALLER thread executes the task itself! (Backpressure protection).
         *  - DiscardPolicy         : Silently drops the task with no error.
         *  - DiscardOldestPolicy   : Drops the oldest unhandled task in the queue and retries.
         *
         * ScheduledExecutorService:
         *  - scheduleAtFixedRate: Fixed time between START of executions (can overlap if task duration > period).
         *  - scheduleWithFixedDelay: Fixed time between END of previous execution and START of next.
         */

        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
                2,                                   // corePoolSize
                4,                                   // maximumPoolSize
                30, TimeUnit.SECONDS,                // keepAliveTime
                new ArrayBlockingQueue<>(4),         // Bounded queue (capacity 4)
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // Backpressure protection!
        );

        // Submit tasks:
        for (int i = 1; i <= 6; i++) {
            customPool.execute(() -> {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ignored) {}
            });
        }

        System.out.println("Custom ThreadPool active count: " + customPool.getActiveCount()
                + " | Queue size: " + customPool.getQueue().size());

        // Graceful Shutdown Protocol:
        customPool.shutdown(); // Refuses new tasks, drains existing
        if (!customPool.awaitTermination(2, TimeUnit.SECONDS)) {
            customPool.shutdownNow(); // Force cancel hanging tasks
        }
        System.out.println("Custom ThreadPool shut down cleanly: " + customPool.isTerminated());

        // ScheduledExecutorService demonstration:
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        CountDownLatch scheduleLatch = new CountDownLatch(1);
        scheduler.schedule(() -> {
            System.out.println("   [Scheduled Task] Fired once after 50ms delay!");
            scheduleLatch.countDown();
        }, 50, TimeUnit.MILLISECONDS);

        scheduleLatch.await(1, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    // =========================================================================================
    // SECTION 9: Future<V> & THE BLOCKING ASYNCHRONOUS MODEL
    // =========================================================================================
    private static void section9_FutureAndBlockingAsync() throws Exception {
        printHeader("9. Future<V> & THE BLOCKING ASYNCHRONOUS MODEL");
        /*
         * WHAT IS A Future<V>?
         * Introduced in Java 5, a `Future` represents the pending result of an asynchronous computation.
         *
         * Key Methods:
         *  - V get()                     : BLOCKS the calling thread until computation completes.
         *  - V get(long timeout, TimeUnit) : Blocks up to timeout, then throws TimeoutException.
         *  - boolean isDone()            : Checks if task completed (normally, exception, or cancelled).
         *  - boolean cancel(boolean mayInterruptIfRunning): Attempts to cancel the task.
         *  - boolean isCancelled()       : Returns true if cancelled before completion.
         *
         * BATCH SUBMISSION:
         *  - invokeAll(tasks) : Blocks until ALL callables complete; returns List<Future<T>>.
         *  - invokeAny(tasks) : Blocks until ANY ONE callable completes successfully; cancels the rest!
         *
         * THE 5 MAJOR LIMITATIONS OF Future:
         *  1. Blocking get(): Stalls the calling thread, defeating the purpose of non-blocking I/O.
         *  2. Cannot be completed manually (e.g. from an asynchronous callback).
         *  3. Cannot chain dependent async tasks (no "when done, then do X").
         *  4. Cannot combine multiple futures without blocking.
         *  5. No built-in exception recovery pipeline.
         */

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 1. Single Future with Callable:
        Callable<String> expensiveTask = () -> {
            Thread.sleep(80);
            return "Order-9001 Processed";
        };

        Future<String> future = executor.submit(expensiveTask);
        System.out.println("Future submitted. isDone before wait? " + future.isDone());

        // Timed blocking get:
        String result = future.get(500, TimeUnit.MILLISECONDS);
        System.out.println("future.get() result: " + result + " | isDone? " + future.isDone());

        // 2. Batch Execution with invokeAll:
        List<Callable<Integer>> tasks = List.of(
                () -> 10,
                () -> 20,
                () -> 30
        );

        List<Future<Integer>> results = executor.invokeAll(tasks);
        int totalSum = 0;
        for (Future<Integer> f : results) {
            totalSum += f.get();
        }
        System.out.println("invokeAll() summed result: " + totalSum);

        // 3. Fastest Wins with invokeAny:
        List<Callable<String>> mirrors = List.of(
                () -> { Thread.sleep(200); return "Server-US"; },
                () -> { Thread.sleep(50);  return "Server-EU (Fastest)"; },
                () -> { Thread.sleep(300); return "Server-ASIA"; }
        );

        String fastest = executor.invokeAny(mirrors);
        System.out.println("invokeAny() fastest response: " + fastest);

        executor.shutdown();
    }

    // =========================================================================================
    // SECTION 10: CompletableFuture DEEP DIVE (MODERN ASYNC & REACTIVE)
    // =========================================================================================
    private static void section10_CompletableFutureDeepDive() throws Exception {
        printHeader("10. CompletableFuture DEEP DIVE (MODERN ASYNC & REACTIVE)");
        /*
         * WHY CompletableFuture (Java 8+)?
         * Implements BOTH `Future<T>` and `CompletionStage<T>`:
         *  - Non-blocking, reactive, event-driven pipelines.
         *  - Can be manually completed via `complete(val)` or `completeExceptionally(ex)`.
         *  - Supports transforming (map), flattening (flatMap), and combining (zip) async stages.
         *  - Elegant exception handling and timeout recovery.
         *
         * CORE OPERATORS:
         *  1. Creation:
         *     - supplyAsync(Supplier, [executor]): Asynchronous computation with return value.
         *     - runAsync(Runnable, [executor]):    Asynchronous computation with void return.
         *     - completedFuture(value):            Pre-completed future.
         *  2. Transforming / Callbacks:
         *     - thenApply(Function):        Transforms result (synchronous on same thread).
         *     - thenApplyAsync(Function):   Transforms result on ForkJoinPool or custom Executor.
         *     - thenAccept(Consumer):       Terminal consumption (returns CompletableFuture<Void>).
         *     - thenRun(Runnable):          Runs action after stage finishes without using result.
         *  3. Composition (Dependent vs Independent):
         *     - thenCompose (flatMap):      Chains DEPENDENT futures: f(T) -> CompletableFuture<U>.
         *     - thenCombine (zip):          Combines 2 INDEPENDENT futures running in parallel.
         *  4. Multi-Future Coordination:
         *     - CompletableFuture.allOf:    Waits for ALL futures to complete.
         *     - CompletableFuture.anyOf:    Completes as soon as the FASTEST future completes.
         *  5. Exception Handling & Timeouts:
         *     - exceptionally:              Provides fallback value on error.
         *     - handle:                     Inspects BOTH value and exception.
         *     - orTimeout(Java 9+):         Fails with TimeoutException if taking too long.
         *     - completeOnTimeout(Java 9+): Fallback default value if timeout occurs.
         */

        ExecutorService customExecutor = Executors.newFixedThreadPool(4);

        // 1. Transforming Pipeline (supplyAsync -> thenApply -> thenAccept):
        CompletableFuture<String> pipeline = CompletableFuture
                .supplyAsync(() -> "iphone 16", customExecutor)
                .thenApply(String::toUpperCase)
                .thenApply(model -> "Product Catalog Entry: [" + model + "]");

        System.out.println("Pipeline output: " + pipeline.get());

        // 2. thenCompose (Chaining Dependent Async Operations - Monadic flatMap):
        // Fetch User -> Then fetch User's Orders based on User ID:
        CompletableFuture<String> orderPipeline = CompletableFuture
                .supplyAsync(() -> "UserId-42", customExecutor)
                .thenCompose(userId -> CompletableFuture.supplyAsync(() -> "Orders for " + userId + ": [ORD-1, ORD-2]"));

        System.out.println("thenCompose result: " + orderPipeline.get());

        // 3. thenCombine (Executing 2 Independent Futures in Parallel and Zipping Results):
        // Fetch Weather in parallel with Currency Exchange Rate:
        CompletableFuture<String> weatherFuture = CompletableFuture.supplyAsync(() -> "24°C Sunny", customExecutor);
        CompletableFuture<Double> usdToInrFuture = CompletableFuture.supplyAsync(() -> 84.5, customExecutor);

        CompletableFuture<String> dashboardWidget = weatherFuture.thenCombine(
                usdToInrFuture,
                (weather, rate) -> "Dashboard: Weather=" + weather + " | USD/INR=" + rate
        );
        System.out.println("thenCombine result: " + dashboardWidget.get());

        // 4. Exception Handling with exceptionally & handle:
        CompletableFuture<String> failingFuture = CompletableFuture
                .supplyAsync(() -> {
                    if (true) throw new RuntimeException("Payment Gateway Timeout");
                    return "Success";
                })
                .exceptionally(ex -> "Recovered from failure: " + ex.getMessage());

        System.out.println("exceptionally recovery: " + failingFuture.get());

        // 5. Java 9+ Timeout Protection (completeOnTimeout):
        CompletableFuture<String> slowService = CompletableFuture
                .supplyAsync(() -> {
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                    return "Slow Service Response";
                })
                .completeOnTimeout("Fast Cached Fallback", 50, TimeUnit.MILLISECONDS);

        System.out.println("completeOnTimeout result: " + slowService.get());

        // 6. allOf Multi-Service Aggregation:
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Data1");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Data2");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Data3");

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(f1, f2, f3);
        allFutures.join(); // Waits for all 3 without sequential blocking
        System.out.println("allOf joined all 3 futures successfully: " + f1.get() + ", " + f2.get() + ", " + f3.get());

        customExecutor.shutdown();
    }

    // =========================================================================================
    // SECTION 11: ThreadLocal & MEMORY LEAKS
    // =========================================================================================
    private static void section11_ThreadLocalAndMemoryLeaks() {
        printHeader("11. ThreadLocal STORAGE & THE THREAD POOL LEAK TRAP");
        /*
         * WHAT IS ThreadLocal?
         *  - Provides thread-confined variables. Each thread accessing a ThreadLocal gets its
         *    own completely independent copy.
         *  - Stored inside each Thread's internal `ThreadLocalMap` where keys are WeakReferences
         *    to the ThreadLocal object, but VALUES ARE STRONGLY REFERENCED!
         *
         * THE DEADLY THREAD POOL MEMORY LEAK:
         *  - In application servers (Tomcat, Spring Boot), worker threads are REUSED for thousands
         *    of requests across weeks.
         *  - If you store an object in a `ThreadLocal` during an HTTP request and forget to call
         *    `threadLocal.remove()`, that object REMAINS PINNED in the thread's map forever!
         *  - This causes catastrophic memory leaks and ClassLoader leaks across redeployments!
         *
         * GOLDEN RULE: ALWAYS call `threadLocal.remove()` in a finally block!
         */

        ThreadLocal<String> userContext = ThreadLocal.withInitial(() -> "Anonymous");

        try {
            userContext.set("User-Admin-109");
            System.out.println("ThreadLocal retrieved in current thread: " + userContext.get());
        } finally {
            userContext.remove(); // CRITICAL CLEANUP!
            System.out.println("ThreadLocal removed safely: " + userContext.get()); // Reverts to default "Anonymous"
        }
    }

    // =========================================================================================
    // SECTION 12: DEADLOCK SIMULATION, DETECTION & PREVENTION
    // =========================================================================================
    private static void section12_DeadlockDetectionAndPrevention() throws InterruptedException {
        printHeader("12. DEADLOCK DETECTION & PREVENTION");
        /*
         * THE 4 COFFMAN CONDITIONS FOR DEADLOCK:
         *  1. Mutual Exclusion  : At least one resource must be held in non-shareable mode.
         *  2. Hold and Wait     : A thread holding resources can request additional resources.
         *  3. No Preemption     : Resources cannot be forcibly taken from a thread holding them.
         *  4. Circular Wait     : A closed chain of threads exists, each waiting for the next.
         *
         * PREVENTION STRATEGIES:
         *  1. Lock Ordering: ALWAYS acquire locks in the exact same global order across all threads!
         *  2. Lock Timeout: Use `tryLock(timeout)` instead of indefinite blocking.
         */

        // Programmatic Deadlock Detection using JVM ThreadMXBean:
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = mxBean.findDeadlockedThreads();
        System.out.println("JVM ThreadMXBean active deadlock check: "
                + (deadlockedThreads == null ? "ZERO Deadlocks detected (System healthy)" : deadlockedThreads.length + " deadlocks!"));
    }

    // =========================================================================================
    // SECTION 13: JAVA 21 VIRTUAL THREADS (PROJECT LOOM)
    // =========================================================================================
    private static void section13_Java21VirtualThreads() throws InterruptedException {
        printHeader("13. JAVA 21 VIRTUAL THREADS (PROJECT LOOM)");
        /*
         * PLATFORM THREADS VS. VIRTUAL THREADS:
         * -------------------------------------------------------------------------------------
         * Feature               | Platform Threads (OS)       | Virtual Threads (Java 21 JEP 444)
         * ----------------------|-----------------------------|--------------------------------
         * Mapping               | 1-to-1 with OS Kernel Thread| M-to-N on Carrier Threads (ForkJoinPool)
         * Memory Footprint      | ~1 MB stack per thread      | ~Few hundred bytes (on JVM heap!)
         * Max Safe Count        | Thousands (OS limited)      | MILLIONS!
         * Creation Overhead     | Heavy OS syscall (~1ms)     | Almost zero (simple Java object)
         * Best For              | CPU-bound heavy computing   | I/O-bound tasks (HTTP, DB, Sockets)
         * Pooling               | MUST be pooled              | NEVER POOL! Create & discard per task!
         * -------------------------------------------------------------------------------------
         *
         * HOW VIRTUAL THREADS ACHIEVE MASSIVE SCALE:
         *  - When a Virtual Thread hits blocking I/O (`Thread.sleep`, socket read, JDBC query):
         *    The JVM UNMOUNTS the virtual thread from its OS Carrier Thread!
         *    The Carrier Thread is now immediately free to run other virtual threads!
         *    When the I/O finishes, the OS notifies the JVM, which MOUNTS the virtual thread back
         *    on an available carrier thread!
         *
         * THE PINNING GOTCHA (CRITICAL):
         *  - If a Virtual Thread blocks while inside a `synchronized` block or a native method,
         *    it PINNETH the carrier thread to the OS thread (cannot unmount)!
         *  - SOLUTION: Replace `synchronized` with `ReentrantLock` in high-throughput Virtual Thread code!
         */

        // 1. Spawning a single Virtual Thread:
        Thread vThread = Thread.ofVirtual().name("Loom-Worker").start(() -> {
            System.out.println("   Virtual thread running on carrier: " + Thread.currentThread());
        });
        vThread.join();

        // 2. Running 10,000 Virtual Threads concurrently in under a second!
        int vThreadCount = 10_000;
        Instant start = Instant.now();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < vThreadCount; i++) {
                executor.submit(() -> {
                    Thread.sleep(10); // Simulated blocking I/O
                    return 1;
                });
            }
        } // AutoCloseable executor waits for all 10,000 virtual threads to complete!

        Duration duration = Duration.between(start, Instant.now());
        System.out.printf("Successfully executed %,d Virtual Threads in %d ms!%n",
                vThreadCount, duration.toMillis());
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}

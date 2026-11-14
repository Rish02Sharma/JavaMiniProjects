package org.example.JavaPractice.concurrency;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * =============================================================================================
 *   MASTERCLASS: JAVA MEMORY MODEL (JMM), THREAD SAFETY & HIGH-THROUGHPUT CONCURRENCY
 * =============================================================================================
 *
 * This file serves as a comprehensive, runnable guide and benchmark suite for mastering:
 *  - The Java Memory Model (JMM) & Hardware Architecture (Caches, Store Buffers, RAM)
 *  - The 3 Pillars of Concurrency: Atomicity, Visibility & Ordering (Happens-Before)
 *  - Anatomy of Race Conditions & Bytecode disassembly of count++
 *  - Visibility & Memory Barriers with volatile (and why volatile != thread-safe)
 *  - Monitor Locks (synchronized) & Reentrancy
 *  - Lock-free CAS (Compare-And-Swap) with AtomicInteger & ABA Problem
 *  - High-Throughput Striped Cell Concurrency with LongAdder
 *  - Live Multi-Threaded Counter Race Condition Debugging & Benchmark Suite
 *  - False Sharing (Cache Line Contention) & System Architecture Trade-offs
 *
 * TABLE OF CONTENTS:
 *  1. JMM Architecture & The 3 Pillars (Atomicity, Visibility, Ordering)
 *  2. Race Conditions & The Anatomy of count++ (Bytecode Analysis)
 *  3. Visibility & The volatile Keyword (Memory Fences & Stale Reads)
 *  4. Synchronized Monitor Locks (Reentrancy & Memory Flushing)
 *  5. Lock-Free Concurrency (CAS, AtomicInteger & The ABA Problem)
 *  6. Striped High-Throughput Concurrency (LongAdder & Cell Splitting)
 *  7. Live Benchmark Suite: Comparing 5 Counter Implementations Under Heavy Contention
 *  8. False Sharing (CPU Cache Lines) & High-Throughput System Design Rules
 * =============================================================================================
 */
public class JmmAndConcurrency {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===================================================================");
        System.out.println("   JAVA MEMORY MODEL, THREAD SAFETY & LOCK-FREE BENCHMARK SUITE    ");
        System.out.println("===================================================================\n");

        section1_JmmArchitectureAndPillars();
        section2_RaceConditionsAndBytecode();
        section3_VolatileAndVisibility();
        section4_SynchronizedAndMonitors();
        section5_LockFreeCasAndAtomics();
        section6_LongAdderAndStriping();
        section7_LiveBenchmarkSuite();
        section8_FalseSharingAndArchitectureRules();

        System.out.println("\n===================================================================");
        System.out.println("             ALL CONCURRENCY LESSONS COMPLETE!                     ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: JMM ARCHITECTURE & THE 3 PILLARS
    // =========================================================================================
    private static void section1_JmmArchitectureAndPillars() {
        printHeader("1. JMM ARCHITECTURE & THE 3 PILLARS OF CONCURRENCY");
        /*
         * HARDWARE VS. JMM ARCHITECTURE:
         * -------------------------------------------------------------------------------------
         * Modern CPU Architecture:
         *   [ CPU Core 1 ]               [ CPU Core 2 ]
         *     Registers                    Registers
         *     Store Buffer                 Store Buffer
         *     L1 Cache (~32KB, 1ns)        L1 Cache (~32KB, 1ns)
         *     L2 Cache (~512KB, 4ns)       L2 Cache (~512KB, 4ns)
         *           \                          /
         *            Shared L3 Cache (~16MB-64MB, 15ns)
         *                          |
         *                  Main Memory (RAM, 100ns)
         * -------------------------------------------------------------------------------------
         *
         * THE 3 PILLARS OF CONCURRENCY:
         *
         *  1. ATOMICITY:
         *     - An operation is atomic if it executes as a single indivisible unit.
         *     - Either the whole operation completes, or nothing does. No other thread can see
         *       it in a partially completed state.
         *     - Note: Reads/writes of 32-bit primitives and references are atomic in Java.
         *     - Reads/writes of 64-bit `long` and `double` are NOT guaranteed to be atomic by the
         *       JVM spec unless declared `volatile`! (Can suffer from word tearing on 32-bit JVMs).
         *
         *  2. VISIBILITY:
         *     - When Thread 1 writes a value to a variable, when does Thread 2 see that update?
         *     - Without synchronization, Thread 1's write may sit in Core 1's Store Buffer or L1
         *       cache indefinitely without being flushed to RAM or Core 2's cache!
         *
         *  3. ORDERING (Instruction Reordering):
         *     - The compiler (javac), JIT compiler (C2), and the CPU itself reorder instructions
         *       to maximize pipelining and cache efficiency.
         *     - "As-if-serial" guarantee: Reordering will never alter the outcome of a SINGLE thread.
         *     - But across MULTIPLE threads, reordering can cause catastrophic bugs!
         *
         * THE HAPPENS-BEFORE RELATIONSHIP (JSR-133 Specification):
         *  If action A happens-before action B, the memory effects of A are guaranteed to be
         *  VISIBLE to B, and A is ordered before B.
         *   - Program Order Rule: Within a single thread, each action happens-before any subsequent action.
         *   - Monitor Lock Rule: An unlock on a monitor happens-before every subsequent lock on the same monitor.
         *   - Volatile Variable Rule: A write to a volatile field happens-before every subsequent read of that field.
         *   - Thread Start Rule: A call to Thread.start() happens-before any action in the started thread.
         *   - Thread Join Rule: Any action in a thread happens-before any other thread detects its termination (join).
         *   - Transitivity Rule: If A happens-before B, and B happens-before C, then A happens-before C.
         */

        System.out.println("JMM Key Rules Established:");
        System.out.println(" - 32-bit reads/writes are atomic by default; 64-bit long/double require 'volatile'.");
        System.out.println(" - Caches introduce visibility lag; compilers introduce instruction reordering.");
        System.out.println(" - The 'Happens-Before' contract guarantees memory visibility and order across threads.");
    }

    // =========================================================================================
    // SECTION 2: RACE CONDITIONS & THE ANATOMY OF count++
    // =========================================================================================
    private static void section2_RaceConditionsAndBytecode() throws InterruptedException {
        printHeader("2. RACE CONDITIONS & THE ANATOMY OF count++");
        /*
         * WHAT IS A RACE CONDITION?
         * A race condition occurs when multiple threads read and write shared mutable state
         * concurrently, and the final outcome depends on the non-deterministic interleaving of threads.
         *
         * WHY IS `count++` NOT THREAD-SAFE?
         * In Java source code, `count++` looks like a single step.
         * At the bytecode level, it is THREE SEPARATE OPERATIONS (Read-Modify-Write):
         *
         *   1. GETFIELD / ILOAD   -> Read `count` from memory/cache into CPU register.
         *   2. ICONST_1 + IADD   -> Increment register value by 1.
         *   3. PUTFIELD / ISTORE  -> Write updated value back to memory/cache.
         *
         * The Lost Update Scenario:
         *   Thread A reads count = 10
         *   Thread B reads count = 10
         *   Thread A computes 10 + 1 = 11, writes count = 11
         *   Thread B computes 10 + 1 = 11, writes count = 11 (OVERWRITES A's update!)
         *   Result: 2 increments occurred, but count only went from 10 to 11! One update was LOST!
         */

        class UnsafeCounter {
            int count = 0;
            void increment() { count++; }
        }

        UnsafeCounter unsafe = new UnsafeCounter();
        int threadCount = 10;
        int incrementsPerThread = 10_000;
        int expectedTotal = threadCount * incrementsPerThread; // 100,000

        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    unsafe.increment();
                }
                latch.countDown();
            });
        }
        latch.await();

        int lostUpdates = expectedTotal - unsafe.count;
        System.out.printf("Race Condition Result:%n");
        System.out.printf("   Expected Count: %,d%n", expectedTotal);
        System.out.printf("   Actual Count:   %,d%n", unsafe.count);
        System.out.printf("   Lost Updates:   %,d (%.2f%% data loss!)%n",
                lostUpdates, ((double) lostUpdates / expectedTotal) * 100);
        System.out.println("   Conclusion: count++ is NOT atomic!");
    }

    // =========================================================================================
    // SECTION 3: VISIBILITY & THE volatile KEYWORD
    // =========================================================================================
    private static void section3_VolatileAndVisibility() throws InterruptedException {
        printHeader("3. VISIBILITY & THE volatile KEYWORD");
        /*
         * THE STALE READ / INFINITE LOOP PROBLEM:
         * If Thread 1 loops on `while (!stopRequested)`, the JIT compiler (C2) can optimize this
         * loop into:
         *    if (!stopRequested)
         *        while (true) {} // Hoisting! Never re-reads stopRequested from RAM!
         * Even when Thread 2 sets `stopRequested = true`, Thread 1 NEVER STOPS because it
         * reads from its local CPU register / L1 cache!
         *
         * WHAT `volatile` DOES:
         *  1. Direct Memory Visibility:
         *     - Every write to a volatile variable is flushed immediately to Main Memory and
         *       invalidates the corresponding cache line in all other CPU cores (via MESI cache protocol).
         *     - Every read from a volatile variable is fetched directly from Main Memory / L3.
         *  2. Memory Barriers (Fences):
         *     - Prevents reordering: Writes to any variable BEFORE a volatile write cannot be
         *       reordered after it.
         *     - Reads of any variable AFTER a volatile read cannot be reordered before it.
         *
         * CRITICAL GOTCHA: volatile DOES NOT GUARANTEE ATOMICITY!
         *  - If you declare `volatile int count; count++;`, IT IS STILL BROKEN!
         *  - Volatile guarantees you read the latest value, but does NOT stop two threads from
         *    reading the same value at the same time and both writing back the same +1!
         */

        class WorkerRunnable implements Runnable {
            // Without `volatile`, the reader thread could spin forever due to JIT hoisting!
            private volatile boolean keepRunning = true;

            void stop() {
                keepRunning = false;
            }

            @Override
            public void run() {
                long spinCount = 0;
                while (keepRunning) {
                    spinCount++;
                }
                System.out.println("   Worker received volatile stop signal after " + spinCount + " spins.");
            }
        }

        WorkerRunnable worker = new WorkerRunnable();
        Thread thread = new Thread(worker);
        thread.start();

        Thread.sleep(50); // Let worker spin
        worker.stop();    // Update volatile flag from main thread
        thread.join(1000);

        // Demonstrating that volatile DOES NOT make count++ thread-safe:
        class VolatileCounter {
            volatile int count = 0;
            void increment() { count++; } // STILL NOT ATOMIC!
        }

        VolatileCounter vc = new VolatileCounter();
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < 10_000; j++) vc.increment();
                latch.countDown();
            });
        }
        latch.await();
        System.out.printf("Volatile counter test (expected 50,000): %,d -> (Still suffered race condition!)%n", vc.count);
    }

    // =========================================================================================
    // SECTION 4: SYNCHRONIZED MONITOR LOCKS & REENTRANCY
    // =========================================================================================
    private static void section4_SynchronizedAndMonitors() {
        printHeader("4. SYNCHRONIZED MONITOR LOCKS & REENTRANCY");
        /*
         * INTRINSIC LOCKS (MONITORS):
         * Every object in Java has an associated monitor lock (stored in the 2-word Object Header Mark Word).
         *
         * How `synchronized` works:
         *  - Acquiring a lock: Emits bytecode `MONITORENTER`. Invalidates local cache, forcing fresh reads.
         *  - Releasing a lock: Emits bytecode `MONITOREXIT`. Flushes all modified variables to Main Memory.
         *  - Guarantees BOTH Atomicity AND Visibility!
         *
         * REENTRANCY:
         *  If a thread already holds a lock on Object A, it can enter any other synchronized
         *  block locking on Object A without blocking!
         *  The monitor maintains an internal `recursion_count` counter.
         *  - Each enter increments recursion_count.
         *  - Each exit decrements recursion_count. Lock is fully released when count reaches 0.
         *
         * JVM LOCK OPTIMIZATIONS (HotSpot):
         *  1. Biased Locking (historically biased to 1 thread; deprecated in modern JDKs).
         *  2. Lightweight Locking (uses CAS on Mark Word without OS kernel mutex).
         *  3. Heavyweight Locking (inflates to OS kernel mutex when threads actively contend).
         *  4. Lock Coarsening & Lock Elision (Escape Analysis removes locks on thread-confined objects).
         */

        class ReentrantDemonstrator {
            synchronized void outer() {
                // Thread acquires monitor on 'this' (recursion count = 1)
                inner();
            }

            synchronized void inner() {
                // Reentrant call: Re-enters monitor on 'this' without deadlocking! (recursion count = 2)
                System.out.println("   Reentrant lock successfully entered inner() method.");
            }
        }

        new ReentrantDemonstrator().outer();

        /*
         * SYNCHRONIZATION OVERHEAD IN HIGH-THROUGHPUT SYSTEMS:
         *  - Thread contention causes threads to block (BLOCKED state).
         *  - The OS must perform CONTEXT SWITCHING:
         *    Save CPU registers, flush pipeline, switch kernel stack (~1-5 microseconds overhead).
         *  - Massive throughput drop when dozens of threads contend for a single lock!
         */
    }

    // =========================================================================================
    // SECTION 5: LOCK-FREE CONCURRENCY (CAS & AtomicInteger)
    // =========================================================================================
    private static void section5_LockFreeCasAndAtomics() {
        printHeader("5. LOCK-FREE CONCURRENCY (CAS, AtomicInteger & The ABA Problem)");
        /*
         * HOW HARDWARE CAS (Compare-And-Swap) WORKS:
         *  - Supported natively by CPUs as a single hardware instruction (e.g. `LOCK CMPXCHG` on x86).
         *  - Signature: boolean compareAndSet(expectedValue, newValue)
         *  - CPU checks: "Is current value == expectedValue? If yes, store newValue and return true.
         *    If no, do not change and return false."
         *
         * OPTIMISTIC CONCURRENCY CONTROL (Lock-Free Loop):
         *   do {
         *       current = get();
         *       next = current + 1;
         *   } while (!compareAndSet(current, next));
         *
         * Benefits over `synchronized`:
         *   - NO OS CONTEXT SWITCHING: Threads never sleep or enter kernel state.
         *   - No deadlock risk: A thread cannot hold a lock and stall other threads.
         *
         * Limitation under EXTREME Contention:
         *   - If 100 threads hammer the SAME AtomicInteger simultaneously, 99 threads fail CAS
         *     and spin in busy-wait loops, causing high CPU burn and cache-line bouncing!
         */

        AtomicInteger atomicCount = new AtomicInteger(100);

        // compareAndSet demonstration:
        boolean updated = atomicCount.compareAndSet(100, 101);
        boolean failed = atomicCount.compareAndSet(100, 999); // Fails because value is now 101!
        System.out.println("CAS updated from 100 to 101: " + updated);
        System.out.println("CAS failed when expecting 100: " + failed + " (Current value: " + atomicCount.get() + ")");

        // Atomic update with functional accumulator (Java 8+):
        int doubled = atomicCount.updateAndGet(x -> x * 2);
        System.out.println("atomicCount.updateAndGet(x -> x * 2): " + doubled);

        /*
         * THE ABA PROBLEM:
         * -------------------------------------------------------------------------------------
         *  1. Thread 1 reads value A.
         *  2. Thread 2 changes value from A -> B, and then back from B -> A.
         *  3. Thread 1 resumes and performs CAS expecting A.
         *  4. CAS SUCCEEDS because the value is currently A!
         *     However, intermediate state B was missed!
         *     In lock-free linked stacks/queues, this causes node reuse corruption and dangling pointers!
         *
         * SOLUTION: AtomicStampedReference (Associates an integer stamp / version number with the reference!)
         */

        String initialRef = "Version-A";
        int initialStamp = 1;
        AtomicStampedReference<String> stampedRef = new AtomicStampedReference<>(initialRef, initialStamp);

        // Simulate Thread 2 changing A -> B -> A:
        stampedRef.compareAndSet("Version-A", "Version-B", 1, 2);
        stampedRef.compareAndSet("Version-B", "Version-A", 2, 3);

        // Thread 1 attempts CAS with original expected stamp (1):
        boolean abaCaught = stampedRef.compareAndSet("Version-A", "Version-C", 1, 4);
        System.out.println("AtomicStampedReference caught ABA: CAS succeeded? " + abaCaught
                + " (Stamp mismatched: expected 1, but was " + stampedRef.getStamp() + ")");
    }

    // =========================================================================================
    // SECTION 6: STRIPED CONCURRENCY (LongAdder & Cell Splitting)
    // =========================================================================================
    private static void section6_LongAdderAndStriping() {
        printHeader("6. STRIPED CONCURRENCY (LongAdder vs. AtomicLong)");
        /*
         * WHY LongAdder WAS INTRODUCED (Java 8 JEP 155):
         *
         * AtomicLong Bottleneck:
         *   All threads update the SAME volatile variable in memory.
         *   Every update requires invalidating that CPU cache line across all processor cores.
         *   High contention -> massive CPU cache-coherency traffic (Cache Line Bouncing).
         *
         * LongAdder Architecture (Striped 64-bit Cells):
         *   - Maintains a base value + a dynamic array of `Cell` objects.
         *   - Each thread is hashed to its own separate `Cell` in the array.
         *   - When Thread 1 and Thread 2 increment concurrently, they update DIFFERENT Cells!
         *   - ZERO LOCK CONTENTION! ZERO CAS RETRY SPINS!
         *   - When `.sum()` is called, it sums `base + sum(cells)`.
         *
         * Trade-off:
         *   - `AtomicLong`: Exact value known at all times. Use for Sequence IDs / Order Numbers.
         *   - `LongAdder`: Massive throughput for writes. `.sum()` is an eventual consistency snapshot.
         *     Ideal for metrics, telemetry, web server hit counters, analytics.
         */

        LongAdder adder = new LongAdder();
        adder.increment();
        adder.add(50);
        System.out.println("LongAdder sum: " + adder.sum());
    }

    // =========================================================================================
    // SECTION 7: LIVE BENCHMARK SUITE (COMPARING 5 COUNTER IMPLEMENTATIONS)
    // =========================================================================================
    private static void section7_LiveBenchmarkSuite() throws InterruptedException {
        printHeader("7. LIVE BENCHMARK SUITE: 5 COUNTER IMPLEMENTATIONS UNDER CONTENTION");
        /*
         * Benchmark Setup:
         *  - 16 Concurrent Threads
         *  - 250,000 increments per thread
         *  - Total Expected Operations: 4,000,000 increments!
         *
         * We measure:
         *  1. UnsafeCounter        (plain int count++)
         *  2. VolatileCounter      (volatile int count++)
         *  3. SynchronizedCounter  (synchronized method)
         *  4. AtomicCounter        (AtomicInteger CAS)
         *  5. LongAdderCounter     (LongAdder striped cells)
         */

        int threads = 16;
        int opsPerThread = 250_000;
        long totalExpected = (long) threads * opsPerThread;

        System.out.printf("Running benchmark with %d threads, %,d ops/thread (Total Expected: %,d ops)%n%n",
                threads, opsPerThread, totalExpected);

        // 1. Unsafe
        BenchmarkResult r1 = runBenchmark("1. Unsafe (plain int)", threads, opsPerThread, new Counter() {
            private int val = 0;
            @Override public void inc() { val++; }
            @Override public long get() { return val; }
        });

        // 2. Volatile
        BenchmarkResult r2 = runBenchmark("2. Volatile (volatile int)", threads, opsPerThread, new Counter() {
            private volatile int val = 0;
            @Override public void inc() { val++; }
            @Override public long get() { return val; }
        });

        // 3. Synchronized
        BenchmarkResult r3 = runBenchmark("3. Synchronized (Monitor lock)", threads, opsPerThread, new Counter() {
            private int val = 0;
            @Override public synchronized void inc() { val++; }
            @Override public synchronized long get() { return val; }
        });

        // 4. AtomicInteger (CAS)
        BenchmarkResult r4 = runBenchmark("4. AtomicInteger (Lock-free CAS)", threads, opsPerThread, new Counter() {
            private final AtomicInteger val = new AtomicInteger();
            @Override public void inc() { val.incrementAndGet(); }
            @Override public long get() { return val.get(); }
        });

        // 5. LongAdder (Striped Cells)
        BenchmarkResult r5 = runBenchmark("5. LongAdder (Striped 64-bit cells)", threads, opsPerThread, new Counter() {
            private final LongAdder val = new LongAdder();
            @Override public void inc() { val.increment(); }
            @Override public long get() { return val.sum(); }
        });

        // Display Summary Table
        System.out.println("===================================================================================================");
        System.out.printf("%-35s | %-12s | %-12s | %-10s | %-10s | %-14s%n",
                "Implementation", "Final Count", "Expected", "ThreadSafe", "Time (ms)", "Ops/Second");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (BenchmarkResult res : List.of(r1, r2, r3, r4, r5)) {
            boolean safe = res.finalCount == totalExpected;
            double opsPerSec = (totalExpected / (double) Math.max(res.elapsedMs, 1)) * 1000.0;
            System.out.printf("%-35s | %,12d | %,12d | %-10s | %8d ms | %,14.0f%n",
                    res.name, res.finalCount, totalExpected, safe ? "YES (PASS)" : "NO (CORRUPT)", res.elapsedMs, opsPerSec);
        }
        System.out.println("===================================================================================================");
        System.out.println("Takeaways from Benchmark:");
        System.out.println(" - Plain & Volatile fail completely on multi-threaded correctness.");
        System.out.println(" - Synchronized guarantees correctness, but suffers from lock-blocking overhead.");
        System.out.println(" - AtomicInteger is lock-free, but suffers from memory-bus cache bouncing under 16 threads.");
        System.out.println(" - LongAdder shatters contention by striping increments across multiple CPU core cache lines!");
    }

    interface Counter {
        void inc();
        long get();
    }

    record BenchmarkResult(String name, long finalCount, long elapsedMs) {}

    private static BenchmarkResult runBenchmark(String name, int threads, int opsPerThread, Counter counter)
            throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            Thread.ofPlatform().start(() -> {
                try {
                    startGate.await(); // All threads start simultaneously
                    for (int j = 0; j < opsPerThread; j++) {
                        counter.inc();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    endGate.countDown();
                }
            });
        }

        long start = System.currentTimeMillis();
        startGate.countDown(); // Release the gates!
        endGate.await();       // Wait for all threads to finish
        long elapsed = System.currentTimeMillis() - start;

        return new BenchmarkResult(name, counter.get(), elapsed);
    }

    // =========================================================================================
    // SECTION 8: FALSE SHARING & HIGH-THROUGHPUT DESIGN RULES
    // =========================================================================================
    private static void section8_FalseSharingAndArchitectureRules() {
        printHeader("8. FALSE SHARING (CACHE LINE CONTENTION) & ARCHITECTURAL RULES");
        /*
         * FALSE SHARING EXPLAINED:
         * -------------------------------------------------------------------------------------
         *  - CPUs read and write memory in chunks called CACHE LINES (typically 64 bytes).
         *  - Suppose Thread 1 only modifies `variableA`, and Thread 2 only modifies `variableB`.
         *  - If `variableA` and `variableB` sit adjacent to each other in memory, they land inside
         *    the SAME 64-byte Cache Line!
         *  - When Thread 1 modifies `variableA`, the CPU marks the ENTIRE cache line as dirty,
         *    forcing Thread 2's core to invalidate its L1/L2 cache and reload from RAM!
         *  - Result: The two threads slow each other down drastically, even though they NEVER touch
         *    the same variable! This is "False Sharing".
         *
         * Solution:
         *  1. Cache Line Padding (Adding dummy variables to push data into separate cache lines).
         *  2. Java's `@jdk.internal.vm.annotation.Contended` annotation (used internally by LongAdder
         *     and Thread to pad cells automatically to 128 bytes).
         *
         * GOLDEN RULES FOR HIGH-THROUGHPUT CONCURRENT ARCHITECTURES:
         * -------------------------------------------------------------------------------------
         * 1. Immutability First: Immutable objects need ZERO synchronization and have no JMM hazards.
         * 2. Thread Confinement: The fastest lock is NO lock! Confine data to a single thread
         *    (ThreadLocal or actor-based single-thread event loops like Netty/Node).
         * 3. Favor Lock-Free Data Structures: Use ConcurrentHashMap, ConcurrentLinkedQueue,
         *    LongAdder over manual synchronized blocks.
         * 4. Keep Synchronized Blocks Microscopic: Never perform I/O, database queries, or HTTP calls
         *    inside a synchronized block or lock!
         * 5. Read vs. Write Segregation: If reads outnumber writes by 100:1, use `ReentrantReadWriteLock`
         *    or `StampedLock` for optimistic reads.
         */

        System.out.println("High-Throughput Concurrency Rules:");
        System.out.println(" 1. Immutability > Confinement > Lock-free CAS > Striped > Synchronized locks.");
        System.out.println(" 2. Beware False Sharing when placing independent volatile fields in the same class.");
        System.out.println(" 3. LongAdder for metrics/telemetry; AtomicLong for sequence IDs.");
        System.out.println(" 4. Never perform I/O inside a critical section!");
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


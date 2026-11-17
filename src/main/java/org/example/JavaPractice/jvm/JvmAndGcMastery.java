package org.example.JavaPractice.jvm;

import java.lang.management.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * =============================================================================================
 *          MASTERCLASS: JVM ARCHITECTURE, MEMORY MANAGEMENT & GARBAGE COLLECTION
 * =============================================================================================
 *
 * This file serves as an end-to-end, runnable guide, diagnostic suite, and architectural reference
 * for mastering the Java Virtual Machine (JVM), Memory Layout, Garbage Collection algorithms,
 * and production troubleshooting:
 *
 * TABLE OF CONTENTS:
 *  1. JVM Architecture & Subsystems (ClassLoader, Tiered JIT Compilation C1/C2, Execution Engine)
 *  2. JVM Memory Layout (Heap, Young Gen, Old Gen, Metaspace, Thread Stacks, Code Cache)
 *  3. Object Allocation Mechanics (TLAB, Bump-Pointer & Escape Analysis / Scalar Replacement)
 *  4. Garbage Collection Fundamentals (GC Roots, Mark-Sweep-Compact, Safepoints & Stop-The-World)
 *  5. Modern GC Algorithms Compared (Serial, Parallel, G1 GC, and Java 21 Generational ZGC)
 *  6. The 5 Types of OutOfMemoryError (OOM) & Root Cause Analysis
 *  7. Live JVM Memory Diagnostics & MXBean Inspection (Heap, Metaspace, GC Pauses)
 *  8. Live Simulation: Memory Leak Detection & Diagnosis (Growing Collections Pattern)
 *  9. Live Simulation: StackOverflowError Mechanics & Safe Recovery
 * 10. Production JVM Tuning Flags, Container Sizing & Unified GC Logging (-Xlog:gc*)
 * =============================================================================================
 */
public class JvmAndGcMastery {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("   JVM ARCHITECTURE, MEMORY MANAGEMENT & GARBAGE COLLECTION (GC)   ");
        System.out.println("===================================================================\n");

        section1_JvmArchitectureAndJit();
        section2_JvmMemoryLayout();
        section3_ObjectAllocationAndEscapeAnalysis();
        section4_GcFundamentalsAndRoots();
        section5_ModernGcAlgorithmsComparison();
        section6_OutOfMemoryErrorTypes();
        section7_LiveJvmDiagnostics();
        section8_MemoryLeakSimulation();
        section9_StackOverflowSimulation();
        section10_ProductionJvmFlags();

        System.out.println("\n===================================================================");
        System.out.println("                ALL JVM & GC LESSONS COMPLETE!                     ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: JVM ARCHITECTURE & SUBSYSTEMS
    // =========================================================================================
    private static void section1_JvmArchitectureAndJit() {
        printHeader("1. JVM ARCHITECTURE & SUBSYSTEMS (CLASSLOADER & TIERED JIT)");
        /*
         * THE 3 MAJOR SUBSYSTEMS OF THE JVM:
         * -------------------------------------------------------------------------------------
         *  1. CLASSLOADER SUBSYSTEM:
         *     - Loading: Reads .class bytecode into memory.
         *       - Bootstrap ClassLoader : Core Java runtime (java.base). Written in native C/C++.
         *       - Platform ClassLoader  : Extension/platform modules (java.sql, java.net.http).
         *       - Application ClassLoader: Application classpath and user dependencies.
         *     - Linking:
         *       - Verification: Checks bytecode integrity and security boundaries.
         *       - Preparation: Allocates memory for static fields and initializes to defaults.
         *       - Resolution: Replaces symbolic references with direct memory references.
         *     - Initialization: Executes static blocks and static field initializers (<clinit>).
         *
         *  2. RUNTIME DATA AREAS:
         *     - Heap, Metaspace, Code Cache, Thread Stacks, PC Registers, Native Stacks.
         *
         *  3. EXECUTION ENGINE:
         *     - Interpreter: Interprets bytecode line-by-line (starts fast, runs slower).
         *     - JIT Compiler (Just-In-Time): Compiles frequently executed "hot" bytecode into native CPU machine code.
         *       - C1 (Client Compiler): Quick compilation, basic optimizations (inlining, profiling).
         *       - C2 (Server Compiler): Heavy tiered compilation with aggressive global optimizations:
         *         - Method Inlining (removes method call overhead)
         *         - Loop Unrolling (reduces loop branch checks)
         *         - Escape Analysis (allocates objects on the stack or into registers!)
         *         - Dead Code Elimination
         *     - Garbage Collector: Reclaims unreferenced heap memory.
         */

        System.out.println("ClassLoader Hierarchy for current class:");
        ClassLoader appClassLoader = JvmAndGcMastery.class.getClassLoader();
        ClassLoader platformClassLoader = appClassLoader.getParent();
        ClassLoader bootstrapClassLoader = platformClassLoader.getParent(); // Null represents native Bootstrap!

        System.out.println(" - Application ClassLoader: " + appClassLoader.getClass().getName());
        System.out.println(" - Platform ClassLoader:    " + platformClassLoader.getClass().getName());
        System.out.println(" - Bootstrap ClassLoader:   " + bootstrapClassLoader + " (Native C++)");
    }

    // =========================================================================================
    // SECTION 2: JVM MEMORY LAYOUT (HEAP, METASPACE, STACKS)
    // =========================================================================================
    private static void section2_JvmMemoryLayout() {
        printHeader("2. JVM MEMORY LAYOUT (HEAP, METASPACE, THREAD STACKS)");
        /*
         *  JVM RUNTIME MEMORY DIVISION:
         * -------------------------------------------------------------------------------------
         *  THREAD-SHARED MEMORY:
         *   1. HEAP MEMORY:
         *      - YOUNG GENERATION (Eden, Survivor 0 / From, Survivor 1 / To):
         *        - Where new objects are initially allocated.
         *        - Minor GC collects Eden and copies survivors between S0 and S1.
         *      - OLD / TENURED GENERATION:
         *        - Stores objects that have survived multiple GC cycles (exceeded tenuring threshold).
         *        - Major GC / Full GC cleans this area.
         *   2. METASPACE (Java 8+ Native Memory):
         *      - Replaced the old "PermGen" (Permanent Generation).
         *      - Stores class metadata, bytecode definitions, method tables, and interned strings pool.
         *      - Resides in OFF-HEAP native OS memory! Expands automatically unless capped.
         *   3. CODE CACHE:
         *      - Stores native CPU machine code compiled by JIT (C1/C2).
         *
         *  THREAD-PRIVATE MEMORY:
         *   1. THREAD STACK (-Xss, default 1MB on 64-bit JVM):
         *      - Each thread has its own stack containing Stack Frames:
         *        - Local Variable Table (LVT)
         *        - Operand Stack
         *        - Frame Data (Reference to Runtime Constant Pool, method return info)
         *   2. PROGRAM COUNTER (PC) REGISTER:
         *      - Stores address of currently executing JVM instruction.
         *   3. NATIVE METHOD STACK:
         *      - For C/C++ JNI native library calls.
         * -------------------------------------------------------------------------------------
         */

        System.out.println("Key Memory Principles:");
        System.out.println(" - Heap: Dynamic object storage managed by Garbage Collection.");
        System.out.println(" - Metaspace: Native OS memory for class definitions (no PermGen since Java 8).");
        System.out.println(" - Thread Stack: Fast LIFO execution frames, private to each thread.");
    }

    // =========================================================================================
    // SECTION 3: OBJECT ALLOCATION & ESCAPE ANALYSIS
    // =========================================================================================
    private static void section3_ObjectAllocationAndEscapeAnalysis() {
        printHeader("3. OBJECT ALLOCATION, TLAB & ESCAPE ANALYSIS");
        /*
         * HOW JAVA ALLOCATES OBJECTS IN MILLISECONDS:
         *
         *  1. TLAB (Thread-Local Allocation Buffer):
         *     - If every thread allocated directly into the shared Eden space, synchronized locks
         *       would bottleneck throughput.
         *     - SOLUTION: Each thread gets a private chunk of Eden space called a TLAB.
         *     - Allocation is LOCK-FREE: The thread simply increments an internal pointer
         *       (Bump-the-Pointer technique) in ~2 nanoseconds!
         *
         *  2. ESCAPE ANALYSIS & SCALAR REPLACEMENT (C2 Optimization):
         *     - JIT analyzes if an object escapes the method where it is instantiated:
         *       - GlobalEscape: Object escapes method and thread (e.g. stored in static field or returned).
         *       - ArgEscape: Passed as argument to other methods, but does not escape thread.
         *       - NoEscape: Object is strictly confined to the local method!
         *     - If NoEscape is proven:
         *       - The JVM DOES NOT allocate the object on the Heap!
         *       - Instead, it performs SCALAR REPLACEMENT: breaks the object into primitive local
         *         variables placed in CPU registers or on the stack!
         *       - ZERO GC OVERHEAD when the method returns!
         */

        System.out.println("Escape Analysis Concept Demonstration:");
        long start = System.nanoTime();
        long sum = 0;
        // In the loop below, Point objects DO NOT escape the method!
        // C2 JIT eliminates heap allocations via Scalar Replacement!
        for (int i = 0; i < 1_000_000; i++) {
            Point p = new Point(i, i * 2);
            sum += p.x() + p.y();
        }
        long durationUs = (System.nanoTime() - start) / 1000;
        System.out.printf("1,000,000 non-escaping objects computed in %,d us (Zero GC allocations!)%n", durationUs);
        System.out.println("Computed sum: " + sum);
    }

    public record Point(int x, int y) {}

    // =========================================================================================
    // SECTION 4: GARBAGE COLLECTION FUNDAMENTALS & GC ROOTS
    // =========================================================================================
    private static void section4_GcFundamentalsAndRoots() {
        printHeader("4. GC FUNDAMENTALS, GC ROOTS & REACHABILITY");
        /*
         * HOW DOES GC DETERMINE WHAT IS TRASH?
         *  - Reference Counting (NOT used in JVM due to circular reference leaks: A -> B -> A).
         *  - Tracing Garbage Collection via GC ROOTS (Root Reachability Graph):
         *    The GC traverses all references starting from GC Roots.
         *    Any object reachable from a root is ALIVE; all other objects are DEAD and collected!
         *
         * WHAT QUALIFIES AS A GC ROOT?
         *  1. Local variables in active thread stack frames.
         *  2. Active Thread objects themselves.
         *  3. Static variables referenced by loaded classes.
         *  4. JNI (Native) local and global C references.
         *  5. Objects used as synchronization monitors.
         *
         * THE 3 CLASSIC GC ALGORITHM PHASES:
         *  1. Mark    : Identify and flag all live reachable objects starting from GC roots.
         *  2. Sweep   : Free memory occupied by unflagged (dead) objects. (Causes fragmentation).
         *  3. Compact : Slide live objects to the beginning of the heap into a contiguous block,
         *               eliminating fragmentation so allocation remains fast (Bump-the-pointer).
         */

        System.out.println("GC Root Tracing Principles:");
        System.out.println(" - Reachable objects are preserved; unreachable circular graphs are collected safely.");
        System.out.println(" - Stop-The-World (STW): Application threads pause at Safepoints during critical phases.");
    }

    // =========================================================================================
    // SECTION 5: MODERN GC ALGORITHMS COMPARED
    // =========================================================================================
    private static void section5_ModernGcAlgorithmsComparison() {
        printHeader("5. MODERN GC ALGORITHMS COMPARISON");
        /*
         *  GC ALGORITHM COMPARISON MATRIX:
         *  =====================================================================================
         *  Collector     | Flag                  | Target Metric      | Heap Sizing    | Notes
         *  --------------|-----------------------|--------------------|----------------|-------
         *  Serial GC     | -XX:+UseSerialGC      | Footprint / Single | Small (<100MB) | Simple, single-threaded STW
         *  Parallel GC   | -XX:+UseParallelGC    | Max Throughput     | Medium         | Best for batch processing / reports
         *  G1 GC         | -XX:+UseG1GC          | Balanced (Default) | 4GB - 64GB     | Region-based, predictable pauses
         *  ZGC           | -XX:+UseZGC           | Ultra-Low Latency  | 16GB - 16TB    | Sub-millisecond pauses (<1ms)
         *  GenZGC (J21)  | -XX:+ZGenerational    | Low Latency + Gen  | 16GB - 16TB    | Young/Old generation aware ZGC!
         *  Shenandoah    | -XX:+UseShenandoahGC  | Ultra-Low Latency  | Medium - Large | Red Hat concurrent evacuation
         *  =====================================================================================
         *
         *  HOW G1 GC WORKS (Default since Java 9):
         *   - Partitions the heap into ~2,048 equal regions (1MB to 32MB each).
         *   - Each region dynamically acts as Eden, Survivor, or Old.
         *   - Collects regions with the highest amount of garbage first ("Garbage-First").
         *   - Soft real-time pause target: `-XX:MaxGCPauseMillis=200`
         *
         *  HOW ZGC ACHIEVES <1ms PAUSES:
         *   - Performs marking, relocation, and pointer remapping CONCURRENTLY while application runs!
         *   - Uses COLORED POINTERS (metadata stored in spare reference bits) and LOAD BARRIERS.
         *   - Java 21 introduced Generational ZGC (`-XX:+ZGenerational`), slashing CPU overhead by 70%!
         */

        System.out.println("Quick GC Selection Guide:");
        System.out.println(" - Default for general microservices: G1 GC (-XX:+UseG1GC)");
        System.out.println(" - Ultra-low latency (<1ms pause requirements): Java 21 Generational ZGC (-XX:+UseZGC -XX:+ZGenerational)");
        System.out.println(" - Batch computing / high-throughput non-interactive jobs: Parallel GC (-XX:+UseParallelGC)");
    }

    // =========================================================================================
    // SECTION 6: THE 5 TYPES OF OutOfMemoryError (OOM)
    // =========================================================================================
    private static void section6_OutOfMemoryErrorTypes() {
        printHeader("6. THE 5 TYPES OF OutOfMemoryError (OOM) & DIAGNOSIS");
        /*
         *  1. java.lang.OutOfMemoryError: Java heap space
         *     - Cause: Heap is full. GC cannot reclaim enough memory to allocate a new object.
         *     - Culprits: Memory leaks (static collections, un-removed ThreadLocals, unclosed streams).
         *     - Remedy: Capture Heap Dump (`-XX:+HeapDumpOnOutOfMemoryError`) and inspect via Eclipse MAT.
         *
         *  2. java.lang.OutOfMemoryError: Metaspace
         *     - Cause: Native memory allocated for loaded class metadata is exhausted.
         *     - Culprits: Excessive dynamic class generation (CGLIB, ByteBuddy, un-collected ClassLoaders).
         *     - Remedy: Increase `-XX:MaxMetaspaceSize` or eliminate ClassLoader leaks.
         *
         *  3. java.lang.OutOfMemoryError: Unable to create new native thread
         *     - Cause: JVM cannot allocate a new OS kernel thread (OS thread limit or RAM exhausted).
         *     - Culprits: Creating un-pooled threads (`new Thread()`) in loops, thread leaks.
         *     - Remedy: Use ThreadPoolExecutor with bounded queues, or switch to Java 21 Virtual Threads!
         *
         *  4. java.lang.OutOfMemoryError: Direct buffer memory
         *     - Cause: Off-heap memory allocated via `ByteBuffer.allocateDirect()` exceeded `-XX:MaxDirectMemorySize`.
         *     - Culprits: Netty / NIO off-heap buffers leaked without deallocation.
         *
         *  5. java.lang.StackOverflowError (Subclass of VirtualMachineError):
         *     - Cause: Thread stack space (`-Xss`) exhausted due to runaway recursion or circular method calls.
         */

        System.out.println("OOM Diagnostic Strategy:");
        System.out.println(" 1. Always enable: -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/dumps/");
        System.out.println(" 2. Differentiate heap leaks from off-heap / Metaspace / native thread leaks.");
    }

    // =========================================================================================
    // SECTION 7: LIVE JVM MEMORY DIAGNOSTICS & MXBEANS
    // =========================================================================================
    private static void section7_LiveJvmDiagnostics() {
        printHeader("7. LIVE JVM MEMORY DIAGNOSTICS & MXBEAN INSPECTION");

        // 1. Runtime memory inspection:
        Runtime rt = Runtime.getRuntime();
        long mb = 1024 * 1024;
        long totalMemory = rt.totalMemory() / mb;
        long freeMemory = rt.freeMemory() / mb;
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = rt.maxMemory() / mb;

        System.out.printf("Runtime Heap Metrics: Used: %d MB | Total: %d MB | Max Allowed: %d MB%n",
                usedMemory, totalMemory, maxMemory);

        // 2. MemoryMXBean inspection (Heap vs Non-Heap / Metaspace):
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memBean.getNonHeapMemoryUsage();

        System.out.printf("MemoryMXBean Heap:     Used %d MB / Max %d MB%n",
                heapUsage.getUsed() / mb, heapUsage.getMax() / mb);
        System.out.printf("MemoryMXBean Non-Heap: Used %d MB / Committed %d MB (Metaspace/CodeCache)%n",
                nonHeapUsage.getUsed() / mb, nonHeapUsage.getCommitted() / mb);

        // 3. GarbageCollectorMXBeans inspection:
        System.out.println("\nActive Garbage Collectors in current JVM:");
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : gcBeans) {
            System.out.printf("   Collector: %-25s | Total Collections: %4d | Total Time: %d ms%n",
                    gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
    }

    // =========================================================================================
    // SECTION 8: LIVE SIMULATION: MEMORY LEAK DIAGNOSIS
    // =========================================================================================
    private static void section8_MemoryLeakSimulation() {
        printHeader("8. LIVE SIMULATION: MEMORY LEAK DETECTION (GROWING COLLECTION)");
        /*
         * WHAT IS A JAVA MEMORY LEAK?
         * Unlike C++, where a leak is un-freed allocated memory without a pointer,
         * in Java a Memory Leak is an UNINTENDED STRONG REFERENCE to an object that is
         * no longer needed by business logic, preventing the Garbage Collector from freeing it!
         */

        System.out.println("Simulating an unintentional cache accumulation memory leak...");
        class MemoryLeakDemo {
            // Simulated static cache that never purges old entries:
            final List<byte[]> leakedCache = new ArrayList<>();

            void simulateLeakingEvents(int count) {
                for (int i = 0; i < count; i++) {
                    // Allocate 100 KB chunks:
                    leakedCache.add(new byte[100 * 1024]);
                }
            }

            void clear() {
                leakedCache.clear();
            }
        }

        MemoryLeakDemo demo = new MemoryLeakDemo();
        long mb = 1024 * 1024;
        long memBefore = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;

        demo.simulateLeakingEvents(100); // Allocate ~10 MB of leaked memory
        long memAfter = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;

        System.out.printf("Memory before accumulation: %d MB | After: %d MB (Retained size: %d entries)%n",
                memBefore, memAfter, demo.leakedCache.size());

        // Releasing references allows GC to collect:
        demo.clear();
        System.gc(); // Hint JVM to run GC
        long memAfterGc = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;
        System.out.printf("Memory after clearing collection and GC hint: %d MB%n", memAfterGc);
        System.out.println("Takeaway: Always bound caches (e.g. Caffeine/LRU) or use WeakReference!");
    }

    // =========================================================================================
    // SECTION 9: LIVE SIMULATION: StackOverflowError
    // =========================================================================================
    private static void section9_StackOverflowSimulation() {
        printHeader("9. LIVE SIMULATION: StackOverflowError MECHANICS");

        System.out.println("Demonstrating StackOverflowError via runaway recursion with safe recovery:");
        try {
            recursiveCall(0);
        } catch (StackOverflowError e) {
            System.out.println("   [CAUGHT] java.lang.StackOverflowError!");
            System.out.println("   Cause: Stack frames exceeded thread stack memory limit (-Xss).");
            System.out.println("   Fix: Use iteration, Tail-Call Trampolines, or increase -Xss.");
        }
    }

    private static void recursiveCall(int depth) {
        if (depth > 20_000) return; // Safeguard if stack is exceptionally large
        recursiveCall(depth + 1);
    }

    // =========================================================================================
    // SECTION 10: PRODUCTION JVM TUNING FLAGS & CONTAINER SIZING
    // =========================================================================================
    private static void section10_ProductionJvmFlags() {
        printHeader("10. PRODUCTION JVM TUNING FLAGS & CONTAINER SIZING");
        /*
         *  CRITICAL PRODUCTION JVM FLAGS (JAVA 17 - 21):
         * -------------------------------------------------------------------------------------
         *  1. HEAP SIZING IN CONTAINERS (KUBERNETES / DOCKER):
         *     - DO NOT use hardcoded -Xmx4g in containers!
         *     - Use container percentage flags:
         *         -XX:+UseContainerSupport
         *         -XX:MaxRAMPercentage=75.0
         *         -XX:InitialRAMPercentage=75.0
         *       (Leaves 25% for OS, Metaspace, Thread Stacks, and Direct Buffers to prevent OOMKilled!).
         *
         *  2. ZERO-DOWNTIME CRASH ANALYSIS:
         *     -XX:+HeapDumpOnOutOfMemoryError
         *     -XX:HeapDumpPath=/var/log/java/heapdump-%p.hprof
         *     -XX:+ExitOnOutOfMemoryError (Terminates unhealthy pod so Kubernetes can restart it cleanly!).
         *
         *  3. UNIFIED GC LOGGING (JAVA 9+):
         *     -Xlog:gc*,gc+phases=debug:file=/var/log/gc.log:time,uptime,pid:filecount=5,filesize=100M
         *
         *  4. LOW-LATENCY ZGC ENABLER (JAVA 21):
         *     -XX:+UseZGC -XX:+ZGenerational
         */

        System.out.println("Recommended Golden JVM Command Line for Containerized Services:");
        System.out.println("  java -XX:+UseContainerSupport \\");
        System.out.println("       -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=75.0 \\");
        System.out.println("       -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \\");
        System.out.println("       -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumps/ \\");
        System.out.println("       -XX:+ExitOnOutOfMemoryError \\");
        System.out.println("       -Xlog:gc*:file=/var/log/gc.log:time,uptime:filecount=5,filesize=50M \\");
        System.out.println("       -jar app.jar");
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


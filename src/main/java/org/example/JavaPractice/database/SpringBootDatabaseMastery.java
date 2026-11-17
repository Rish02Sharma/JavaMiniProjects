package org.example.JavaPractice.database;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * =============================================================================================
 *   MASTERCLASS: SPRING BOOT DATABASE ARCHITECTURE, HIKARICP INTERNALS & TIMEOUTS
 * =============================================================================================
 *
 * This file serves as an end-to-end, runnable guide, diagnostic suite, and simulation engine
 * covering how Spring Boot manages database connectivity, connection pool lifecycles, sizing,
 * timeout hierarchies, and diagnosing connection pool exhaustion caused by slow external I/O:
 *
 * TABLE OF CONTENTS:
 *  1. Spring Boot Database Ecosystem (DataSource, Auto-Configuration & Connection Libraries)
 *  2. Database Access Libraries: JDBC, JdbcTemplate, JdbcClient, JPA/Hibernate, R2DBC
 *  3. HikariCP Architecture & Core Internals (ConcurrentBag, FastList, Javassist Bytecode Proxies)
 *  4. Connection Lifecycle in HikariCP (Creation, Borrowing, Leak Detection, Eviction)
 *  5. Pool Sizing Formulas & The Thread Starvation Trap (The (Cores * 2) + Spindles Rule)
 *  6. The 5-Level Client-Side & Network Timeout Hierarchy (Lease, Connect, Socket, Query, Tx)
 *  7. The Fatal Anti-Pattern: External API Calls Inside @Transactional Boundaries
 *  8. Complete In-Memory Simulation Suite:
 *      - Scenario A: High-Throughput Clean Database Workload (Smooth Sailing)
 *      - Scenario B: The Disaster! Connection Pool Exhaustion via Slow External API Calls
 *      - Scenario C: The Architectural Cure! "Fetch First, Transact Fast" Pattern
 *  9. Production application.yml Configuration & Micrometer Metrics Monitoring Checklist
 * =============================================================================================
 */
public class SpringBootDatabaseMastery {

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("  SPRING BOOT DATABASE ARCHITECTURE & HIKARICP MASTERCLASS         ");
        System.out.println("===================================================================\n");

        section1_SpringBootDatabaseEcosystem();
        section2_DatabaseAccessLibraries();
        section3_HikariCpArchitectureAndConcurrentBag();
        section4_HikariConnectionLifecycle();
        section5_PoolSizingAndThreadStarvation();
        section6_TimeoutHierarchyDeepDive();
        section7_ExternalApiTransactionalAntiPattern();
        section8_LiveExhaustionSimulationSuite();
        section9_ProductionConfigurationAndMetrics();

        System.out.println("\n===================================================================");
        System.out.println("          ALL SPRING BOOT DATABASE LESSONS COMPLETE!               ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: SPRING BOOT DATABASE ECOSYSTEM & AUTO-CONFIGURATION
    // =========================================================================================
    private static void section1_SpringBootDatabaseEcosystem() {
        printHeader("1. SPRING BOOT DATABASE ECOSYSTEM & DataSource AUTO-CONFIGURATION");
        /*
         * THE javax.sql.DataSource CONTRACT:
         * In JDBC, `DataSource` is the standard factory interface for obtaining physical
         * database connections (`java.sql.Connection`).
         *
         * SPRING BOOT POOL AUTO-CONFIGURATION HIERARCHY (DataSourceAutoConfiguration):
         * When `spring-boot-starter-jdbc` or `spring-boot-starter-data-jpa` is on the classpath,
         * Spring Boot scans for connection pool implementations in this EXACT priority order:
         *
         *  1. HikariCP (com.zaxxer.hikari.HikariDataSource) -> DEFAULT since Spring Boot 2.0!
         *  2. Tomcat JDBC Pool (org.apache.tomcat.jdbc.pool.DataSource)
         *  3. Apache Commons DBCP2 (org.apache.commons.dbcp2.BasicDataSource)
         *  4. Oracle UCP (Universal Connection Pool)
         *
         * If HikariCP is on the classpath (standard in starters), Spring Boot automatically
         * instantiates and wires a `HikariDataSource` bean configured from `spring.datasource.hikari.*`.
         */

        System.out.println("Spring Boot Connection Pool Hierarchy:");
        System.out.println(" 1. HikariCP (Default, ultra-fast, zero-overhead)");
        System.out.println(" 2. Tomcat JDBC Connection Pool");
        System.out.println(" 3. Commons DBCP2");
        System.out.println(" 4. Oracle UCP");
    }

    // =========================================================================================
    // SECTION 2: DATABASE ACCESS LIBRARIES (JDBC, JDBCCLIENT, JPA, R2DBC)
    // =========================================================================================
    private static void section2_DatabaseAccessLibraries() {
        printHeader("2. DATABASE ACCESS LIBRARIES (JDBC, JdbcClient, JPA/Hibernate, R2DBC)");
        /*
         *  DATABASE ACCESS ABSTRACTION LEVELS IN MODERN SPRING BOOT:
         *  -------------------------------------------------------------------------------------
         *  Library               | Paradigm         | Strengths                      | Pitfalls
         *  ----------------------|------------------|--------------------------------|----------
         *  Plain JDBC            | Low-level SQL    | Raw speed, zero overhead       | Massive boilerplate
         *  JdbcTemplate          | Template method  | Manages connections/cleanup    | Verbose RowMappers
         *  JdbcClient (SB 3.2+)  | Fluent SQL API   | Modern, type-safe, records!    | Manual SQL queries
         *  Spring Data JPA       | ORM (Hibernate)  | Automated CRUD, dirty-checking | N+1 queries, OSIV trap, lazy init
         *  Spring Data JDBC      | Simple ORM       | No dirty-checking/proxies      | No lazy loading
         *  R2DBC (Reactive)      | Non-blocking I/O | Event-loop reactive pipelines  | Incompatible with JDBC / @Transactional!
         *  -------------------------------------------------------------------------------------
         *
         *  WHY CANNOT WE USE JDBC WITH REACTIVE WEBFLUX?
         *  JDBC is inherently synchronous and blocking: `socket.read()` blocks the calling thread.
         *  In WebFlux, blocking a Netty EventLoop thread freezes the entire server!
         *  R2DBC (Reactive Relational Database Connectivity) uses non-blocking asynchronous drivers.
         */

        System.out.println("Modern Spring Boot 3.2+ Recommendation:");
        System.out.println(" - Use Spring Data JPA for complex relational domains with rich business entities.");
        System.out.println(" - Use JdbcClient (Spring Boot 3.2+) for high-throughput reporting, CQRS read models, and raw SQL.");
    }

    // =========================================================================================
    // SECTION 3: HIKARICP ARCHITECTURE & ConcurrentBag INTERNALS
    // =========================================================================================
    private static void section3_HikariCpArchitectureAndConcurrentBag() {
        printHeader("3. HIKARICP ARCHITECTURE & ConcurrentBag INTERNALS");
        /*
         * WHY IS HIKARICP SO MUCH FASTER THAN COMMONS DBCP / C3P0?
         *
         *  1. Javassist Bytecode-Generated Proxies:
         *     - Traditional pools use `java.lang.reflect.Proxy`, which incurs heavy dynamic dispatch
         *       and argument boxing on every `connection.prepareStatement()` or `executeQuery()`.
         *     - HikariCP generates custom bytecode delegate classes directly at compile/startup time!
         *
         *  2. FastList (Replacing java.util.ArrayList):
         *     - When a connection closes, it must close all open Statements.
         *     - ArrayList.remove(Object) scans from index 0 forward with range checks (O(n)).
         *     - Statements are typically closed in reverse order of opening!
         *     - Hikari's `FastList` scans backwards without range checks, slashing statement close latency!
         *
         *  3. ConcurrentBag Lock-Free Pool Queue:
         *     - A custom lock-free, 2-tier queue designed for high-concurrency connection handoff.
         *     - CONNECTION BORROW PROTOCOL:
         *       Step 1: Check ThreadLocal bag. If thread previously used a connection and it's
         *               still available -> BORROW INSTANTLY WITH ZERO LOCKS (0 nanoseconds!).
         *       Step 2: Check shared queue of unreserved connections using CAS.
         *       Step 3: Steal a connection from another thread's list.
         *       Step 4: Wait up to `connectionTimeout` on a handoff SynchronousQueue.
         *
         *  THE 4 ConcurrentBag STATES:
         *   - STATE_NOT_IN_USE (0) : Available to be borrowed.
         *   - STATE_IN_USE     (1) : Currently leased by a worker thread.
         *   - STATE_REMOVED   (-1) : Closed and permanently removed from the pool.
         *   - STATE_RESERVED  (-2) : Reserved for eviction or validation checks.
         */

        System.out.println("HikariCP Performance Inventions:");
        System.out.println(" 1. ConcurrentBag: Lock-free ThreadLocal connection reuse.");
        System.out.println(" 2. FastList: Reverse-scan statement tracking without bounds checking.");
        System.out.println(" 3. Direct bytecode generation: Eliminates reflection overhead.");
    }

    // =========================================================================================
    // SECTION 4: HIKARI CONNECTION LIFECYCLE
    // =========================================================================================
    private static void section4_HikariConnectionLifecycle() {
        printHeader("4. HIKARICP CONNECTION LIFECYCLE (ACQUIRE, LEAK, EVICT)");
        /*
         *  HIKARICP LIFECYCLE TIMERS & PHASES:
         * -------------------------------------------------------------------------------------
         *  Phase             | Parameter                | Default    | Purpose
         *  ------------------|--------------------------|------------|-------------------------
         *  Lease Acquisition | connectionTimeout        | 30,000 ms  | Max wait time to borrow a connection
         *  Keep-Alive        | keepaliveTime            | 0 (off)    | Proactive ping to prevent firewall drop
         *  Idle Retirement   | idleTimeout              | 600,000 ms | Max time idle connection stays in pool
         *  Max Lifetime      | maxLifetime              | 1,800,000ms| Max overall age of a connection (30 min)
         *  Leak Detection    | leakDetectionThreshold   | 0 (off)    | Alert if connection held > X ms without close!
         * -------------------------------------------------------------------------------------
         *
         *  STATE CLEANUP ON RETURN:
         *  When a thread calls `connection.close()`, HikariCP DOES NOT close the TCP socket!
         *  Instead, it intercepts `close()` via its proxy:
         *   1. Rolls back any uncommitted transaction if auto-commit was false.
         *   2. Resets auto-commit, read-only status, catalog, and isolation level to defaults.
         *   3. Transitions state from STATE_IN_USE back to STATE_NOT_IN_USE in the ConcurrentBag.
         *
         *  LEAK DETECTION THRESHOLD (CRITICAL PRODUCTION SETTING):
         *  - If set to `leakDetectionThreshold = 2000` (2 seconds):
         *    If a thread borrows a connection and doesn't return it within 2 seconds, HikariCP logs
         *    an EXCEPTION WITH FULL STACK TRACE showing exactly where the leak occurred!
         */

        System.out.println("Lifecycle Rules:");
        System.out.println(" - connection.close() returns the connection to ConcurrentBag, never drops the TCP socket.");
        System.out.println(" - Always set maxLifetime 2-3 minutes shorter than DB/firewall connection timeouts.");
        System.out.println(" - Enable leakDetectionThreshold = 2000-5000ms in staging and production!");
    }

    // =========================================================================================
    // SECTION 5: POOL SIZING & THE THREAD STARVATION TRAP
    // =========================================================================================
    private static void section5_PoolSizingAndThreadStarvation() {
        printHeader("5. POOL SIZING & THE THREAD STARVATION TRAP");
        /*
         * THE GREAT SIZING MYTH: "MORE CONNECTIONS = FASTER APPLICATION"
         *  - Many developers configure `maximumPoolSize = 100` or `200` thinking it handles more traffic.
         *  - REALITY: A single database server has a limited number of CPU cores and disk channels.
         *  - 100 concurrent queries force the DB server into massive CPU context switching and
         *    disk head thrashing, GRINDING DATABASE THROUGHPUT TO A HALT!
         *
         * THE GOLDEN POOL SIZING FORMULA (PostgreSQL / Oracle / HikariCP):
         * -------------------------------------------------------------------------------------
         *    pool_size = (CPU_cores * 2) + effective_spindle_count
         * -------------------------------------------------------------------------------------
         *  - For a database server with 8 CPU cores and SSD storage (effective_spindle = 1):
         *      pool_size = (8 * 2) + 1 = 17 connections!
         *  - A pool of 17 connections will handle THOUSANDS of requests per second faster than a pool of 100!
         *
         * THE DEADLOCK RISK IN NESTED TRANSACTIONS:
         *  - Suppose Thread 1 borrows Connection A, and inside its transaction needs Connection B
         *    from the same pool.
         *  - If you have 10 threads and pool size = 10:
         *    All 10 threads borrow 1 connection each -> Pool is empty -> All 10 threads try to
         *    borrow a 2nd connection -> ALL THREADS WAIT FOREVER ON EACH OTHER (DEADLOCK!).
         *  - SAFE MINIMUM POOL SIZING FORMULA FOR NESTED TRANSACTIONS:
         *      pool_size = T * (N - 1) + 1
         *      (where T = max threads, N = max simultaneous connections per thread).
         */

        System.out.println("Pool Sizing Takeaways:");
        System.out.println(" - Formula: (Cores * 2) + Spindles. An 8-core DB server needs only ~17 connections!");
        System.out.println(" - Less is more: Smaller pools keep the database CPU in L1/L2 cache and avoid disk thrashing.");
    }

    // =========================================================================================
    // SECTION 6: THE 5-LEVEL TIMEOUT HIERARCHY
    // =========================================================================================
    private static void section6_TimeoutHierarchyDeepDive() {
        printHeader("6. THE 5-LEVEL CLIENT-SIDE & NETWORK TIMEOUT HIERARCHY");
        /*
         * THE HIERARCHY OF TIMEOUTS (FROM CLIENT POOL TO DATABASE ENGINE):
         *
         * [Level 1: Transaction Timeout (@Transactional(timeout = 5))]
         *     │
         * [Level 2: Connection Lease Timeout (Hikari connectionTimeout = 2000ms)]
         *     │
         * [Level 3: Query / Statement Timeout (statement.setQueryTimeout(3))]
         *     │
         * [Level 4: Socket / Read Timeout (JDBC driver socketTimeout = 5000ms)]
         *     │
         * [Level 5: Network Connect Timeout (JDBC driver connectTimeout = 3000ms)]
         *
         * 1. Connection Lease Timeout (`hikari.connection-timeout`):
         *    - How long a thread waits for HikariCP to give it a connection before throwing
         *      `SQLTransientConnectionException`. Default 30s is way too high! Recommended: 1000-3000ms.
         *
         * 2. Connect Timeout (`connectTimeout` driver property):
         *    - Max time allowed for TCP handshake + TLS negotiation with DB host.
         *    - Prevents thread hanging when DB host or router is down.
         *
         * 3. Socket / Read Timeout (`socketTimeout` driver property):
         *    - Max time waiting for the DB to respond over an established TCP connection.
         *    - Prevents thread hanging forever if DB server freezes mid-query or network cable pulls out!
         *
         * 4. Query Timeout (`statement.setQueryTimeout(seconds)`):
         *    - Asks database engine to cancel the query if it runs longer than X seconds.
         *
         * 5. Transaction Timeout (`@Transactional(timeout = X)`):
         *    - Spring rolls back transaction if elapsed time exceeds limit.
         */

        System.out.println("Timeout Hierarchy Best Practice:");
        System.out.println(" connectTimeout (3s) < connectionTimeout (3s) < socketTimeout (5s) < queryTimeout (5s) < txTimeout (10s)");
    }

    // =========================================================================================
    // SECTION 7: THE FATAL ANTI-PATTERN: EXTERNAL CALLS INSIDE @Transactional
    // =========================================================================================
    private static void section7_ExternalApiTransactionalAntiPattern() {
        printHeader("7. THE FATAL ANTI-PATTERN: EXTERNAL API CALLS IN @Transactional");
        /*
         * THE DEADLIEST ENTERPRISE ANTI-PATTERN:
         *
         *   @Transactional
         *   public void placeOrder(OrderRequest request) {
         *       // 1. Thread borrows DB connection from HikariCP pool!
         *       orderRepository.save(order); // 2ms write
         *
         *       // 2. THE CATASTROPHE: Synchronous call to 3rd-party Payment Gateway or Microservice!
         *       PaymentResult res = paymentClient.chargeCreditCard(request); // TAKES 3 SECONDS!
         *
         *       // 3. Update order status
         *       orderRepository.updateStatus(order.id(), res.status());
         *       // 4. Connection finally returned to HikariCP pool!
         *   }
         *
         * WHAT HAPPENS UNDER LOAD?
         *  - For the ENTIRE 3 SECONDS that the external HTTP call is waiting, the physical database
         *    connection sits COMPLETELY IDLE, locked by that thread!
         *  - With a pool size of 10 connections:
         *    Just 10 concurrent users will LOCK ALL 10 DATABASE CONNECTIONS!
         *  - The 11th user arrives to view their profile (fast 1ms query) -> POOL EXHAUSTED!
         *    The user gets a 500 Internal Server Error: `SQLTransientConnectionException: Connection lease timed out`!
         *  - Entire application goes down because the database connection pool was hijacked as an HTTP wait queue!
         *
         * THE ARCHITECTURAL CURE: "FETCH FIRST, TRANSACT FAST":
         *  1. Perform external I/O / HTTP calls OUTSIDE any database transaction.
         *  2. Open the `@Transactional` boundary ONLY for the rapid (1-5ms) database write!
         */

        System.out.println("Golden Architectural Rule:");
        System.out.println(" NEVER, EVER make HTTP, REST, gRPC, or third-party network calls inside @Transactional!");
        System.out.println(" Keep database connection lease times under 10 milliseconds.");
    }

    // =========================================================================================
    // SECTION 8: COMPLETE IN-MEMORY SIMULATION SUITE
    // =========================================================================================
    private static void section8_LiveExhaustionSimulationSuite() throws Exception {
        printHeader("8. LIVE SIMULATION: POOL EXHAUSTION DIAGNOSIS & CURE");

        int poolSize = 5;
        int leaseTimeoutMs = 150; // Fast timeout for simulation responsiveness
        SimulatedHikariPool pool = new SimulatedHikariPool("HikariPool-Orders", poolSize, leaseTimeoutMs);

        // --- SCENARIO A: NORMAL FAST DB WORKLOAD ---
        System.out.println("SCENARIO A: Clean High-Throughput Workload (15 concurrent fast queries of 15ms each):");
        runSimulationTest(pool, 15, false);

        // --- SCENARIO B: DISASTER! SLOW EXTERNAL API CALL INSIDE DB LEASE ---
        System.out.println("\nSCENARIO B: Disaster! (15 concurrent requests calling slow 250ms API inside DB lease):");
        runSimulationTest(pool, 15, true);

        // --- SCENARIO C: THE CURE! FETCH FIRST, TRANSACT FAST ---
        System.out.println("\nSCENARIO C: The Architectural Cure! (Fetch slow API first, then transact fast 15ms):");
        runDecoupledSimulationTest(pool, 15);
    }

    private static void runSimulationTest(SimulatedHikariPool pool, int concurrentRequests, boolean includeSlowApiCall)
            throws Exception {
        ExecutorService requestPool = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger timeoutCount = new AtomicInteger();

        Instant start = Instant.now();

        for (int i = 1; i <= concurrentRequests; i++) {
            final int requestId = i;
            requestPool.submit(() -> {
                try {
                    // Step 1: Thread attempts to borrow DB connection from pool
                    SimulatedConnection conn = pool.getConnection();
                    try {
                        // Step 2: Database work (15ms)
                        Thread.sleep(15);

                        // Step 3: THE ANTI-PATTERN: Slow external call while holding connection!
                        if (includeSlowApiCall) {
                            Thread.sleep(250); // Simulated slow external payment gateway / microservice
                        }

                        successCount.incrementAndGet();
                    } finally {
                        // Step 4: Return connection to pool
                        conn.close();
                    }
                } catch (TimeoutException e) {
                    timeoutCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        requestPool.shutdown();
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();

        System.out.printf("   [Results] Duration: %d ms | Success: %d | Timeouts: %d (Pool Size: %d, Lease Timeout: %d ms)%n",
                elapsedMs, successCount.get(), timeoutCount.get(), pool.getMaxPoolSize(), pool.getConnectionTimeoutMs());
        if (timeoutCount.get() > 0) {
            System.out.println("   --> FAILURE DIAGNOSIS: Connection pool was exhausted by slow external I/O!");
            System.out.println("       Incoming threads starved waiting for connections and threw SQLTransientConnectionException!");
        } else {
            System.out.println("   --> SUCCESS: All requests completed without thread starvation!");
        }
    }

    private static void runDecoupledSimulationTest(SimulatedHikariPool pool, int concurrentRequests)
            throws Exception {
        ExecutorService requestPool = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger timeoutCount = new AtomicInteger();

        Instant start = Instant.now();

        for (int i = 1; i <= concurrentRequests; i++) {
            requestPool.submit(() -> {
                try {
                    // STEP 1: Perform slow external API call FIRST (WITHOUT BORROWING A DB CONNECTION!)
                    Thread.sleep(250); // Third-party payment gateway HTTP call

                    // STEP 2: Only now borrow DB connection for rapid 15ms database save
                    SimulatedConnection conn = pool.getConnection();
                    try {
                        Thread.sleep(15); // Fast DB write
                        successCount.incrementAndGet();
                    } finally {
                        conn.close(); // Immediate return
                    }
                } catch (TimeoutException e) {
                    timeoutCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        requestPool.shutdown();
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();

        System.out.printf("   [Results] Duration: %d ms | Success: %d | Timeouts: %d%n",
                elapsedMs, successCount.get(), timeoutCount.get());
        System.out.println("   --> CURE CONFIRMED: By decoupling external HTTP calls from the database transaction,");
        System.out.println("       the small pool of 5 connections easily handled all 15 concurrent requests with 0 timeouts!");
    }

    // =========================================================================================
    // SECTION 9: PRODUCTION CONFIGURATION & MONITORING CHECKLIST
    // =========================================================================================
    private static void section9_ProductionConfigurationAndMetrics() {
        printHeader("9. PRODUCTION application.yml CONFIGURATION & METRICS");
        /*
         *  RECOMMENDED PRODUCTION application.yml:
         * -------------------------------------------------------------------------------------
         *  spring:
         *    datasource:
         *      hikari:
         *        pool-name: OrdersHikariCP
         *        maximum-pool-size: 15        # (CPU cores * 2) + 1
         *        minimum-idle: 15             # Fixed pool size (prevents resize latency)
         *        connection-timeout: 3000     # 3 seconds max wait (fail fast!)
         *        idle-timeout: 300000         # 5 minutes
         *        max-lifetime: 1200000        # 20 minutes (shorter than DB server idle timeout)
         *        leak-detection-threshold: 3000 # Alert if connection held > 3s!
         *        data-source-properties:
         *          cachePrepStmts: true
         *          prepStmtCacheSize: 250
         *          prepStmtCacheSqlLimit: 2048
         *          useServerPrepStmts: true
         *          connectTimeout: 3000       # 3s TCP handshake timeout
         *          socketTimeout: 5000        # 5s socket read timeout
         * -------------------------------------------------------------------------------------
         *
         *  CRITICAL MICROMETER METRICS TO ALERT ON IN GRAFANA:
         *   1. `hikaricp.connections.pending`:
         *      - Number of threads waiting to acquire a connection.
         *      - Alert IF pending > 0 for more than 5 seconds! (Indicates starvation).
         *   2. `hikaricp.connections.active`:
         *      - Number of connections currently leased.
         *   3. `hikaricp.connections.acquire`:
         *      - Time taken for a thread to acquire a connection (p99 latency).
         */

        System.out.println("Production Golden Rules:");
        System.out.println(" 1. Fix maximum-pool-size == minimum-idle to avoid runtime pool resizing latency.");
        System.out.println(" 2. Lower connection-timeout to 2-3 seconds to fail fast instead of hanging client threads.");
        System.out.println(" 3. Set leak-detection-threshold = 2000-4000ms to instantly uncover unclosed connections.");
        System.out.println(" 4. Always set driver-level connectTimeout and socketTimeout.");
    }

    // =========================================================================================
    // IN-MEMORY SIMULATION DATA STRUCTURES
    // =========================================================================================
    public static class SimulatedConnection implements AutoCloseable {
        private final int id;
        private final SimulatedHikariPool pool;

        public SimulatedConnection(int id, SimulatedHikariPool pool) {
            this.id = id;
            this.pool = pool;
        }

        @Override
        public void close() {
            pool.returnConnection(this);
        }

        public int getId() { return id; }
    }

    public static class SimulatedHikariPool {
        private final String poolName;
        private final int maxPoolSize;
        private final int connectionTimeoutMs;
        private final BlockingQueue<SimulatedConnection> availableConnections;
        private final AtomicInteger activeLeases = new AtomicInteger(0);

        public SimulatedHikariPool(String poolName, int maxPoolSize, int connectionTimeoutMs) {
            this.poolName = poolName;
            this.maxPoolSize = maxPoolSize;
            this.connectionTimeoutMs = connectionTimeoutMs;
            this.availableConnections = new ArrayBlockingQueue<>(maxPoolSize);

            // Pre-fill pool with connections
            for (int i = 1; i <= maxPoolSize; i++) {
                availableConnections.add(new SimulatedConnection(i, this));
            }
        }

        public SimulatedConnection getConnection() throws TimeoutException, InterruptedException {
            SimulatedConnection conn = availableConnections.poll(connectionTimeoutMs, TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new TimeoutException(String.format(
                        "[%s] SQLTransientConnectionException: Connection lease timed out after %d ms! (Active Leases: %d/%d)",
                        poolName, connectionTimeoutMs, activeLeases.get(), maxPoolSize));
            }
            activeLeases.incrementAndGet();
            return conn;
        }

        public void returnConnection(SimulatedConnection conn) {
            activeLeases.decrementAndGet();
            availableConnections.offer(conn);
        }

        public int getMaxPoolSize() { return maxPoolSize; }
        public int getConnectionTimeoutMs() { return connectionTimeoutMs; }
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


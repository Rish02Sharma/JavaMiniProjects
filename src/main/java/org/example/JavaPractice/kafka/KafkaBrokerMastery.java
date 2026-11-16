package org.example.JavaPractice.kafka;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * =============================================================================================
 *   MASTERCLASS: APACHE KAFKA & DISTRIBUTED MESSAGE BROKER ARCHITECTURE
 * =============================================================================================
 *
 * This file serves as an end-to-end, runnable guide, architectural reference, and simulation suite
 * for mastering Apache Kafka and distributed message brokers (from fundamentals to production internals):
 *
 * TABLE OF CONTENTS:
 *  1. Kafka vs Traditional Brokers (Distributed Commit Log vs Message Queue)
 *  2. Message Durability, Replication & Broker Internals (acks, ISR, LEO, High Watermark)
 *  3. Consumer Groups & Partition Assignment (Rebalancing Protocols, Heartbeats, Max Poll)
 *  4. Consumer Offset Management & Delivery Semantics (At-Most-Once, At-Least-Once, Exactly-Once)
 *  5. Backlog Replay & Offset Resets (earliest, latest, seek by offset/timestamp)
 *  6. Consumer Lag Monitoring & Health Metrics (Formulas, Poison Pills, Throughput Saturation)
 *  7. Complete In-Memory Simulation Suite:
 *      - Step A: Partition Commit Log & Message Production
 *      - Step B: Consumer Group Partition Assignment (2 Consumers across 3 Partitions)
 *      - Step C: Active Consumption & Live Consumer Lag Tracking
 *      - Step D: Dynamic Rebalance Trigger (Adding 3rd Consumer -> Cooperative Reassignment)
 *      - Step E: Offset Reset to EARLIEST (Full Backlog Replay)
 *      - Step F: Offset Reset to LATEST (Skip Backlog During Incident Recovery)
 *      - Step G: Time-Travel Specific Offset Seek
 *  8. Production Tuning Checklist & Interview Pearls
 * =============================================================================================
 */
public class KafkaBrokerMastery {

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("     APACHE KAFKA & DISTRIBUTED MESSAGE BROKER MASTERCLASS         ");
        System.out.println("===================================================================\n");

        section1_KafkaVsTraditionalBrokers();
        section2_DurabilityReplicationAndAcks();
        section3_ConsumerGroupsAndRebalancing();
        section4_OffsetManagementAndSemantics();
        section5_BacklogReplayAndResetStrategies();
        section6_ConsumerLagMonitoringTheory();
        section7_CompleteKafkaSimulationSuite();
        section8_ProductionChecklistAndInterviewPearls();

        System.out.println("\n===================================================================");
        System.out.println("             ALL KAFKA & BROKER LESSONS COMPLETE!                  ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: KAFKA VS TRADITIONAL BROKERS
    // =========================================================================================
    private static void section1_KafkaVsTraditionalBrokers() {
        printHeader("1. KAFKA VS TRADITIONAL MESSAGE BROKERS (JMS / RABBITMQ)");
        /*
         *  TRADITIONAL BROKERS (RabbitMQ, ActiveMQ, SQS) vs. DISTRIBUTED COMMIT LOG (Kafka, Pulsar):
         *  -------------------------------------------------------------------------------------
         *  Feature               | Traditional Broker (RabbitMQ) | Distributed Log (Kafka)
         *  ----------------------|-------------------------------|-----------------------------
         *  Core Paradigm         | Message Queue / Exchange      | Append-Only Commit Log
         *  Message Lifecycle     | Deleted after ACK             | Retained based on time/size (days/years)
         *  Consumption Model     | Push (Broker pushes to client)| Pull (Consumer polls in batches)
         *  Broker State          | Smart Broker (tracks per msg) | Dumb Broker (tracks only offsets)
         *  Consumer Scaling      | Competing consumers on 1 queue| Partitioned: 1 partition -> 1 consumer
         *  Message Replay        | Cannot replay deleted messages| Replay anytime by rewinding offset!
         *  Throughput            | Thousands of msgs/sec         | Millions of msgs/sec (Zero-Copy)
         *  -------------------------------------------------------------------------------------
         *
         *  THE TOPIC-PARTITION SCALING LAW:
         *   - A Topic is divided into 1 or more PARTITIONS.
         *   - Partitions are the UNIT OF PARALLELISM in Kafka.
         *   - ORDERING GUARANTEE: Kafka guarantees strict ordering ONLY WITHIN A SINGLE PARTITION!
         *     There is NO global ordering across different partitions!
         *   - PARTITION RULE: Within a Consumer Group, each partition is consumed by AT MOST ONE
         *     active consumer instance at any given time.
         *     If you have 3 partitions and 5 consumers in the group, 2 consumers will sit idle!
         */

        System.out.println("Kafka Architectural Invariant:");
        System.out.println(" - Topics are partitioned append-only logs stored sequentially on disk.");
        System.out.println(" - Messages are retained indefinitely or by retention policy, NOT deleted upon ACK.");
        System.out.println(" - Strict FIFO ordering is guaranteed WITHIN a partition, never across partitions.");
    }

    // =========================================================================================
    // SECTION 2: DURABILITY, REPLICATION & BROKER INTERNALS
    // =========================================================================================
    private static void section2_DurabilityReplicationAndAcks() {
        printHeader("2. MESSAGE DURABILITY, REPLICATION & PRODUCER ACKS");
        /*
         * DISK DURABILITY WITHOUT SLOW FSYNC:
         *  - Kafka writes incoming messages to the OS PAGE CACHE (RAM) and relies on background
         *    OS flushing to physical disk.
         *  - High performance: Reads leverage Linux `sendfile` (Zero-Copy), transferring data
         *    directly from Page Cache to Network Socket without copying into user-space JVM memory!
         *
         * REPLICATION & IN-SYNC REPLICAS (ISR):
         *  - Each partition has 1 LEADER and N - 1 FOLLOWERS (Replication Factor = N).
         *  - All reads and writes normally go to the Leader. Followers pull data from the Leader.
         *  - ISR (In-Sync Replicas): The subset of replicas that are actively caught up with the Leader
         *    within `replica.lag.time.max.ms` (e.g. 30 seconds).
         *
         * PRODUCER ACKS TRADEOFF MATRIX:
         * -------------------------------------------------------------------------------------
         *  acks=0  : Fire-and-forget. Producer does not wait for any acknowledgment.
         *            Highest throughput, maximum risk of data loss.
         *  acks=1  : Producer waits for Leader to write to local log.
         *            Moderate durability. Data loss if Leader crashes before replication!
         *  acks=all: Producer waits until Leader AND all `min.insync.replicas` acknowledge write.
         *            Zero data loss guarantee! (Required for financial/critical systems).
         * -------------------------------------------------------------------------------------
         *
         * HIGH WATERMARK (HW) VS. LOG END OFFSET (LEO):
         *  - LEO (Log End Offset): The offset of the next record to be written to the Leader log.
         *  - HW (High Watermark): The highest offset replicated across ALL In-Sync Replicas.
         *  - CRUCIAL RULE: Consumers can ONLY read up to the High Watermark!
         *    This prevents consumers from reading un-replicated messages that could be lost on failover!
         */

        System.out.println("Durability Configuration Rules for Zero Data Loss:");
        System.out.println(" 1. Producer: acks = all (or -1)");
        System.out.println(" 2. Broker:   min.insync.replicas = 2 (with replication.factor = 3)");
        System.out.println(" 3. Broker:   unclean.leader.election.enable = false");
        System.out.println(" Consumers are isolated from uncommitted writes via the High Watermark (HW).");
    }

    // =========================================================================================
    // SECTION 3: CONSUMER GROUPS & PARTITION REBALANCING
    // =========================================================================================
    private static void section3_ConsumerGroupsAndRebalancing() {
        printHeader("3. CONSUMER GROUPS & REBALANCING PROTOCOLS");
        /*
         * CONSUMER GROUP MECHANICS:
         *  - Group ID identifies a logical subscriber application (e.g. "payment-service-group").
         *  - Scale Out: Multiple instances of the same service join the same Group ID, sharing partitions.
         *  - Publish-Subscribe: Two DIFFERENT consumer groups subscribed to the same topic each get
         *    their own independent copy of every message!
         *
         * REBALANCING PROTOCOLS:
         *  When a consumer joins, crashes, or leaves, the Group Coordinator triggers a REBALANCE.
         *
         *  1. Eager Rebalance (Stop-the-World, pre-Kafka 2.4):
         *     - Every consumer in the group revokes ALL its assigned partitions.
         *     - All message processing pauses completely!
         *     - Partitions are reassigned from scratch.
         *
         *  2. Cooperative Sticky Rebalance (Incremental, Modern Default):
         *     - Only partitions that NEED to move are reassigned!
         *     - Consumers continue processing unaffected partitions without pause!
         *     - Slashes rebalance latency from seconds to milliseconds.
         *
         * HEALTH CHECKS & TIMEOUTS:
         *  - heartbeat.interval.ms (e.g. 3s): Heartbeat sent to Group Coordinator to prove liveness.
         *  - session.timeout.ms (e.g. 45s): Coordinator evicts consumer if no heartbeats arrive.
         *  - max.poll.interval.ms (e.g. 5m): Processing timeout! If your message processing logic
         *    takes longer than this between calls to `poll()`, the consumer is considered hung
         *    and evicted from the group, causing a cascade of rebalances!
         */

        System.out.println("Rebalance Engineering Pearls:");
        System.out.println(" - Use CooperativeStickyAssignor to prevent stop-the-world partition stalls.");
        System.out.println(" - If long processing causes repeated rebalances, increase max.poll.interval.ms");
        System.out.println("   or offload heavy processing to worker thread pools.");
    }

    // =========================================================================================
    // SECTION 4: OFFSET MANAGEMENT & DELIVERY SEMANTICS
    // =========================================================================================
    private static void section4_OffsetManagementAndSemantics() {
        printHeader("4. OFFSET MANAGEMENT & DELIVERY SEMANTICS");
        /*
         * WHAT IS AN OFFSET?
         * An immutable, sequentially increasing 64-bit integer assigned to every message
         * within a partition. It uniquely identifies a message's position.
         *
         * WHERE ARE COMMITTED OFFSETS STORED?
         *  - Historically (pre-0.9): Stored in Apache ZooKeeper (bottleneck).
         *  - Modern Kafka: Stored in an internal, high-throughput, log-compacted topic:
         *    `__consumer_offsets` (50 partitions by default).
         *
         * THE 3 DELIVERY SEMANTICS:
         * -------------------------------------------------------------------------------------
         * 1. AT-MOST-ONCE:
         *    - Offset is committed BEFORE processing the message batch.
         *    - If consumer crashes during processing -> Messages are LOST! Never processed again!
         *
         * 2. AT-LEAST-ONCE (Most Common):
         *    - Offset is committed AFTER processing the message batch.
         *    - If consumer crashes during processing -> On restart, messages are re-consumed.
         *    - Requirement: Downstream processing MUST BE IDEMPOTENT (Deduplication keys)!
         *
         * 3. EXACTLY-ONCE SEMANTICS (EOS):
         *    - Achieved via Kafka Transactions (read-process-write pattern with transactional producer).
         *    - Producer commits offsets to consumer topic inside the same atomic transaction
         *      as publishing output to downstream topics!
         *
         * AUTO-COMMIT VS MANUAL COMMIT:
         *  - enable.auto.commit = true: Background thread commits every `auto.commit.interval.ms`.
         *    HIGH RISK: Can commit offsets for messages that haven't been safely processed yet!
         *  - Manual commit: Call `commitSync()` or `commitAsync()` explicitly after DB commit.
         */

        System.out.println("Delivery Semantics Summary:");
        System.out.println(" - At-Least-Once + Idempotent Consumer is the gold standard for microservices.");
        System.out.println(" - Turn OFF enable.auto.commit in mission-critical applications.");
        System.out.println(" - Use commitSync() on graceful shutdown, commitAsync() during high-throughput loop.");
    }

    // =========================================================================================
    // SECTION 5: BACKLOG REPLAY & OFFSET RESET STRATEGIES
    // =========================================================================================
    private static void section5_BacklogReplayAndResetStrategies() {
        printHeader("5. BACKLOG REPLAY & OFFSET RESET STRATEGIES");
        /*
         * auto.offset.reset (When no committed offset exists on broker):
         *  1. "earliest": Rewinds to the beginning of the partition (offset 0 or oldest retained).
         *  2. "latest"  : Jumps to the Log End Offset (LEO), ignoring all historical messages.
         *  3. "none"    : Throws NoOffsetForPartitionException if no existing offset found.
         *
         * PROGRAMMATIC SEEKING (TIME TRAVEL & REPLAY):
         *  - consumer.seek(TopicPartition, targetOffset):
         *    Instantly rewinds or fast-forwards consumption position.
         *  - consumer.seekToBeginning(partitions):
         *    Full backlog replay (useful for database re-indexing, rebuilding search caches).
         *  - consumer.seekToEnd(partitions):
         *    Discards historical backlog during critical incident recovery to restore real-time processing.
         *  - consumer.offsetsForTimes(Map<TopicPartition, Long timestamp>):
         *    Looks up offsets matching a specific wall-clock timestamp (e.g. rewind to 9:00 AM).
         */

        System.out.println("Backlog Replay Strategies:");
        System.out.println(" - Seek to Earliest: Disaster recovery, rebuilding search index / cache.");
        System.out.println(" - Seek to Latest:   Emergency recovery to skip poisoned/unmanageable backlogs.");
        System.out.println(" - Seek by Timestamp: Time-travel debugging to re-run events around an incident.");
    }

    // =========================================================================================
    // SECTION 6: CONSUMER LAG MONITORING & METRICS THEORY
    // =========================================================================================
    private static void section6_ConsumerLagMonitoringTheory() {
        printHeader("6. CONSUMER LAG MONITORING & METRICS THEORY");
        /*
         * THE CONSUMER LAG FORMULA:
         * -------------------------------------------------------------------------------------
         *    Consumer Lag = Log End Offset (LEO) - Current Committed Offset
         * -------------------------------------------------------------------------------------
         *
         * WHY CONSUMER LAG IS THE #1 HEALTH KPI:
         *  - Lag = 0: Consumer is completely caught up in real time.
         *  - Constant Lag: Consumer is keeping pace with producer, but with an established buffer.
         *  - Growing Lag: System is UNHEALTHY! Producer rate > Consumer processing rate!
         *    Causes: Slow database writes, downstream HTTP latency, garbage collection pauses,
         *    or POISON PILL messages (malformed payload causing crash/restart loops).
         *
         * PRODUCTION MONITORING TOOLS:
         *  - Kafka Exporter + Prometheus + Grafana
         *  - LinkedIn Burrow (automates lag evaluation without hardcoded thresholds)
         *  - JMX MBeans: `records-lag-max`, `records-lead-min`
         */

        System.out.println("Consumer Lag Diagnostics:");
        System.out.println(" - Monitor Consumer Lag per partition, not just topic aggregate.");
        System.out.println(" - If one partition has high lag while others are zero: Check for key skew or poison pill.");
    }

    // =========================================================================================
    // SECTION 7: COMPLETE KAFKA IN-MEMORY SIMULATION SUITE
    // =========================================================================================
    private static void section7_CompleteKafkaSimulationSuite() {
        printHeader("7. COMPLETE KAFKA IN-MEMORY SIMULATION SUITE");

        // Step A: Initialize Broker with Topic "orders" having 3 Partitions
        SimulatedBroker broker = new SimulatedBroker();
        String topic = "orders";
        broker.createTopic(topic, 3);
        System.out.println("Step A: Topic '" + topic + "' created with 3 partitions [P0, P1, P2].");

        // Produce 12 messages across the 3 partitions (4 messages per partition)
        for (int p = 0; p < 3; p++) {
            for (int i = 1; i <= 4; i++) {
                String key = "cust-" + (p * 10 + i);
                String val = "Order-Item-" + (p * 10 + i) + " ($" + (i * 25) + ")";
                broker.produce(topic, p, key, val);
            }
        }
        System.out.println("Produced 12 messages across 3 partitions (LEO = 4 for all partitions).\n");

        // Step B: Form Consumer Group with 2 Consumers
        SimulatedConsumerGroup group = new SimulatedConsumerGroup("order-processing-group", broker);
        SimulatedConsumer c1 = new SimulatedConsumer("Consumer-1", group);
        SimulatedConsumer c2 = new SimulatedConsumer("Consumer-2", group);

        group.registerConsumer(c1);
        group.registerConsumer(c2);
        group.rebalance(); // Initial assignment

        // Step C: Active Consumption & Live Lag Tracking
        System.out.println("\nStep C: Active Consumption & Live Lag Tracking:");
        c1.pollAndProcess(2); // Consume 2 messages from assigned partitions
        c2.pollAndProcess(2); // Consume 2 messages from assigned partitions
        group.printLagReport(topic);

        // Consume remaining messages to reach zero lag
        c1.pollAndProcess(10);
        c2.pollAndProcess(10);
        System.out.println("\nAfter consuming full backlog:");
        group.printLagReport(topic);

        // Step D: Dynamic Rebalance (Add Consumer 3 -> 3 consumers for 3 partitions!)
        System.out.println("\nStep D: Dynamic Rebalance Triggered (Adding Consumer-3):");
        SimulatedConsumer c3 = new SimulatedConsumer("Consumer-3", group);
        group.registerConsumer(c3);
        group.rebalance();

        // Step E: Offset Reset to EARLIEST (Full Backlog Replay)
        System.out.println("\nStep E: Simulating Offset Reset to EARLIEST (Replaying all historical events):");
        group.resetOffsets(topic, OffsetResetStrategy.EARLIEST);
        group.printLagReport(topic);
        System.out.println("Consuming replay stream with newly rebalanced 3-consumer group:");
        c1.pollAndProcess(10);
        c2.pollAndProcess(10);
        c3.pollAndProcess(10);
        group.printLagReport(topic);

        // Step F: Produce More Messages, Then Reset to LATEST
        System.out.println("\nStep F: Simulating Offset Reset to LATEST (Incident Emergency Skip):");
        // Produce 3 emergency backlog messages
        broker.produce(topic, 0, "emergency-1", "Stale-Backlog-1");
        broker.produce(topic, 1, "emergency-2", "Stale-Backlog-2");
        broker.produce(topic, 2, "emergency-3", "Stale-Backlog-3");
        System.out.println("3 new messages produced. Current Lag:");
        group.printLagReport(topic);

        System.out.println("Skipping backlog by resetting offsets to LATEST...");
        group.resetOffsets(topic, OffsetResetStrategy.LATEST);
        group.printLagReport(topic);

        // Step G: Time-Travel Seek to Specific Offset
        System.out.println("\nStep G: Time-Travel Seek to Specific Offset (Partition 0 -> Offset 2):");
        group.seek(topic, 0, 2);
        group.printLagReport(topic);
        System.out.println("Processing from rewound offset 2 on Partition 0:");
        c1.pollAndProcess(5);
        group.printLagReport(topic);
    }

    // =========================================================================================
    // SECTION 8: PRODUCTION CHECKLIST & INTERVIEW PEARLS
    // =========================================================================================
    private static void section8_ProductionChecklistAndInterviewPearls() {
        printHeader("8. PRODUCTION TUNING CHECKLIST & INTERVIEW PEARLS");
        /*
         *  PRODUCER PRODUCTION CHECKLIST:
         *   1. acks = all (Zero data loss guarantee).
         *   2. enable.idempotence = true (Prevents duplicate messages due to network retries).
         *   3. compression.type = zstd or lz4 (Reduces network I/O and broker disk usage by 50-70%).
         *   4. linger.ms = 20 & batch.size = 64KB (Enables batching, massive throughput boost).
         *   5. max.in.flight.requests.per.connection = 5 (With idempotence enabled, preserves ordering).
         *
         *  CONSUMER PRODUCTION CHECKLIST:
         *   1. enable.auto.commit = false (Commit offsets manually after business transaction completes).
         *   2. partition.assignment.strategy = CooperativeStickyAssignor (Incremental rebalances).
         *   3. max.poll.interval.ms: Set appropriately higher than maximum batch processing time.
         *   4. Dead Letter Queue (DLQ): Route unparseable poison pills to DLQ after N retries
         *      to prevent blocking the partition indefinitely!
         */

        System.out.println("Top 5 Kafka Interview Questions Answered:");
        System.out.println(" 1. How does Kafka achieve high write throughput? Sequential I/O + OS Page Cache.");
        System.out.println(" 2. How does Kafka achieve high read throughput? Zero-Copy (sendfile syscall).");
        System.out.println(" 3. What causes a rebalance? Consumer crash, consumer addition, or max.poll.interval.ms expiry.");
        System.out.println(" 4. Can you have 10 consumers in a group for a 4-partition topic? Yes, but 6 will sit idle.");
        System.out.println(" 5. How to achieve Exactly-Once? Kafka Transactions or Idempotent Consumer pattern.");
    }

    // =========================================================================================
    // SIMULATION ENGINE DATA STRUCTURES & IMPLEMENTATIONS
    // =========================================================================================
    public enum OffsetResetStrategy { EARLIEST, LATEST }

    public record Message(long offset, String key, String value, Instant timestamp) {}

    public static class SimulatedPartition {
        private final int partitionId;
        private final List<Message> log = new ArrayList<>();
        private final AtomicLong nextOffset = new AtomicLong(0);

        public SimulatedPartition(int partitionId) {
            this.partitionId = partitionId;
        }

        public synchronized long append(String key, String value) {
            long offset = nextOffset.getAndIncrement();
            log.add(new Message(offset, key, value, Instant.now()));
            return offset;
        }

        public synchronized long getLogEndOffset() {
            return nextOffset.get();
        }

        public synchronized List<Message> readFrom(long offset, int maxCount) {
            List<Message> batch = new ArrayList<>();
            for (Message m : log) {
                if (m.offset() >= offset && batch.size() < maxCount) {
                    batch.add(m);
                }
            }
            return batch;
        }

        public int getPartitionId() { return partitionId; }
    }

    public static class SimulatedBroker {
        private final Map<String, List<SimulatedPartition>> topics = new ConcurrentHashMap<>();

        public void createTopic(String topic, int partitions) {
            List<SimulatedPartition> partitionList = new ArrayList<>();
            for (int i = 0; i < partitions; i++) {
                partitionList.add(new SimulatedPartition(i));
            }
            topics.put(topic, partitionList);
        }

        public void produce(String topic, int partition, String key, String value) {
            List<SimulatedPartition> partList = topics.get(topic);
            if (partList == null || partition >= partList.size()) {
                throw new IllegalArgumentException("Unknown topic or partition!");
            }
            partList.get(partition).append(key, value);
        }

        public List<SimulatedPartition> getPartitions(String topic) {
            return topics.getOrDefault(topic, List.of());
        }

        public SimulatedPartition getPartition(String topic, int partitionId) {
            return topics.get(topic).get(partitionId);
        }
    }

    public static class SimulatedConsumerGroup {
        private final String groupId;
        private final SimulatedBroker broker;
        private final List<SimulatedConsumer> members = new ArrayList<>();
        // Stores committed offsets: Map<Topic, Map<PartitionId, CommittedOffset>>
        private final Map<String, Map<Integer, Long>> committedOffsets = new ConcurrentHashMap<>();

        public SimulatedConsumerGroup(String groupId, SimulatedBroker broker) {
            this.groupId = groupId;
            this.broker = broker;
        }

        public synchronized void registerConsumer(SimulatedConsumer consumer) {
            members.add(consumer);
            System.out.printf("   [Group %s] Registered %s (Total Members: %d)%n",
                    groupId, consumer.getName(), members.size());
        }

        public synchronized void rebalance() {
            System.out.println("   [Rebalance] Executing partition assignment across group members...");
            // Clear existing assignments:
            for (SimulatedConsumer c : members) c.clearAssignments();

            // Simple Round-Robin Assignment:
            String topic = "orders";
            List<SimulatedPartition> partitions = broker.getPartitions(topic);
            for (int i = 0; i < partitions.size(); i++) {
                SimulatedConsumer assignedConsumer = members.get(i % members.size());
                assignedConsumer.assignPartition(topic, partitions.get(i).getPartitionId());
            }

            for (SimulatedConsumer c : members) {
                System.out.printf("      -> %s assigned partitions: %s%n",
                        c.getName(), c.getAssignedPartitions(topic));
            }
        }

        public synchronized void commitOffset(String topic, int partition, long offset) {
            committedOffsets.computeIfAbsent(topic, k -> new ConcurrentHashMap<>()).put(partition, offset);
        }

        public synchronized long getCommittedOffset(String topic, int partition) {
            return committedOffsets.getOrDefault(topic, Map.of()).getOrDefault(partition, 0L);
        }

        public synchronized void resetOffsets(String topic, OffsetResetStrategy strategy) {
            List<SimulatedPartition> partitions = broker.getPartitions(topic);
            for (SimulatedPartition p : partitions) {
                long targetOffset = (strategy == OffsetResetStrategy.EARLIEST) ? 0L : p.getLogEndOffset();
                commitOffset(topic, p.getPartitionId(), targetOffset);
                // Also update local fetch position of assigned consumers:
                for (SimulatedConsumer c : members) {
                    if (c.getAssignedPartitions(topic).contains(p.getPartitionId())) {
                        c.setFetchPosition(topic, p.getPartitionId(), targetOffset);
                    }
                }
            }
            System.out.printf("   [Reset] All partitions for topic '%s' reset to %s.%n", topic, strategy);
        }

        public synchronized void seek(String topic, int partition, long targetOffset) {
            commitOffset(topic, partition, targetOffset);
            for (SimulatedConsumer c : members) {
                if (c.getAssignedPartitions(topic).contains(partition)) {
                    c.setFetchPosition(topic, partition, targetOffset);
                }
            }
            System.out.printf("   [Seek] Topic '%s' Partition %d sought to Offset %d.%n", topic, partition, targetOffset);
        }

        public void printLagReport(String topic) {
            System.out.printf("--- LAG REPORT for Topic '%s' [Group: %s] ---%n", topic, groupId);
            List<SimulatedPartition> partitions = broker.getPartitions(topic);
            long totalLag = 0;
            for (SimulatedPartition p : partitions) {
                long leo = p.getLogEndOffset();
                long committed = getCommittedOffset(topic, p.getPartitionId());
                long lag = Math.max(0, leo - committed);
                totalLag += lag;
                System.out.printf("   Partition %d | LEO: %d | Committed Offset: %d | LAG: %d%n",
                        p.getPartitionId(), leo, committed, lag);
            }
            System.out.printf("   TOTAL CONSUMER LAG: %d messages%n", totalLag);
            System.out.println("-------------------------------------------------------");
        }

        public SimulatedBroker getBroker() { return broker; }
    }

    public static class SimulatedConsumer {
        private final String name;
        private final SimulatedConsumerGroup group;
        // Map<Topic, Set<PartitionId>>
        private final Map<String, Set<Integer>> assignments = new HashMap<>();
        // Local fetch position: Map<Topic, Map<PartitionId, Offset>>
        private final Map<String, Map<Integer, Long>> fetchPositions = new HashMap<>();

        public SimulatedConsumer(String name, SimulatedConsumerGroup group) {
            this.name = name;
            this.group = group;
        }

        public void clearAssignments() {
            assignments.clear();
        }

        public void assignPartition(String topic, int partitionId) {
            assignments.computeIfAbsent(topic, k -> new HashSet<>()).add(partitionId);
            long initialOffset = group.getCommittedOffset(topic, partitionId);
            setFetchPosition(topic, partitionId, initialOffset);
        }

        public Set<Integer> getAssignedPartitions(String topic) {
            return assignments.getOrDefault(topic, Set.of());
        }

        public void setFetchPosition(String topic, int partition, long offset) {
            fetchPositions.computeIfAbsent(topic, k -> new HashMap<>()).put(partition, offset);
        }

        public void pollAndProcess(int maxRecordsPerPartition) {
            for (Map.Entry<String, Set<Integer>> entry : assignments.entrySet()) {
                String topic = entry.getKey();
                for (int partitionId : entry.getValue()) {
                    long currentPos = fetchPositions.getOrDefault(topic, Map.of()).getOrDefault(partitionId, 0L);
                    SimulatedPartition part = group.getBroker().getPartition(topic, partitionId);
                    List<Message> records = part.readFrom(currentPos, maxRecordsPerPartition);

                    for (Message msg : records) {
                        // Business processing logic simulation
                        System.out.printf("      [%s] Processed P%d@Offset %d: key='%s', val='%s'%n",
                                name, partitionId, msg.offset(), msg.key(), msg.value());
                        currentPos = msg.offset() + 1;
                    }

                    // Update local fetch position and commit offset to group:
                    setFetchPosition(topic, partitionId, currentPos);
                    group.commitOffset(topic, partitionId, currentPos);
                }
            }
        }

        public String getName() { return name; }
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


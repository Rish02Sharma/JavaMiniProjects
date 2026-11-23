package org.example.JavaPractice.streams;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.*;

/**
 * =============================================================================================
 *                      MASTERCLASS: JAVA STREAM API (ZERO TO HERO)
 * =============================================================================================
 *
 * This file serves as a comprehensive, hands-on, runnable documentation and guide for mastering
 * the Java Stream API (from basic fundamentals to advanced collectors, parallel processing,
 * and high-frequency interview problems).
 *
 * TABLE OF CONTENTS:
 *  1. Stream Philosophy & Architecture (Stream vs Collection, 3 Pipeline Stages, Re-use Trap)
 *  2. Ways to Create Streams (Collections, Arrays, Values, Generators, Infinite Streams)
 *  3. Intermediate Operations (Stateless vs Stateful, Lazy Evaluation & Short-Circuiting)
 *  4. flatMap & mapMulti Deep Dive (Transforming 1-to-Many & Flattening Nested Structures)
 *  5. Terminal Operations & Reductions (reduce, count, min, max, matching, toList)
 *  6. Primitive Streams (IntStream, LongStream, DoubleStream & Zero-Boxing Performance)
 *  7. The Collectors Framework (groupingBy, partitioningBy, toMap, joining, summarizing)
 *  8. Advanced Collectors (collectingAndThen, mapping, filtering, and Java 12+ teeing)
 *  9. Custom Collectors (Building Custom Collectors with Collector.of)
 * 10. Parallel Streams & ForkJoinPool (Spliterator, N*Q Rule, Pitfalls & Thread Pools)
 * 11. Top 12 Real-World & Interview Stream Problems (Fully Solved & Explained)
 * 12. Stream Best Practices, Anti-Patterns & Debugging (peek, purity, performance)
 * =============================================================================================
 */
public class Streams {

    // -----------------------------------------------------------------------------------------
    // DOMAIN MODELS USED ACROSS EXAMPLES
    // -----------------------------------------------------------------------------------------
    public record Employee(String name, String dept, double salary, int age, List<String> skills) {
        // Overloaded constructor for simple creation
        public Employee(String name, String dept, double salary, int age) {
            this(name, dept, salary, age, List.of());
        }
    }

    public record Order(String orderId, String customer, double amount, String status) {}

    // -----------------------------------------------------------------------------------------
    // MAIN METHOD: EXECUTES ALL SECTIONS SEQUENTIALLY
    // -----------------------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("        JAVA STREAM API: BEGINNER TO ADVANCED MASTERCLASS          ");
        System.out.println("===================================================================\n");

        section1_StreamPhilosophyAndArchitecture();
        section2_WaysToCreateStreams();
        section3_IntermediateOperationsAndLaziness();
        section4_FlatMapAndMapMulti();
        section5_TerminalOperationsAndReductions();
        section6_PrimitiveStreams();
        section7_CollectorsFramework();
        section8_AdvancedCollectorsAndTeeing();
        section9_CustomCollectors();
        section10_ParallelStreamsAndForkJoin();
        section11_Top12InterviewStreamProblems();
        section12_BestPracticesAndAntiPatterns();

        System.out.println("\n===================================================================");
        System.out.println("               ALL STREAM API LESSONS COMPLETE!                    ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: STREAM PHILOSOPHY & ARCHITECTURE
    // =========================================================================================
    private static void section1_StreamPhilosophyAndArchitecture() {
        printHeader("1. STREAM PHILOSOPHY & ARCHITECTURE");
        /*
         * WHAT IS A STREAM?
         * A Stream is NOT a data structure. It does not store elements in memory.
         * Instead, it is a typed, pipeline-based view of a data source (Collection, Array, I/O)
         * that carries values through computational transformations.
         *
         * STREAM VS. COLLECTION:
         * -------------------------------------------------------------------------------------
         * Feature            | Collection                   | Stream
         * -------------------|------------------------------|----------------------------------
         * Nature             | In-memory Data Structure     | Computations on demand (Pipeline)
         * Data Storage       | Stores elements in RAM       | Does NOT store elements
         * Modification       | Modifies underlying data     | Pure / Non-interfering (returns new Stream)
         * Evaluation         | Eager (computed immediately) | Lazy (computed only on terminal op)
         * Traversal          | Iterated multiple times      | Consumed ONCE (Single-use only!)
         * -------------------------------------------------------------------------------------
         *
         * THE 3 STAGES OF A STREAM PIPELINE:
         *  [Source] -> [Intermediate Op (lazy)] -> [Intermediate Op (lazy)] -> [Terminal Op (eager)]
         *
         * STREAM INVARIANTS:
         *  1. Streams are Single-Use: Once a terminal operation is called, the stream is closed!
         *     Attempting to reuse it throws `IllegalStateException`.
         *  2. Non-interference: The stream source must NOT be modified while the stream is running!
         *  3. Stateless & Pure: Lambdas passed to intermediate operations should be free of side effects.
         */

        // Demonstrating the Single-Use Stream Trap:
        List<String> fruits = List.of("Apple", "Banana", "Cherry");
        Stream<String> fruitStream = fruits.stream().filter(f -> f.startsWith("A"));

        // First consumption (OK):
        System.out.println("First consumption: " + fruitStream.toList());

        // Second consumption (FAILS!):
        try {
            long count = fruitStream.count(); // Throws IllegalStateException!
            System.out.println(count);
        } catch (IllegalStateException e) {
            System.out.println("Re-use trap verified: " + e.getMessage());
        }
    }

    // =========================================================================================
    // SECTION 2: WAYS TO CREATE STREAMS
    // =========================================================================================
    private static void section2_WaysToCreateStreams() {
        printHeader("2. WAYS TO CREATE STREAMS");

        // 1. From Collections:
        List<String> list = List.of("A", "B", "C");
        Stream<String> s1 = list.stream();

        // 2. From Arrays:
        String[] arr = {"X", "Y", "Z"};
        int[] arr2 = {1, 2, 3, 4};
        Stream<String> s2 = Arrays.stream(arr);

        // 3. From Direct Values (Stream.of):
        Stream<String> s3 = Stream.of("One", "Two", "Three");

        // 4. Using Stream.builder():
        Stream<String> s4 = Stream.<String>builder().add("Red").add("Green").add("Blue").build();

        // 5. Primitive Ranges (IntStream, LongStream):
        IntStream rangeExclusive = IntStream.range(1, 5);       // 1, 2, 3, 4
        IntStream rangeInclusive = IntStream.rangeClosed(1, 5); // 1, 2, 3, 4, 5
        System.out.println("IntStream rangeClosed(1, 5): " + rangeInclusive.boxed().toList());

        // 6. Infinite Streams with Stream.iterate:
        // Java 8 iterate: Stream.iterate(seed, UnaryOperator) -> REQUIRES limit()!
        List<Integer> powersOfTwo = Stream.iterate(1, x -> x * 2).limit(6).toList();
        System.out.println("Infinite iterate with limit: " + powersOfTwo); // [1, 2, 4, 8, 16, 32]

        // Java 9+ iterate with Predicate (acting like a for-loop: for(i=1; i<=100; i*=2)):
        List<Integer> terminatingIterate = Stream.iterate(1, n -> n <= 50, n -> n * 2).toList();
        System.out.println("Java 9 iterate with terminating predicate: " + terminatingIterate); // [1, 2, 4, 8, 16, 32]

        // 7. Infinite Streams with Stream.generate:
        List<Double> randomNumbers = Stream.generate(Math::random).limit(3).toList();
        System.out.println("Stream.generate(Math::random): " + randomNumbers);

        // 8. From Strings (chars / codePoints):
        String text = "Stream";
        List<Character> charList = text.chars().mapToObj(c -> (char) c).toList();
        System.out.println("String.chars() mapped to Characters: " + charList);
    }

    // =========================================================================================
    // SECTION 3: INTERMEDIATE OPERATIONS & LAZY EVALUATION
    // =========================================================================================
    private static void section3_IntermediateOperationsAndLaziness() {
        printHeader("3. INTERMEDIATE OPERATIONS & LAZY EVALUATION");
        /*
         * STATELESS VS. STATEFUL INTERMEDIATE OPERATIONS:
         *
         * Stateless Operations:
         *  - Processes each element independently of others.
         *  - Requires zero memory of previously processed elements.
         *  - Examples: filter(), map(), mapToInt(), peek()
         *
         * Stateful Operations:
         *  - Requires knowledge of previously processed elements or entire dataset!
         *  - Cannot emit results until it buffers or inspects other elements.
         *  - Examples: distinct(), sorted(), limit(), skip(), takeWhile(), dropWhile()
         *
         * LAZY EVALUATION PROOF:
         * Intermediate operations are NOT executed when declared.
         * The JVM constructs an execution pipeline graph. Execution begins ONLY when a
         * terminal operation (e.g. toList, findFirst, forEach) pulls data through the pipeline!
         */

        List<String> names = List.of("Asha", "Ravi", "Alexander", "Meera", "Karan");

        System.out.println("Demonstrating Lazy Execution & Short-Circuiting:");
        // Notice: We print inside filter & map to trace execution order:
        Optional<String> firstLongName = names.stream()
                .filter(name -> {
                    System.out.println("   [filter] checking: " + name);
                    return name.length() > 5;
                })
                .map(name -> {
                    System.out.println("   [map] transforming: " + name);
                    return name.toUpperCase();
                })
                .findFirst(); // Terminal operation (Short-circuits immediately after finding Alexander!)

        System.out.println("Result: " + firstLongName.orElse("None"));
        System.out.println("Notice: 'Meera' and 'Karan' were NEVER evaluated because findFirst short-circuited!");

        // Java 9+ takeWhile & dropWhile:
        // takeWhile: Takes elements as long as predicate is true; stops immediately on first false!
        // dropWhile: Drops elements as long as predicate is true; keeps everything once false!
        List<Integer> orderedNums = List.of(2, 4, 6, 7, 8, 10);
        List<Integer> taken = orderedNums.stream().takeWhile(n -> n % 2 == 0).toList();
        List<Integer> dropped = orderedNums.stream().dropWhile(n -> n % 2 == 0).toList();
        System.out.println("takeWhile even numbers: " + taken);   // [2, 4, 6] (stops at 7)
        System.out.println("dropWhile even numbers: " + dropped); // [7, 8, 10]
    }

    // =========================================================================================
    // SECTION 4: flatMap & mapMulti DEEP DIVE
    // =========================================================================================
    private static void section4_FlatMapAndMapMulti() {
        printHeader("4. flatMap & mapMulti DEEP DIVE");
        /*
         * map vs. flatMap:
         *  - map(T -> R):           1-to-1 transformation. Input T -> Output R.
         *                           If R is a List, you get Stream<List<R>>.
         *  - flatMap(T -> Stream<R>): 1-to-Many transformation. Flattens inner streams into a single Stream<R>.
         *
         * Java 16+ mapMulti:
         *  - flatMap creates a new intermediate Stream object for every element (high GC overhead).
         *  - mapMulti(BiConsumer<T, Consumer<R>>) passes elements directly to a downstream consumer,
         *    eliminating stream allocation overhead entirely!
         */

        // 1. Flattening nested lists:
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
        List<Integer> flattened = nested.stream()
                .flatMap(List::stream)
                .toList();
        System.out.println("flatMap flattened nested list: " + flattened); // [1, 2, 3, 4, 5]

        // 2. Extracting words from sentences:
        List<String> sentences = List.of("hello world", "java streams", "modern functional programming");
        List<String> words = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .toList();
        System.out.println("Words extracted via flatMap: " + words);

        // 3. Extracting nested collections from objects:
        List<Employee> team = List.of(
                new Employee("Asha", "IT", 90000, 30, List.of("Java", "Kubernetes", "AWS")),
                new Employee("Ravi", "IT", 70000, 25, List.of("Python", "Docker")),
                new Employee("Sita", "Finance", 80000, 45, List.of("Excel", "SQL", "Java"))
        );

        List<String> distinctSkills = team.stream()
                .flatMap(e -> e.skills().stream())
                .distinct()
                .sorted()
                .toList();
        System.out.println("Distinct employee skills: " + distinctSkills);

        // 4. Java 16+ mapMulti (High-performance zero-allocation alternative to flatMap):
        List<Integer> numbers = List.of(1, 2, 3, 4);
        // Duplicate each number and emit its square:
        List<Integer> mapMultiResult = numbers.stream()
                .<Integer>mapMulti((num, consumer) -> {
                    consumer.accept(num);       // emit original
                    consumer.accept(num * num); // emit square
                })
                .toList();
        System.out.println("mapMulti expansion [n, n^2]: " + mapMultiResult); // [1, 1, 2, 4, 3, 9, 4, 16]
    }

    // =========================================================================================
    // SECTION 5: TERMINAL OPERATIONS & REDUCTIONS
    // =========================================================================================
    private static void section5_TerminalOperationsAndReductions() {
        printHeader("5. TERMINAL OPERATIONS & REDUCTIONS");
        /*
         * Terminal operations trigger the execution of the pipeline.
         *
         * Categories:
         *  1. Matching (Short-circuiting): anyMatch, allMatch, noneMatch
         *  2. Finding (Short-circuiting):  findFirst, findAny
         *  3. Aggregation:                count, min, max
         *  4. General Reduction:          reduce(accumulator), reduce(identity, accumulator)
         *  5. Collection:                 collect(Collector), toList()
         */

        List<Integer> nums = List.of(5, 12, 18, 25, 40);

        // Matching:
        boolean hasBigNum = nums.stream().anyMatch(n -> n > 30);
        boolean allPositive = nums.stream().allMatch(n -> n > 0);
        System.out.println("anyMatch (> 30): " + hasBigNum + " | allMatch (> 0): " + allPositive);

        // Finding (findFirst vs findAny):
        // findFirst: Always returns the first element in encounter order.
        // findAny: Returns ANY element (optimized for parallel streams where any thread finishing first wins).
        Optional<Integer> firstEven = nums.stream().filter(n -> n % 2 == 0).findFirst();
        System.out.println("findFirst even number: " + firstEven.orElse(-1));

        // Reductions:
        // Form 1: reduce(BinaryOperator) -> Returns Optional (because stream might be empty)
        Optional<Integer> sumOptional = nums.stream().reduce((a, b) -> a + b);
        System.out.println("reduce without identity: " + sumOptional.orElse(0));

        // Form 2: reduce(identity, BinaryOperator) -> Returns value directly
        int product = nums.stream().reduce(1, (a, b) -> a * b);
        System.out.println("reduce with identity (product): " + product);

        // Min and Max with Comparator:
        Optional<Integer> maxVal = nums.stream().max(Comparator.naturalOrder());
        Optional<Integer> minVal = nums.stream().min(Comparator.naturalOrder());
        System.out.println("Min: " + minVal.orElse(0) + " | Max: " + maxVal.orElse(0));

        // Java 16+ Stream.toList() vs Collectors.toList():
        // - stream.toList(): Produces an IMMUTABLE List (backed by compact internal array, cannot add/remove).
        // - collect(Collectors.toList()): Produces a MUTABLE ArrayList by default.
        List<Integer> unmodifiableList = nums.stream().toList();
        System.out.println("Java 16 Stream.toList(): " + unmodifiableList.getClass().getSimpleName());
    }

    // =========================================================================================
    // SECTION 6: PRIMITIVE STREAMS (ZERO-BOXING PERFORMANCE)
    // =========================================================================================
    private static void section6_PrimitiveStreams() {
        printHeader("6. PRIMITIVE STREAMS (ZERO-BOXING HIGH PERFORMANCE)");
        /*
         * Why Primitive Streams?
         * Stream<Integer> boxes every primitive 'int' into an Integer heap object.
         * For millions of elements, this causes massive GC overhead.
         * IntStream, LongStream, and DoubleStream operate directly on raw primitives!
         *
         * Primitive Specialized Operations:
         *  - .sum()
         *  - .average() -> OptionalDouble
         *  - .summaryStatistics() -> count, sum, min, average, max in a single pass!
         *  - .mapToObj(i -> ...) -> convert primitive back to object
         *  - .boxed() -> convert IntStream to Stream<Integer>
         */

        IntStream intStream = IntStream.rangeClosed(1, 100);
        int totalSum = intStream.sum();
        System.out.println("IntStream sum 1..100: " + totalSum);

        // SummaryStatistics in a single pass:
        IntSummaryStatistics stats = IntStream.of(15, 25, 40, 10, 95, 60).summaryStatistics();
        System.out.println("Statistics: count=" + stats.getCount()
                + ", sum=" + stats.getSum()
                + ", min=" + stats.getMin()
                + ", avg=" + stats.getAverage()
                + ", max=" + stats.getMax());

        // Mapping between primitive and object streams:
        List<String> formatted = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> "Item #" + i)
                .toList();
        System.out.println("mapToObj: " + formatted);
    }

    // =========================================================================================
    // SECTION 7: THE COLLECTORS FRAMEWORK
    // =========================================================================================
    private static void section7_CollectorsFramework() {
        printHeader("7. THE COLLECTORS FRAMEWORK (groupingBy, toMap, partitioningBy)");

        List<Employee> emps = List.of(
                new Employee("Asha", "IT", 90000, 30),
                new Employee("Ravi", "IT", 70000, 25),
                new Employee("Meera", "HR", 50000, 40),
                new Employee("Karan", "HR", 65000, 35),
                new Employee("Sita", "Finance", 80000, 45)
        );

        // 1. toList, toSet, toCollection:
        Set<String> departments = emps.stream().map(Employee::dept).collect(Collectors.toSet());
        TreeSet<String> sortedDepts = emps.stream().map(Employee::dept).collect(Collectors.toCollection(TreeSet::new));
        System.out.println("Collected to Set: " + departments);
        System.out.println("Collected to TreeSet: " + sortedDepts);

        // 2. Collectors.joining:
        String joinedNames = emps.stream()
                .map(Employee::name)
                .collect(Collectors.joining(", ", "Employees: [", "]"));
        System.out.println("joining: " + joinedNames);

        // 3. Collectors.toMap (Handling Duplicate Keys!):
        // Basic: toMap(keyMapper, valueMapper) -> THROWS IllegalStateException if keys collide!
        // Robust: toMap(keyMapper, valueMapper, mergeFunction)
        Map<String, Double> deptHighestSalary = emps.stream().collect(Collectors.toMap(
                Employee::dept,
                Employee::salary,
                BinaryOperator.maxBy(Double::compare) // Merge function on duplicate key!
        ));
        System.out.println("toMap with merge function (highest salary per dept): " + deptHighestSalary);

        // 4. Collectors.groupingBy:
        // Form A: Single-level grouping (Map<Dept, List<Employee>>)
        Map<String, List<Employee>> byDept = emps.stream().collect(Collectors.groupingBy(Employee::dept));
        System.out.println("groupingBy dept: " + byDept.keySet());

        // Form B: groupingBy + Downstream Collector (Counting)
        Map<String, Long> countByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
        System.out.println("Count by dept: " + countByDept);

        // Form C: groupingBy + Downstream Collector (Averaging)
        Map<String, Double> avgSalaryByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));
        System.out.println("Avg salary by dept: " + avgSalaryByDept);

        // Form D: groupingBy + Downstream mapping
        Map<String, List<String>> namesByDept = emps.stream().collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.mapping(Employee::name, Collectors.toList())
        ));
        System.out.println("Employee names by dept: " + namesByDept);

        // 5. Collectors.partitioningBy (Specialized grouping into true / false):
        Map<Boolean, List<Employee>> highEarners = emps.stream()
                .collect(Collectors.partitioningBy(e -> e.salary() >= 75000));
        System.out.println("Partitioned by salary >= 75000: High=" + highEarners.get(true).size()
                + ", Regular=" + highEarners.get(false).size());
    }

    // =========================================================================================
    // SECTION 8: ADVANCED COLLECTORS & JAVA 12+ TEEING
    // =========================================================================================
    private static void section8_AdvancedCollectorsAndTeeing() {
        printHeader("8. ADVANCED COLLECTORS & JAVA 12+ TEEING");

        List<Employee> emps = List.of(
                new Employee("Asha", "IT", 90000, 30),
                new Employee("Ravi", "IT", 70000, 25),
                new Employee("Meera", "HR", 50000, 40),
                new Employee("Karan", "HR", 65000, 35),
                new Employee("Sita", "Finance", 80000, 45)
        );

        // 1. collectingAndThen (Post-process the collector result):
        // Wrap collected list in an unmodifiable list or extract specific element
        List<String> unmodifiableNames = emps.stream()
                .map(Employee::name)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        System.out.println("collectingAndThen unmodifiable: " + unmodifiableNames);

        // Finding the maximum salary employee per department, returning unwrapped Employee directly:
        Map<String, Employee> topEarnerPerDept = emps.stream().collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingDouble(Employee::salary)),
                        Optional::get // Safely unwrap Optional<Employee>
                )
        ));
        System.out.println("Top earner per department (unwrapped): " + topEarnerPerDept);

        // 2. Java 9+ Collectors.filtering:
        // Unlike stream.filter() (which removes the key from the map entirely if no element matches),
        // Collectors.filtering keeps the department key with an empty list!
        Map<String, List<Employee>> seniorByDept = emps.stream().collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.filtering(e -> e.age() >= 40, Collectors.toList())
        ));
        System.out.println("groupingBy with filtering (age >= 40): " + seniorByDept);

        // 3. Java 12+ Collectors.teeing (Two collectors in a single pass, merged with a BiFunction!):
        // Problem: How to compute count AND average salary in ONE single pass through the stream?
        // Prior to teeing, this required two stream passes or a custom collector.
        String summary = emps.stream().collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.averagingDouble(Employee::salary),
                (count, avg) -> String.format("Total: %d employees, Average Salary: $%.2f", count, avg)
        ));
        System.out.println("Collectors.teeing single-pass summary: " + summary);
    }

    // =========================================================================================
    // SECTION 9: CUSTOM COLLECTORS (Collector.of)
    // =========================================================================================
    private static void section9_CustomCollectors() {
        printHeader("9. CUSTOM COLLECTORS (Collector.of)");
        /*
         * The 4 Core Functions of a Collector<T, A, R>:
         *  1. Supplier:       () -> A          (Creates new mutable result container)
         *  2. Accumulator:    (A, T) -> void   (Folds an element into container)
         *  3. Combiner:       (A, A) -> A      (Merges two containers together in parallel streams)
         *  4. Finisher:       A -> R           (Transforms container into final result)
         *  5. Characteristics: IDENTITY_FINISH, CONCURRENT, UNORDERED
         */

        // Custom Collector 1: Collect strings into an immutable List
        Collector<String, List<String>, List<String>> toImmutableList = Collector.of(
                ArrayList::new,                                    // Supplier
                List::add,                                         // Accumulator
                (left, right) -> { left.addAll(right); return left; }, // Combiner
                Collections::unmodifiableList                      // Finisher
        );

        List<String> immutableResult = Stream.of("Spring", "Quarkus", "Micronaut")
                .collect(toImmutableList);
        System.out.println("Custom toImmutableList collector: " + immutableResult);

        // Custom Collector 2: Joining strings with custom prefix/suffix and uppercase transformation
        Collector<String, StringJoiner, String> customJoiner = Collector.of(
                () -> new StringJoiner(" ~ ", "<< ", " >>"),
                (joiner, str) -> joiner.add(str.toUpperCase()),
                StringJoiner::merge,
                StringJoiner::toString
        );

        String customJoined = Stream.of("alpha", "beta", "gamma").collect(customJoiner);
        System.out.println("Custom customJoiner collector: " + customJoined);
    }

    // =========================================================================================
    // SECTION 10: PARALLEL STREAMS & FORKJOINPOOL
    // =========================================================================================
    private static void section10_ParallelStreamsAndForkJoin() {
        printHeader("10. PARALLEL STREAMS & THE FORKJOINPOOL");
        /*
         * HOW PARALLEL STREAMS WORK:
         *  - Uses a Spliterator to recursively divide (fork) data into halves.
         *  - Sub-tasks are dispatched to the common ForkJoinPool (Runtime.getRuntime().availableProcessors()).
         *  - Sub-results are merged back together using the Collector's combiner function.
         *
         * WHEN TO USE PARALLEL STREAMS (THE N*Q RULE):
         *  - N = Number of elements
         *  - Q = Computational cost per element
         *  - Rule of Thumb: Use parallel streams ONLY if N * Q > 10,000!
         *
         * WHEN NOT TO USE PARALLEL STREAMS:
         *  - Data source splits poorly (e.g. LinkedList or Stream.iterate is O(n) to split!).
         *    ArrayList and int[] split in O(1) time and are ideal for parallel streams.
         *  - Boxing / Unboxing overhead dominates computation.
         *  - Code modifies shared mutable state (Causes race conditions!).
         *  - I/O bound operations (Blocking the shared common ForkJoinPool stalls other tasks!).
         */

        // Measuring Parallel vs Sequential Sum on ArrayList:
        List<Integer> largeList = IntStream.rangeClosed(1, 1_000_000).boxed().toList();

        long startSeq = System.currentTimeMillis();
        long sumSeq = largeList.stream().mapToLong(i -> (long) i * 2).sum();
        long seqTime = System.currentTimeMillis() - startSeq;

        long startPar = System.currentTimeMillis();
        long sumPar = largeList.parallelStream().mapToLong(i -> (long) i * 2).sum();
        long parTime = System.currentTimeMillis() - startPar;

        System.out.printf("Sum 1M items -> Sequential: %d ms | Parallel: %d ms (Sums match? %b)%n",
                seqTime, parTime, (sumSeq == sumPar));

        // forEach vs forEachOrdered:
        System.out.print("parallelStream().forEach (Non-deterministic order): ");
        List.of(1, 2, 3, 4, 5, 6, 7, 8).parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("parallelStream().forEachOrdered (Preserves encounter order): ");
        List.of(1, 2, 3, 4, 5, 6, 7, 8).parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
    }

    // =========================================================================================
    // SECTION 11: TOP 12 REAL-WORLD & INTERVIEW STREAM PROBLEMS
    // =========================================================================================
    private static void section11_Top12InterviewStreamProblems() {
        printHeader("11. TOP 12 REAL-WORLD & INTERVIEW STREAM PROBLEMS");

        List<Employee> emps = List.of(
                new Employee("Asha", "IT", 90000, 30),
                new Employee("Ravi", "IT", 70000, 25),
                new Employee("Rahul", "IT", 85000, 28),
                new Employee("Meera", "HR", 50000, 40),
                new Employee("Karan", "HR", 65000, 35),
                new Employee("Sita", "Finance", 80000, 45)
        );

        // PROBLEM 1: Second Highest Salary in Each Department
        System.out.println("Problem 1: Second Highest Salary in Each Department");
        Map<String, Optional<Employee>> secondHighestPerDept = emps.stream().collect(
                Collectors.groupingBy(
                        Employee::dept,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                                        .skip(1)
                                        .findFirst()
                        )
                )
        );
        secondHighestPerDept.forEach((dept, emp) ->
                System.out.println("   " + dept + " -> " + emp.map(e -> e.name() + " ($" + e.salary() + ")").orElse("N/A")));

        // PROBLEM 2: Highest Paid Employee per Department
        System.out.println("\nProblem 2: Highest Paid Employee per Department");
        Map<String, Employee> highestPaidPerDept = emps.stream().collect(Collectors.toMap(
                Employee::dept,
                Function.identity(),
                BinaryOperator.maxBy(Comparator.comparingDouble(Employee::salary))
        ));
        highestPaidPerDept.forEach((dept, emp) ->
                System.out.println("   " + dept + " -> " + emp.name() + " ($" + emp.salary() + ")"));

        // PROBLEM 3: Word Frequency Counter
        System.out.println("\nProblem 3: Word Frequency Counter");
        String sentence = "to be or not to be that is the question";
        Map<String, Long> wordFrequencies = Arrays.stream(sentence.split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("   Frequencies: " + wordFrequencies);

        // PROBLEM 4: First Non-Repeating Character in a String
        System.out.println("\nProblem 4: First Non-Repeating Character");
        String text = "swiss";
        Optional<Character> firstNonRepeating = text.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == 1L)
                .map(Map.Entry::getKey)
                .findFirst();
        System.out.println("   First non-repeating char in '" + text + "': " + firstNonRepeating.orElse('?')); // 'w'

        // PROBLEM 5: Find Duplicate Elements in a List
        System.out.println("\nProblem 5: Find Duplicate Elements");
        List<Integer> numbersWithDups = List.of(1, 2, 3, 4, 2, 5, 6, 3, 7, 8, 1);
        Set<Integer> seen = new HashSet<>();
        List<Integer> duplicates = numbersWithDups.stream()
                .filter(n -> !seen.add(n)) // Set.add returns false if element already exists!
                .distinct()
                .toList();
        System.out.println("   Duplicates in " + numbersWithDups + " -> " + duplicates);

        // PROBLEM 6: Sum of Squares of Even Numbers
        System.out.println("\nProblem 6: Sum of Squares of Even Numbers (1..10)");
        int sumOfEvenSquares = IntStream.rangeClosed(1, 10)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .sum();
        System.out.println("   Result: " + sumOfEvenSquares); // 2^2 + 4^2 + 6^2 + 8^2 + 10^2 = 220

        // PROBLEM 7: Palindrome Check using IntStream
        System.out.println("\nProblem 7: Palindrome Check using IntStream");
        String candidate = "radar";
        boolean isPalindrome = IntStream.range(0, candidate.length() / 2)
                .allMatch(i -> candidate.charAt(i) == candidate.charAt(candidate.length() - 1 - i));
        System.out.println("   Is '" + candidate + "' a palindrome? " + isPalindrome);

        // PROBLEM 8: Merge Two Lists Without Duplicates (Sorted)
        System.out.println("\nProblem 8: Merge Two Lists Without Duplicates (Sorted)");
        List<Integer> listA = List.of(5, 1, 9, 3);
        List<Integer> listB = List.of(2, 9, 4, 1, 8);
        List<Integer> mergedSorted = Stream.concat(listA.stream(), listB.stream())
                .distinct()
                .sorted()
                .toList();
        System.out.println("   Merged & Sorted: " + mergedSorted); // [1, 2, 3, 4, 5, 8, 9]

        // PROBLEM 9: Longest Word in a String
        System.out.println("\nProblem 9: Longest Word in a Sentence");
        String quote = "I am learning functional stream pipelines in modern Java";
        String longestWord = Arrays.stream(quote.split("\\s+"))
                .reduce((w1, w2) -> w1.length() >= w2.length() ? w1 : w2)
                .orElse("");
        System.out.println("   Longest word: '" + longestWord + "' (length " + longestWord.length() + ")");

        // PROBLEM 10: N-th Highest Salary (e.g. N = 3)
        System.out.println("\nProblem 10: N-th Highest Salary (N = 3)");
        int n = 3;
        Optional<Double> nthSalary = emps.stream()
                .map(Employee::salary)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(n - 1)
                .findFirst();
        System.out.println("   " + n + "rd highest salary: $" + nthSalary.orElse(0.0));

        // PROBLEM 11: Group Names by First Letter
        System.out.println("\nProblem 11: Group Names by First Letter");
        List<String> names = List.of("Alice", "Bob", "Charlie", "Amy", "Brian", "David");
        Map<Character, List<String>> byFirstLetter = names.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("   Grouped: " + byFirstLetter);

        // PROBLEM 12: Comma-Separated Employee Names per Department
        System.out.println("\nProblem 12: Comma-Separated Employee Names per Department");
        Map<String, String> namesPerDept = emps.stream().collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.mapping(Employee::name, Collectors.joining(", "))
        ));
        namesPerDept.forEach((dept, nameStr) -> System.out.println("   " + dept + ": " + nameStr));
    }

    // =========================================================================================
    // SECTION 12: STREAM BEST PRACTICES, ANTI-PATTERNS & DEBUGGING
    // =========================================================================================
    private static void section12_BestPracticesAndAntiPatterns() {
        printHeader("12. STREAM BEST PRACTICES, ANTI-PATTERNS & DEBUGGING");
        /*
         *  DO'S:
         *   1. Keep streams pure: Functions inside filter/map must NOT mutate shared state.
         *   2. Favor primitive streams (IntStream, LongStream) over Stream<Integer> for numeric data.
         *   3. Use Stream.toList() (Java 16+) for immutable result lists.
         *   4. Use method references (String::toUpperCase) for readable code.
         *   5. Use peek() ONLY for temporary logging/debugging, NEVER for business logic!
         *
         *  DON'TS:
         *   1. DO NOT mutate collections from within streams:
         *      BAD:  list.stream().filter(...).forEach(results::add); // Race condition in parallel!
         *      GOOD: List<String> results = list.stream().filter(...).toList();
         *   2. DO NOT use parallel streams blindly: Benchmarking is essential (check N * Q > 10,000).
         *   3. DO NOT create streams inside tight loops: Stream creation has slight allocation cost.
         *   4. DO NOT write unreadable 20-line stream pipelines: Break into named helper functions.
         */

        // Debugging with peek():
        System.out.println("Debugging Stream Pipeline with peek():");
        List<String> debugged = Stream.of("one", "two", "three", "four")
                .filter(s -> s.length() > 3)
                .peek(filtered -> System.out.println("   [DEBUG after filter] passed: " + filtered))
                .map(String::toUpperCase)
                .peek(mapped -> System.out.println("   [DEBUG after map] transformed: " + mapped))
                .toList();
        System.out.println("Final debugged list: " + debugged);
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}

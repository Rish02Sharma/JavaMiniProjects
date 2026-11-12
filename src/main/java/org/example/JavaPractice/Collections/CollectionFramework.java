package org.example.JavaPractice.Collections;

import java.util.*;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * =============================================================================================
 *                   MASTERCLASS: JAVA COLLECTIONS FRAMEWORK (JCF)
 * =============================================================================================
 * 
 * This file serves as a comprehensive, hands-on, runnable documentation and guide for mastering
 * the Java Collections Framework (from beginner fundamentals to advanced internals & Java 21).
 * 
 * TABLE OF CONTENTS:
 *  1. JCF Architecture & Hierarchy (Collection vs Map, Immutability Spectrum)
 *  2. The List Family (ArrayList, LinkedList, Vector, Stack & Cache Locality)
 *  3. The Set Family (HashSet, LinkedHashSet, TreeSet, EnumSet & Hash Contracts)
 *  4. The Queue & Deque Family (FIFO, ArrayDeque, PriorityQueue, LIFO Stacks)
 *  5. The Map Family (HashMap, LinkedHashMap, TreeMap, WeakHashMap, EnumMap)
 *  6. Modern Functional Map & Collection Operations (computeIfAbsent, merge, removeIf)
 *  7. Java 21 Sequenced Collections (SequencedCollection, SequencedSet, SequencedMap)
 *  8. Iteration Mechanics: Fail-Fast vs Fail-Safe & Avoiding ConcurrentModificationException
 *  9. The equals() and hashCode() Contract & The Mutable Key Trap
 * 10. Sorting Mastery: Comparable vs. Comparator (Chaining, Null-handling)
 * 11. Concurrent Collections (java.util.concurrent overview & Lock-free structures)
 * 12. Collections Utility Class Power Tools (binarySearch, disjoint, frequency, rotate)
 * 13. Real-World Architectural Patterns (LRU Cache, MultiMap, Top-K Heap)
 * 14. Big-O Complexity Master Reference & Decision Flowchart
 * =============================================================================================
 */
public class CollectionFramework {

    // -----------------------------------------------------------------------------------------
    // DOMAIN MODELS USED ACROSS EXAMPLES
    // -----------------------------------------------------------------------------------------
    public record Student(int id, String name, double gpa) implements Comparable<Student> {
        @Override
        public int compareTo(Student other) {
            // Default natural ordering: sort by ID ascending
            return Integer.compare(this.id, other.id);
        }
    }

    public record Item(String sku, String name, double price) {}

    // A mutable key class created specifically to demonstrate the dangerous "Mutable Key Trap"
    public static class MutableKey {
        private String identifier;

        public MutableKey(String identifier) {
            this.identifier = identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MutableKey that = (MutableKey) o;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return "Key(" + identifier + ")";
        }
    }

    // -----------------------------------------------------------------------------------------
    // MAIN METHOD: EXECUTES ALL SECTIONS SEQUENTIALLY
    // -----------------------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("      JAVA COLLECTIONS FRAMEWORK: BEGINNER TO ADVANCED GUIDE       ");
        System.out.println("===================================================================\n");

        section1_ArchitectureAndImmutability();
        section2_ListFamily();
        section3_SetFamily();
        section4_QueueAndDequeFamily();
        section5_MapFamily();
        section6_ModernFunctionalOperations();
        section7_Java21SequencedCollections();
        section8_IterationMechanics();
        section9_EqualsAndHashCodeTrap();
        section10_SortingComparableComparator();
        section11_ConcurrentCollections();
        section12_CollectionsUtilityGems();
        section13_RealWorldPatterns();
        section14_ComplexityAndDecisionGuide();

        System.out.println("\n===================================================================");
        System.out.println("             ALL COLLECTION LESSONS COMPLETED SUCCESSFULLY!        ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: JCF ARCHITECTURE & IMMUTABILITY SPECTRUM
    // =========================================================================================
    private static void section1_ArchitectureAndImmutability() {
        printHeader("1. JCF ARCHITECTURE & THE IMMUTABILITY SPECTRUM");
        /*
         * CORE INTERFACE HIERARCHY:
         *
         *           java.lang.Iterable<T>
         *                    |
         *         java.util.Collection<E>                   java.util.Map<K, V> (Separate!)
         *          /          |          \                          /            \
         *     List<E>      Set<E>     Queue<E>                 HashMap        SortedMap
         *        |            |           |                                       |
         *   ArrayList     HashSet      Deque<E>                                TreeMap
         *   LinkedList   TreeSet          |
         *                            ArrayDeque
         *
         * CRUCIAL INTERVIEW QUESTION: Why does Map NOT extend Collection?
         *  - Collection holds individual elements (E).
         *  - Map holds key-value pairs (K, V) and has completely different semantics
         *    (e.g., put(k,v) vs add(e), keySet(), values(), entrySet()).
         *  - Forcing Map to extend Collection would violate the Liskov Substitution Principle (LSP).
         */

        // 1. MUTABLE Collections (Classic pre-Java 9):
        List<String> mutableList = new ArrayList<>();
        mutableList.add("Java");
        mutableList.add("Python");
        mutableList.set(0, "Modern Java"); // Allowed
        System.out.println("Mutable List: " + mutableList);

        // 2. UNMODIFIABLE VIEW (Collections.unmodifiableList):
        // Note: It's an unmodifiable VIEW backed by the original list. If the original list
        // mutates, the view reflects those changes!
        List<String> backingList = new ArrayList<>(List.of("A", "B"));
        List<String> unmodifiableView = Collections.unmodifiableList(backingList);
        // unmodifiableView.add("C"); // Throws UnsupportedOperationException at runtime!
        backingList.add("C"); // Mutating underlying list
        System.out.println("Unmodifiable view reflects underlying changes: " + unmodifiableView); // [A, B, C]

        // 3. TRULY IMMUTABLE FACTORY METHODS (Java 9+ List.of, Set.of, Map.of):
        //  - Compact internal footprint (no overhead of ArrayList wrapper).
        //  - Null elements are STRICTLY DISALLOWED (throws NullPointerException immediately).
        //  - Order is randomized in Set.of and Map.of to prevent relying on iteration order.
        List<String> trulyImmutableList = List.of("Spring", "Quarkus", "Micronaut");
        Map<String, Integer> immutableMap = Map.of("Port", 8080, "Timeout", 3000);
        System.out.println("Immutable List (List.of): " + trulyImmutableList);
        System.out.println("Immutable Map (Map.of):   " + immutableMap);

        // Shallow vs Deep Immutability Caveat:
        // List.of prevents adding/removing elements, but if the elements themselves are mutable,
        // their inner state CAN still be changed!
        class Container { int val = 10; }
        Container c = new Container();
        List<Container> shallowList = List.of(c);
        shallowList.get(0).val = 99; // MUTATES internal element state!
        System.out.println("Shallow immutability warning - inner object mutated: " + shallowList.get(0).val);
    }

    // =========================================================================================
    // SECTION 2: THE LIST FAMILY (ArrayList, LinkedList, Vector, Stack)
    // =========================================================================================
    private static void section2_ListFamily() {
        printHeader("2. THE LIST FAMILY & HARDWARE CACHE LOCALITY");
        /*
         *  ArrayList vs LinkedList Comparison:
         *
         *  Feature               | ArrayList                    | LinkedList
         *  ----------------------|------------------------------|-----------------------------
         *  Underlying Structure  | Resizable dynamic Object[]   | Doubly-linked Node<E>
         *  Random Access (get)   | O(1) direct offset           | O(n) must traverse nodes
         *  Append (add at end)   | Amortized O(1)               | O(1)
         *  Insert/Delete at Head | O(n) array shift (System.arraycopy) | O(1) re-link pointers
         *  Memory Overhead       | Low (contiguous array)       | High (24-32 bytes per node!)
         *  CPU Cache Locality    | EXCELLENT (sequential RAM)   | TERRIBLE (random heap pointers)
         *
         * HARDWARE REALITY: Modern CPUs fetch data in 64-byte Cache Lines.
         * ArrayList elements sit contiguously in memory, leading to massive L1/L2 cache hits.
         * LinkedList nodes are scattered randomly across the heap, causing L1/L2 cache misses.
         * IN 99% OF REAL-WORLD APPLICATIONS, ArrayList BEATS LinkedList EVEN FOR INSERTIONS!
         */

        // ArrayList resizing formula (Java 8+):
        // newCapacity = oldCapacity + (oldCapacity >> 1)  => Grows by 50% (1.5x)
        ArrayList<Integer> arrayList = new ArrayList<>(10); // Initial capacity avoids early resizes
        arrayList.add(100);
        arrayList.add(200);
        arrayList.add(300);
        System.out.println("ArrayList element at index 1 (O(1)): " + arrayList.get(1));

        // SubList View Trap:
        // subList(from, to) creates a VIEW, not a copy!
        List<Integer> sub = arrayList.subList(0, 2); // [100, 200]
        sub.set(0, 999); // Modifies original arrayList!
        System.out.println("Parent list after subList modification: " + arrayList); // [999, 200, 300]
        // Note: Structurally modifying arrayList (like arrayList.add(400)) invalidates 'sub',
        // causing ConcurrentModificationException on next access to 'sub'!

        // LinkedList as a Deque:
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.addFirst("First");
        linkedList.addLast("Last");
        System.out.println("LinkedList acting as double-ended queue: " + linkedList);

        // Vector & Stack (Legacy - DO NOT USE IN NEW CODE):
        // Vector synchronizes EVERY method call (massive performance overhead).
        // Stack extends Vector (violating composition, exposing index-based get/set).
        // Standard Modern Alternative: Use ArrayDeque for LIFO Stacks!
    }

    // =========================================================================================
    // SECTION 3: THE SET FAMILY (HashSet, LinkedHashSet, TreeSet, EnumSet)
    // =========================================================================================
    private static void section3_SetFamily() {
        printHeader("3. THE SET FAMILY & UNIQUENESS GUARANTEES");
        /*
         *  Set Implementations:
         *   - HashSet:       O(1) lookup. Backed internally by a HashMap (dummy value Object).
         *                    NO order guarantee.
         *   - LinkedHashSet: O(1) lookup. Maintains INSERTION ORDER via doubly-linked bucket chain.
         *   - TreeSet:       O(log n) lookup. Backed by Red-Black Tree (TreeMap).
         *                    Maintains elements in SORTED order (Comparable or Comparator).
         *   - EnumSet:       High-performance bit-vector implementation for enums.
         *                    Zero heap allocation per element (uses single 'long' bitmask).
         */

        // 1. HashSet (Unordered, fast):
        Set<String> hashSet = new HashSet<>(List.of("Banana", "Apple", "Mango", "Orange"));
        System.out.println("HashSet (arbitrary order): " + hashSet);

        // 2. LinkedHashSet (Predictable iteration order):
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("First");
        linkedHashSet.add("Second");
        linkedHashSet.add("Third");
        System.out.println("LinkedHashSet (maintains insertion order): " + linkedHashSet);

        // 3. TreeSet (Sorted order, NavigableSet interface):
        NavigableSet<Integer> treeSet = new TreeSet<>(List.of(40, 10, 90, 25, 70));
        System.out.println("TreeSet (automatically sorted): " + treeSet);
        System.out.println("TreeSet.lower(40)   [strictly < 40]:  " + treeSet.lower(40));   // 25
        System.out.println("TreeSet.floor(40)   [<= 40]:          " + treeSet.floor(40));   // 40
        System.out.println("TreeSet.ceiling(45) [>= 45]:          " + treeSet.ceiling(45)); // 70
        System.out.println("TreeSet.higher(70)  [strictly > 70]:  " + treeSet.higher(70));  // 90
        System.out.println("TreeSet subSet [25, 70):              " + treeSet.subSet(25, 70)); // [25, 40]

        // 4. EnumSet (The fastest set in Java):
        enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }
        EnumSet<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
        EnumSet<Day> weekdays = EnumSet.complementOf(weekend);
        EnumSet<Day> workWeek = EnumSet.range(Day.MON, Day.FRI);
        System.out.println("EnumSet weekdays: " + weekdays);
        System.out.println("EnumSet workWeek: " + workWeek);
    }

    // =========================================================================================
    // SECTION 4: THE QUEUE & DEQUE FAMILY (FIFO, LIFO, PriorityQueue, ArrayDeque)
    // =========================================================================================
    private static void section4_QueueAndDequeFamily() {
        printHeader("4. THE QUEUE & DEQUE FAMILY");
        /*
         * QUEUE METHODS SUMMARY:
         * -------------------------------------------------------------
         * Operation | Throws Exception | Returns Special Value (null/false)
         * ----------|------------------|------------------------------
         * Insert    | add(e)           | offer(e)
         * Remove    | remove()         | poll()
         * Examine   | element()        | peek()
         * -------------------------------------------------------------
         *
         * ArrayDeque vs LinkedList:
         *  - ArrayDeque is implemented as a circular resizable array.
         *  - Never allocates nodes on push/pop.
         *  - DOES NOT ALLOW NULL ELEMENTS (throws NPE).
         *  - Preferred as both Queue (FIFO) and Stack (LIFO)!
         */

        // 1. ArrayDeque as a LIFO Stack (Replacing legacy java.util.Stack):
        Deque<String> stack = new ArrayDeque<>();
        stack.push("Base Page");
        stack.push("Product Page");
        stack.push("Checkout Page");
        System.out.println("Stack peek: " + stack.peek()); // Checkout Page
        System.out.println("Stack popped: " + stack.pop()); // Checkout Page
        System.out.println("Stack remaining: " + stack);

        // 2. ArrayDeque as a FIFO Queue:
        Queue<String> fifoQueue = new ArrayDeque<>();
        fifoQueue.offer("Customer 1");
        fifoQueue.offer("Customer 2");
        System.out.println("FIFO polled: " + fifoQueue.poll()); // Customer 1
        System.out.println("FIFO next:   " + fifoQueue.peek()); // Customer 2

        // 3. PriorityQueue (Min-Heap / Max-Heap):
        // Elements ordered by natural order or custom Comparator.
        // NOTE: Iterating with for-each does NOT show elements in sorted order!
        // Elements are only guaranteed sorted when extracted one-by-one via poll()!
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // Default: Min-Heap
        minHeap.addAll(List.of(50, 10, 80, 20, 5));

        System.out.print("PriorityQueue polling in min-heap priority order: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " "); // 5 10 20 50 80
        }
        System.out.println();

        // Max-Heap PriorityQueue:
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.addAll(List.of(50, 10, 80, 20, 5));
        System.out.println("Max-Heap peek (highest priority element): " + maxHeap.peek()); // 80
    }

    // =========================================================================================
    // SECTION 5: THE MAP FAMILY (HashMap, LinkedHashMap, TreeMap, Special Maps)
    // =========================================================================================
    private static void section5_MapFamily() {
        printHeader("5. THE MAP FAMILY & INTERNALS");
        /*
         *  HashMap Architecture:
         *   - Array of buckets: Node<K, V>[] table
         *   - Default Initial Capacity: 16
         *   - Load Factor: 0.75 (rehashes/doubles table size when 75% full)
         *   - Hash spreading function: (h ^ (h >>> 16)) minimizes bucket collisions.
         *   - Bucket index formula: index = (n - 1) & hash
         *   - Treeification (Java 8+):
         *       If a single bucket gets >= 8 nodes (TREEIFY_THRESHOLD)
         *       AND total table capacity >= 64 (MIN_TREEIFY_CAPACITY),
         *       the linked list converts into a Red-Black Tree (TreeNode),
         *       slashing worst-case search from O(n) to O(log n)!
         */

        // 1. HashMap:
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Java", 1995);
        hashMap.put("Rust", 2010);
        hashMap.put("Go", 2009);
        System.out.println("HashMap entries: " + hashMap);

        // 2. LinkedHashMap (Insertion order vs Access order):
        // accessOrder = true enables LRU (Least Recently Used) caching behavior!
        Map<String, String> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        accessOrderMap.put("A", "Apple");
        accessOrderMap.put("B", "Banana");
        accessOrderMap.put("C", "Cherry");

        accessOrderMap.get("A"); // Access "A" -> moves "A" to the very end of the iteration order!
        System.out.println("LinkedHashMap access-order (accessed 'A' moved to end): " + accessOrderMap.keySet()); // [B, C, A]

        // 3. TreeMap (NavigableMap, sorted keys):
        NavigableMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(100, "Bronze");
        treeMap.put(500, "Silver");
        treeMap.put(1000, "Gold");
        treeMap.put(5000, "Platinum");

        System.out.println("TreeMap lowerEntry(500)  [< 500]:  " + treeMap.lowerEntry(500));  // 100=Bronze
        System.out.println("TreeMap floorEntry(500)  [<= 500]: " + treeMap.floorEntry(500));  // 500=Silver
        System.out.println("TreeMap ceilingEntry(750) [>= 750]: " + treeMap.ceilingEntry(750)); // 1000=Gold

        // 4. IdentityHashMap (Uses reference equality == instead of .equals()):
        Map<String, String> identityMap = new IdentityHashMap<>();
        String k1 = new String("key");
        String k2 = new String("key");
        identityMap.put(k1, "Val1");
        identityMap.put(k2, "Val2"); // Treated as TWO DIFFERENT KEYS because k1 != k2 in memory!
        System.out.println("IdentityHashMap size (distinct object references): " + identityMap.size()); // 2

        // 5. WeakHashMap (Entries whose keys are no longer strongly referenced get auto-garbage-collected):
        Map<Object, String> weakMap = new WeakHashMap<>();
        Object strongKey = new Object();
        weakMap.put(strongKey, "Cached metadata");
        System.out.println("WeakHashMap before key dereference: " + weakMap.size()); // 1
        strongKey = null; // Key is eligible for GC
        System.gc();      // Hint JVM to run garbage collector
        System.out.println("WeakHashMap after GC (entry automatically purged): " + weakMap.size()); // Typically 0

        // 6. EnumMap (Extremely fast array-based lookup using enum ordinals):
        enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
        EnumMap<Priority, String> alertMap = new EnumMap<>(Priority.class);
        alertMap.put(Priority.CRITICAL, "Page On-Call Engineer Immediately");
        alertMap.put(Priority.LOW, "Log to file");
        System.out.println("EnumMap entry: " + alertMap.get(Priority.CRITICAL));
    }

    // =========================================================================================
    // SECTION 6: MODERN FUNCTIONAL OPERATIONS (JAVA 8+)
    // =========================================================================================
    private static void section6_ModernFunctionalOperations() {
        printHeader("6. MODERN FUNCTIONAL OPERATIONS ON COLLECTIONS & MAPS");

        // 1. Map.computeIfAbsent: Evaluates mapping function only if key is missing (Lazy Init / MultiMap)
        Map<String, List<String>> multiMap = new HashMap<>();
        multiMap.computeIfAbsent("IT", k -> new ArrayList<>()).add("Alice");
        multiMap.computeIfAbsent("IT", k -> new ArrayList<>()).add("Bob");
        multiMap.computeIfAbsent("HR", k -> new ArrayList<>()).add("Carol");
        System.out.println("MultiMap using computeIfAbsent: " + multiMap);

        // 2. Map.merge: The Swiss Army Knife for aggregations and word counts!
        // map.merge(key, value, (existingVal, newVal) -> combinedVal)
        Map<String, Integer> wordCounts = new HashMap<>();
        List<String> words = List.of("apple", "banana", "apple", "cherry", "banana", "apple");
        for (String w : words) {
            wordCounts.merge(w, 1, Integer::sum);
        }
        System.out.println("Word counts using Map.merge: " + wordCounts);

        // 3. Map.computeIfPresent & compute:
        wordCounts.computeIfPresent("banana", (k, v) -> v + 10);
        System.out.println("Map after computeIfPresent (banana += 10): " + wordCounts);

        // 4. Map.getOrDefault:
        int mangoCount = wordCounts.getOrDefault("mango", 0);
        System.out.println("getOrDefault for missing key 'mango': " + mangoCount);

        // 5. Collection.removeIf (In-place predicate removal without CME):
        List<Integer> scoreList = new ArrayList<>(List.of(85, 42, 90, 55, 30, 99));
        scoreList.removeIf(score -> score < 60); // Removes failing grades
        System.out.println("Filtered list using removeIf (< 60 removed): " + scoreList);

        // 6. List.replaceAll (In-place transformation):
        List<String> names = new ArrayList<>(List.of("charlie", "bob", "alice"));
        names.replaceAll(String::toUpperCase);
        System.out.println("List after replaceAll toUpperCase: " + names);
    }

    // =========================================================================================
    // SECTION 7: JAVA 21 SEQUENCED COLLECTIONS (JEP 431)
    // =========================================================================================
    private static void section7_Java21SequencedCollections() {
        printHeader("7. JAVA 21 SEQUENCED COLLECTIONS (JEP 431)");
        /*
         * THE HISTORICAL PROBLEM IN JAVA:
         * Before Java 21, getting the first and last element of a collection was fragmented:
         *   - List had: list.get(0) and list.get(list.size() - 1)
         *   - Deque had: deque.getFirst() and deque.getLast()
         *   - SortedSet had: set.first() and set.last()
         *   - LinkedHashSet had: set.iterator().next() (no way to get last without iteration!)
         *   - Reverse iteration was totally inconsistent.
         *
         * JAVA 21 SOLUTION: JEP 431 introduced 3 unified interfaces:
         *   1. SequencedCollection<E>
         *   2. SequencedSet<E>
         *   3. SequencedMap<K, V>
         *
         * New Universal Methods:
         *   - addFirst(e), addLast(e)
         *   - getFirst(), getLast()
         *   - removeFirst(), removeLast()
         *   - reversed() (Returns a zero-copy reverse-ordered VIEW!)
         */

        // 1. SequencedCollection with ArrayList:
        SequencedCollection<String> seqList = new ArrayList<>(List.of("Alpha", "Beta", "Gamma"));
        seqList.addFirst("Zero");
        seqList.addLast("Omega");
        System.out.println("SequencedCollection: " + seqList);
        System.out.println("First element: " + seqList.getFirst()); // Zero
        System.out.println("Last element:  " + seqList.getLast());  // Omega

        // Reversed VIEW (Zero-copy reflection!):
        SequencedCollection<String> reversedView = seqList.reversed();
        System.out.println("Reversed View: " + reversedView); // [Omega, Gamma, Beta, Alpha, Zero]

        // 2. SequencedSet with LinkedHashSet:
        SequencedSet<Integer> seqSet = new LinkedHashSet<>(List.of(1, 2, 3));
        seqSet.addFirst(0);
        seqSet.addLast(4);
        System.out.println("SequencedSet (LinkedHashSet): " + seqSet);
        System.out.println("SequencedSet first: " + seqSet.getFirst() + ", last: " + seqSet.getLast());

        // 3. SequencedMap with LinkedHashMap:
        SequencedMap<String, Integer> seqMap = new LinkedHashMap<>();
        seqMap.putFirst("One", 1);
        seqMap.putLast("Two", 2);
        seqMap.putLast("Three", 3);
        System.out.println("SequencedMap: " + seqMap);
        System.out.println("First entry: " + seqMap.firstEntry()); // One=1
        System.out.println("Last entry:  " + seqMap.lastEntry());  // Three=3
        System.out.println("Reversed Map keys: " + seqMap.reversed().keySet()); // [Three, Two, One]
    }

    // =========================================================================================
    // SECTION 8: ITERATION MECHANICS (FAIL-FAST VS FAIL-SAFE VS WEAKLY CONSISTENT)
    // =========================================================================================
    private static void section8_IterationMechanics() {
        printHeader("8. ITERATION MECHANICS & CONCURRENT MODIFICATION EXCEPTION");
        /*
         * What is modCount and Fail-Fast?
         *  - Collections like ArrayList, HashSet, HashMap maintain an internal volatile int modCount.
         *  - Every time an element is structurally added/removed, modCount is incremented.
         *  - When an Iterator is created, it captures expectedModCount = modCount.
         *  - On every next() or remove(), if (modCount != expectedModCount),
         *    it throws ConcurrentModificationException (CME) immediately!
         *
         * Types of Iterators:
         *  1. Fail-Fast:         ArrayList, HashSet, HashMap (throws CME)
         *  2. Fail-Safe:         CopyOnWriteArrayList (iterates on a snapshot copy, never throws CME)
         *  3. Weakly Consistent: ConcurrentHashMap (reflects some updates, never throws CME)
         */

        List<String> items = new ArrayList<>(List.of("A", "B", "C", "D"));

        // DANGEROUS MISTAKE (Uncommenting throws ConcurrentModificationException):
        // for (String s : items) {
        //     if ("B".equals(s)) items.remove(s); // CME! Modifies collection behind iterator's back!
        // }

        // CORRECT WAY 1: Use Iterator.remove() (updates expectedModCount):
        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if ("B".equals(item)) {
                iterator.remove(); // Safe removal!
            }
        }
        System.out.println("List after safe Iterator.remove(): " + items); // [A, C, D]

        // CORRECT WAY 2 (Modern & Preferred): Collection.removeIf()
        items.removeIf("C"::equals);
        System.out.println("List after removeIf: " + items); // [A, D]

        // Fail-Safe Iterator demonstration with CopyOnWriteArrayList:
        List<String> cowList = new CopyOnWriteArrayList<>(List.of("X", "Y", "Z"));
        for (String s : cowList) {
            if ("Y".equals(s)) {
                cowList.add("NEW"); // Allowed! Iterator iterates on the snapshot created at loop start
            }
        }
        System.out.println("CopyOnWriteArrayList after mutation during iteration: " + cowList);
    }

    // =========================================================================================
    // SECTION 9: THE equals() AND hashCode() CONTRACT & THE MUTABLE KEY TRAP
    // =========================================================================================
    private static void section9_EqualsAndHashCodeTrap() {
        printHeader("9. EQUALS & HASHCODE CONTRACT & THE MUTABLE KEY TRAP");
        /*
         * THE GOLDEN CONTRACT:
         * 1. Reflexive, Symmetric, Transitive, Consistent.
         * 2. If a.equals(b) == true, then a.hashCode() MUST equal b.hashCode()!
         * 3. If a.hashCode() == b.hashCode(), a.equals(b) does NOT have to be true (Hash collision).
         *
         * What happens if hashCode() always returns a constant (e.g. 42)?
         *  - ALL elements end up in bucket index 0!
         *  - HashMap lookup degrades from O(1) to O(n) or O(log n)!
         */

        // THE DANGEROUS MUTABLE KEY TRAP:
        // Rule: NEVER USE A MUTABLE OBJECT AS A HASHMAP KEY OR HASHSET ELEMENT!
        Map<MutableKey, String> dangerousMap = new HashMap<>();
        MutableKey key = new MutableKey("user-101");
        dangerousMap.put(key, "Active Session Data");

        System.out.println("Key present before mutation: " + dangerousMap.containsKey(key)); // true

        // Now mutate the key's state:
        key.setIdentifier("user-999"); // Changes key.hashCode()!

        // The key is now in the WRONG BUCKET inside the HashMap table!
        System.out.println("Key present after mutation:  " + dangerousMap.containsKey(key)); // FALSE!
        System.out.println("dangerousMap.get(key):        " + dangerousMap.get(key));         // null!
        System.out.println("Map size (entry is still inside, but unfindable!): " + dangerousMap.size()); // 1

        // Moral: Always use immutable objects (like String, Integer, or Java Records) as Map keys!
    }

    // =========================================================================================
    // SECTION 10: SORTING MASTERY (Comparable vs Comparator)
    // =========================================================================================
    private static void section10_SortingComparableComparator() {
        printHeader("10. SORTING MASTERY: Comparable vs. Comparator");
        /*
         *  Comparable<T> vs Comparator<T>:
         *   - Comparable<T>: Defines NATURAL ordering. Implemented BY the class itself (compareTo).
         *                    Only ONE natural order per class.
         *   - Comparator<T>: Defines CUSTOM / AD-HOC ordering. External to the class.
         *                    Can define infinitely many sorting strategies!
         */

        List<Student> students = new ArrayList<>(List.of(
                new Student(103, "Alice", 3.8),
                new Student(101, "Bob", 3.2),
                new Student(104, "Charlie", 3.8),
                new Student(102, "David", 3.9)
        ));

        // 1. Natural Sort via Comparable (by ID ascending):
        Collections.sort(students);
        System.out.println("Natural order sort (by ID):");
        students.forEach(s -> System.out.println("   " + s));

        // 2. Multi-level Fluent Comparator:
        // Sort by GPA DESCENDING, then by Name ASCENDING:
        Comparator<Student> studentComparator = Comparator
                .comparingDouble(Student::gpa).reversed()
                .thenComparing(Student::name);

        students.sort(studentComparator);
        System.out.println("\nCustom multi-level sort (GPA desc, Name asc):");
        students.forEach(s -> System.out.println("   " + s));

        // 3. Null-safe Sorting (nullsFirst / nullsLast):
        List<String> nullableNames = Arrays.asList("Zack", null, "Anna", "Mike", null);
        nullableNames.sort(Comparator.nullsFirst(String::compareToIgnoreCase));
        System.out.println("\nNull-safe sort (nullsFirst): " + nullableNames);
    }

    // =========================================================================================
    // SECTION 11: CONCURRENT COLLECTIONS (java.util.concurrent)
    // =========================================================================================
    private static void section11_ConcurrentCollections() {
        printHeader("11. CONCURRENT COLLECTIONS & HIGH-THROUGHPUT MULTITHREADING");
        /*
         * Why not Collections.synchronizedMap(hashMap)?
         *  - Collections.synchronizedMap locks the ENTIRE table on EVERY read and write.
         *  - Completely kills multithreaded throughput!
         *
         * Modern Concurrent Collections:
         *  1. java.util.concurrent.ConcurrentHashMap:
         *     - Reads are 100% lock-free via volatile field reads.
         *     - Writes lock ONLY the specific bucket head node using synchronized/CAS.
         *     - Multiple threads can write concurrently to different buckets!
         *  2. CopyOnWriteArrayList / CopyOnWriteArraySet:
         *     - Every write creates a fresh copy of the underlying array.
         *     - Ideal for Read-Heavy, Write-Rarely scenarios (e.g. Event Listeners).
         *  3. ConcurrentSkipListMap / Set:
         *     - Thread-safe sorted map/set based on Skip Lists (Lock-free O(log n)).
         *  4. BlockingQueue (ArrayBlockingQueue, LinkedBlockingQueue):
         *     - Thread-safe Producer-Consumer coordination with put() and take().
         */

        // Demonstrating thread-safe ConcurrentHashMap:
        java.util.concurrent.ConcurrentHashMap<String, Integer> concurrentMap = new java.util.concurrent.ConcurrentHashMap<>();
        concurrentMap.put("ActiveConnections", 50);

        // Atomic update with compute:
        concurrentMap.compute("ActiveConnections", (k, v) -> (v == null) ? 1 : v + 1);
        System.out.println("ConcurrentHashMap atomic update: " + concurrentMap);

        // BlockingQueue Producer-Consumer pattern preview:
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        try {
            queue.put("Job-1"); // Blocks if queue is full
            queue.put("Job-2");
            System.out.println("BlockingQueue taken: " + queue.take()); // Blocks if queue is empty
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================================================================================
    // SECTION 12: COLLECTIONS UTILITY CLASS GEMS
    // =========================================================================================
    private static void section12_CollectionsUtilityGems() {
        printHeader("12. COLLECTIONS UTILITY CLASS POWER TOOLS");

        List<Integer> list = new ArrayList<>(List.of(10, 20, 30, 40, 50));

        // 1. Binary Search (Requires sorted list! O(log n)):
        int index = Collections.binarySearch(list, 30);
        System.out.println("Collections.binarySearch for 30: index " + index); // 2

        // 2. Disjoint (Checks if two collections have NO elements in common):
        List<String> setA = List.of("A", "B", "C");
        List<String> setB = List.of("D", "E", "F");
        List<String> setC = List.of("C", "X", "Y");
        System.out.println("Collections.disjoint(setA, setB) [No shared elements]: " + Collections.disjoint(setA, setB)); // true
        System.out.println("Collections.disjoint(setA, setC) [Shares 'C']:         " + Collections.disjoint(setA, setC)); // false

        // 3. Frequency:
        List<String> tags = List.of("java", "spring", "docker", "java", "k8s", "java");
        System.out.println("Collections.frequency of 'java': " + Collections.frequency(tags, "java")); // 3

        // 4. Rotate, Reverse, Shuffle:
        List<Integer> rotatable = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Collections.rotate(rotatable, 2); // Shifts elements forward by distance 2
        System.out.println("Collections.rotate by 2: " + rotatable); // [4, 5, 1, 2, 3]

        // 5. Singleton and nCopies:
        List<String> fixedRepeated = Collections.nCopies(3, "DEFAULT");
        System.out.println("Collections.nCopies (immutable repeated list): " + fixedRepeated);
    }

    // =========================================================================================
    // SECTION 13: REAL-WORLD ARCHITECTURAL PATTERNS
    // =========================================================================================
    private static void section13_RealWorldPatterns() {
        printHeader("13. REAL-WORLD ARCHITECTURAL PATTERNS WITH COLLECTIONS");

        // PATTERN 1: High-Performance LRU (Least Recently Used) Cache using LinkedHashMap
        System.out.println("Pattern 1: LRU Cache Implementation");
        class LruCache<K, V> extends LinkedHashMap<K, V> {
            private final int maxCapacity;

            public LruCache(int maxCapacity) {
                // initialCapacity, loadFactor, accessOrder=true
                super(maxCapacity, 0.75f, true);
                this.maxCapacity = maxCapacity;
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                // Auto-evicts the oldest entry when capacity is exceeded!
                return size() > maxCapacity;
            }
        }

        LruCache<String, String> cache = new LruCache<>(3);
        cache.put("user1", "John");
        cache.put("user2", "Sarah");
        cache.put("user3", "Mike");
        System.out.println("   Cache full (capacity 3): " + cache.keySet());

        cache.get("user1"); // Access user1 -> becomes most recently used
        cache.put("user4", "Emma"); // Exceeds capacity! Evicts eldest (user2)
        System.out.println("   Cache after accessing user1 and adding user4 (user2 evicted): " + cache.keySet()); // [user3, user1, user4]

        // PATTERN 2: Top-K Frequent Elements using PriorityQueue Min-Heap
        System.out.println("\nPattern 2: Top-K Frequent Elements (O(N log K))");
        List<String> logMessages = List.of("ERROR", "INFO", "WARN", "ERROR", "ERROR", "INFO", "DEBUG", "ERROR", "WARN");
        int k = 2; // Find top 2 most frequent log levels

        // Step 1: Count frequencies
        Map<String, Integer> freqMap = new HashMap<>();
        for (String msg : logMessages) {
            freqMap.merge(msg, 1, Integer::sum);
        }

        // Step 2: Keep min-heap of size K
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll(); // Evict lowest frequency element
            }
        }

        List<String> topK = minHeap.stream().map(Map.Entry::getKey).toList();
        System.out.println("   Top " + k + " frequent log levels: " + topK);
    }

    // =========================================================================================
    // SECTION 14: BIG-O COMPLEXITY REFERENCE & DECISION FLOWCHART
    // =========================================================================================
    private static void section14_ComplexityAndDecisionGuide() {
        printHeader("14. BIG-O COMPLEXITY REFERENCE & DECISION FLOWCHART");
        /*
         *  COLLECTION TIME COMPLEXITY CHEAT SHEET:
         *  =============================================================================
         *  Collection             | Get (Access)   | Insert (Add)   | Delete       | Notes
         *  -----------------------|----------------|----------------|--------------|-------------
         *  ArrayList              | O(1)           | Amortized O(1) | O(n)         | Contiguous memory, best default List
         *  LinkedList             | O(n)           | O(1) at head   | O(1) at head | High memory per node, bad cache locality
         *  ArrayDeque             | O(1)           | O(1)           | O(1)         | Best Stack and FIFO Queue
         *  HashSet                | O(1)           | O(1)           | O(1)         | Unordered, backed by HashMap
         *  LinkedHashSet          | O(1)           | O(1)           | O(1)         | Preserves insertion/access order
         *  TreeSet / TreeMap      | O(log n)       | O(log n)       | O(log n)     | Red-Black Tree, sorted keys
         *  HashMap                | O(1) avg       | O(1) avg       | O(1) avg     | O(log n) worst case if treeified
         *  PriorityQueue          | O(1) peek      | O(log n) offer | O(log n) poll| Binary Min-Heap
         *  =============================================================================
         *
         *  QUICK DECISION FLOWCHART:
         *  -------------------------
         *  1. Need Key-Value associations?
         *     -> Yes -> Sorted keys needed?
         *               -> Yes: TreeMap
         *               -> Insertion order needed?
         *                  -> Yes: LinkedHashMap
         *                  -> No:  HashMap (Default)
         *
         *  2. Need Unique elements only (No duplicates)?
         *     -> Yes -> Sorted order needed?
         *               -> Yes: TreeSet
         *               -> Insertion order needed?
         *                  -> Yes: LinkedHashSet
         *                  -> No:  HashSet (Default)
         *
         *  3. Need FIFO Queue or LIFO Stack?
         *     -> Yes -> ArrayDeque (Always choose over java.util.Stack and LinkedList!)
         *
         *  4. Need Ordered List with duplicates allowed?
         *     -> Yes -> ArrayList (Default for 99% of applications)
         */

        System.out.println("Quick Collection Selection Rules:");
        System.out.println(" - Default List:  ArrayList");
        System.out.println(" - Default Set:   HashSet (or LinkedHashSet if order matters)");
        System.out.println(" - Default Map:   HashMap (or LinkedHashMap for caches)");
        System.out.println(" - Default Queue: ArrayDeque (for both Stacks and Queues)");
        System.out.println(" - Priority:      PriorityQueue (Heaps)");
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


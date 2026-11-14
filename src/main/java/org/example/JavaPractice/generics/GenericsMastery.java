package org.example.JavaPractice.generics;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * =============================================================================================
 *          MASTERCLASS: JAVA GENERICS, TYPE ERASURE & ADVANCED TYPE SAFETY
 * =============================================================================================
 *
 * This file serves as a comprehensive, runnable guide and masterclass covering Java Generics
 * from foundational concepts to advanced type theory, bridge methods, and production design patterns:
 *
 * TABLE OF CONTENTS:
 *  1. Generics Foundations (Why Generics? Compile-time Type Safety vs. Runtime ClassCastException)
 *  2. Invariance vs. Covariance vs. Contravariance (Why List<Integer> is NOT a List<Number>)
 *  3. The PECS Principle (Producer Extends, Consumer Super & Wildcard Mastery)
 *  4. Wildcard Capture & Helper Methods (Solving the capture#1-of ? compiler error)
 *  5. Bounded Type Parameters & Multiple Bounds (<T extends Number & Comparable<T>>)
 *  6. Recursive Type Bounds & Self-Referential Generics (<T extends Comparable<T>>)
 *  7. Type Erasure & The Synthetic Bridge Method Mystery (Bytecode Demystified)
 *  8. The 7 Strict Restrictions of Java Generics (And Production Workarounds)
 *  9. Heap Pollution & The @SafeVarargs Contract
 * 10. Real-World Architectural Patterns:
 *      - Pattern A: Type-Safe Heterogeneous Container (ClassToInstanceMap)
 *      - Pattern B: Fluent Builder with Recursive Self-Types (Inheritance in Builders)
 *      - Pattern C: Generic Repository / DAO Pattern (Type-safe CRUD)
 * =============================================================================================
 */
public class GenericsMastery {

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("     JAVA GENERICS, TYPE ERASURE & ADVANCED TYPE SAFETY MASTERCLASS ");
        System.out.println("===================================================================\n");

        section1_GenericsFoundations();
        section2_InvarianceAndSubtyping();
        section3_PecsPrinciple();
        section4_WildcardCapture();
        section5_BoundedTypeParameters();
        section6_RecursiveTypeBounds();
        section7_TypeErasureAndBridgeMethods();
        section8_TheSevenRestrictions();
        section9_HeapPollutionAndSafeVarargs();
        section10_RealWorldGenericPatterns();

        System.out.println("\n===================================================================");
        System.out.println("              ALL GENERICS LESSONS COMPLETE!                       ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: GENERICS FOUNDATIONS & TYPE SAFETY
    // =========================================================================================
    private static void section1_GenericsFoundations() {
        printHeader("1. GENERICS FOUNDATIONS (Type Safety vs. ClassCastException)");
        /*
         * THE PRE-JAVA 5 WORLD (RAW TYPES):
         *  - Before Java 5, collections stored raw `Object` references.
         *  - Developers had to manually cast every element extracted from a collection.
         *  - If an unexpected type was inserted, the program compiled fine, but crashed in
         *    production at runtime with `ClassCastException`!
         *
         * THE POST-JAVA 5 GENERIC WORLD:
         *  - Generics enforce COMPILE-TIME type checking.
         *  - Eliminates manual casting: The compiler inserts casts automatically and safely.
         *  - Code becomes expressive, self-documenting, and provably type-safe.
         *
         * TYPE PARAMETER CONVENTIONS:
         *  - E : Element (used extensively in Collections)
         *  - K : Key (used in Maps)
         *  - V : Value (used in Maps)
         *  - T : Type (general-purpose type parameter)
         *  - N : Number
         *  - S, U, V : 2nd, 3rd, 4th type parameters
         */

        // 1. Raw types (Dangerous pre-Java 5 style):
        @SuppressWarnings({"rawtypes", "unchecked"})
        List rawList = new ArrayList();
        rawList.add("Hello");
        rawList.add(100); // No compile error!

        try {
            for (Object obj : rawList) {
                String str = (String) obj; // Fails on the second element!
                System.out.println(str);
            }
        } catch (ClassCastException e) {
            System.out.println("Pre-Java 5 raw types trap caught: " + e.getMessage());
        }

        // 2. Generic types (Modern compile-time type safety):
        List<String> safeList = new ArrayList<>();
        safeList.add("Modern");
        safeList.add("Java");
        // safeList.add(100); // COMPILE ERROR! Caught instantly by the IDE/compiler.
        System.out.println("Generic type-safe list: " + safeList);
    }

    // =========================================================================================
    // SECTION 2: INVARIANCE VS. COVARIANCE (WHY List<Integer> != List<Number>)
    // =========================================================================================
    private static void section2_InvarianceAndSubtyping() {
        printHeader("2. INVARIANCE VS. COVARIANCE (Why Generics are Invariant)");
        /*
         * ARRAYS IN JAVA ARE COVARIANT (HISTORICAL FLAW):
         *  - Integer[] IS A SUBTYPE of Number[]!
         *  - Because arrays are reified (they know their element type at runtime), the JVM
         *    allows assigning `Number[] arr = new Integer[2]`.
         *  - However, storing a Double into `arr[0]` compiles, but blows up at runtime with
         *    `ArrayStoreException`!
         *
         * GENERICS IN JAVA ARE INVARIANT:
         *  - `List<Integer>` IS NOT A SUBTYPE of `List<Number>`!
         *  - Even though `Integer` is a subtype of `Number`, `List<Integer>` and `List<Number>`
         *    have NO inheritance relationship whatsoever.
         *
         * WHY IS INVARIANCE NECESSARY?
         *  - If `List<Integer>` were a subtype of `List<Number>`, you could do:
         *      List<Integer> ints = new ArrayList<>();
         *      List<Number> nums = ints; // Pretend this compiled...
         *      nums.add(3.14);           // Adding Double to List of Integers!
         *      Integer i = ints.get(0);  // CRASH! Corrupted type safety!
         *  - Java's compiler strictly rejects this to guarantee 100% type safety!
         */

        // Demonstrating Array Covariance Disaster:
        Integer[] intArray = {1, 2, 3};
        Number[] numArray = intArray; // Allowed because arrays are covariant!
        try {
            numArray[0] = 3.14; // Throws ArrayStoreException at runtime!
        } catch (ArrayStoreException e) {
            System.out.println("Array covariance runtime exception caught: " + e.getClass().getSimpleName());
        }

        // Generics Invariance:
        List<Integer> intList = List.of(1, 2, 3);
        // List<Number> numList = intList; // COMPILE ERROR! Type mismatch: List<Integer> cannot convert to List<Number>
        System.out.println("Generics invariance prevents runtime corruption at compile-time!");
    }

    // =========================================================================================
    // SECTION 3: THE PECS PRINCIPLE (PRODUCER EXTENDS, CONSUMER SUPER)
    // =========================================================================================
    private static void section3_PecsPrinciple() {
        printHeader("3. THE PECS PRINCIPLE (Producer Extends, Consumer Super)");
        /*
         * How do we achieve flexible APIs while maintaining type safety?
         * WILDCARDS (?) WITH UPPER & LOWER BOUNDS!
         *
         * THE PECS PRINCIPLE (Joshua Bloch - Effective Java):
         * -------------------------------------------------------------------------------------
         *  P E C S  =  "Producer Extends, Consumer Super"
         * -------------------------------------------------------------------------------------
         *
         *  1. PRODUCER EXTENDS (`? extends T`): Covariance (Read-Only)
         *     - Use when you only READ elements out of the collection (it PRODUCES data for you).
         *     - You can safely read elements as type `T`.
         *     - You CANNOT add any elements (except `null`) because the compiler cannot know
         *       which specific subtype of `T` the collection actually holds!
         *
         *  2. CONSUMER SUPER (`? super T`): Contravariance (Write-Only)
         *     - Use when you only WRITE elements into the collection (it CONSUMES your data).
         *     - You can safely add instances of `T` (or any subtype of `T`).
         *     - Reading from it only yields `Object` (because the collection might hold any supertype of T).
         *
         *  3. UNBOUNDED WILDCARD (`?`):
         *     - Equivalent to `? extends Object`.
         *     - Use when you only care about methods that don't depend on the type parameter
         *       (e.g., `list.size()`, `list.clear()`).
         */

        // PRODUCER EXTENDS (Reading/Summing numbers):
        List<Integer> integers = List.of(10, 20, 30);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);

        System.out.println("Summing Integers via ? extends Number: " + sumOfList(integers)); // 60.0
        System.out.println("Summing Doubles via ? extends Number:  " + sumOfList(doubles));  // 7.5

        // CONSUMER SUPER (Adding/Populating numbers):
        List<Number> numberDestination = new ArrayList<>();
        List<Object> objectDestination = new ArrayList<>();

        addIntegers(numberDestination, 1, 2, 3);
        addIntegers(objectDestination, 4, 5, 6);

        System.out.println("Consumer destination (List<Number>): " + numberDestination);
        System.out.println("Consumer destination (List<Object>): " + objectDestination);

        // Standard library example of PECS in action:
        // Collections.copy(List<? super T> dest, List<? extends T> src)
        // src is Producer (? extends T); dest is Consumer (? super T)!
    }

    // Producer Extends: list produces Numbers for us to read
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number n : list) {
            sum += n.doubleValue(); // Safe read as Number!
        }
        // list.add(10); // COMPILE ERROR! Cannot add to ? extends Number!
        return sum;
    }

    // Consumer Super: list consumes Integers from us to write
    public static void addIntegers(List<? super Integer> list, int... items) {
        for (int item : items) {
            list.add(item); // Safe write of Integer!
        }
        // Integer n = list.get(0); // COMPILE ERROR! Can only read as Object!
    }

    // =========================================================================================
    // SECTION 4: WILDCARD CAPTURE & HELPER METHODS
    // =========================================================================================
    private static void section4_WildcardCapture() {
        printHeader("4. WILDCARD CAPTURE & HELPER METHODS");
        /*
         * WHAT IS WILDCARD CAPTURE?
         * When the compiler processes `List<?>`, it assigns an internal placeholder capture type
         * such as `capture#1-of ?`.
         *
         * The problem:
         * If you try to swap two elements in a `List<?>`:
         *   list.set(i, list.get(j)); // COMPILE ERROR!
         * The compiler says: Cannot pass type `capture#2-of ?` to `set(int, capture#1-of ?)`,
         * even though they are from the exact same list!
         *
         * THE SOLUTION: A Private Generic Helper Method!
         * A helper method with an explicit type parameter `<T>` captures the wildcard cleanly.
         */

        List<String> names = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
        System.out.println("Before swap: " + names);
        swapFirstAndLast(names);
        System.out.println("After swap:  " + names);
    }

    // Public API takes flexible wildcard List<?>
    public static void swapFirstAndLast(List<?> list) {
        // Delegates to private helper with type parameter <T> to capture the wildcard:
        swapFirstAndLastHelper(list);
    }

    // Private helper captures the unknown type as <T>
    private static <T> void swapFirstAndLastHelper(List<T> list) {
        if (list.size() >= 2) {
            T first = list.get(0);
            T last = list.get(list.size() - 1);
            list.set(0, last);
            list.set(list.size() - 1, first);
        }
    }

    // =========================================================================================
    // SECTION 5: BOUNDED TYPE PARAMETERS & MULTIPLE BOUNDS
    // =========================================================================================
    private static void section5_BoundedTypeParameters() {
        printHeader("5. BOUNDED TYPE PARAMETERS & MULTIPLE BOUNDS");
        /*
         * BOUNDED TYPE PARAMETERS:
         * Restricts the types that can be passed to a generic class or method.
         *
         * Syntax:
         *   <T extends SuperClass & Interface1 & Interface2>
         *
         * Rules:
         *  1. At most ONE class can be specified in the bounds.
         *  2. If a class is present, it MUST BE FIRST in the declaration!
         *  3. Followed by zero or more interfaces separated by `&`.
         */

        class NumberBox<T extends Number & Comparable<T>> {
            private final T value;

            NumberBox(T value) { this.value = value; }

            boolean isGreaterThan(T other) {
                return value.compareTo(other) > 0;
            }

            double doubleValue() {
                return value.doubleValue(); // Available from Number!
            }
        }

        NumberBox<Integer> box = new NumberBox<>(100);
        System.out.println("isGreaterThan 50: " + box.isGreaterThan(50));
        System.out.println("doubleValue:      " + box.doubleValue());
    }

    // =========================================================================================
    // SECTION 6: RECURSIVE TYPE BOUNDS (SELF-TYPES)
    // =========================================================================================
    private static void section6_RecursiveTypeBounds() {
        printHeader("6. RECURSIVE TYPE BOUNDS & SELF-REFERENTIAL GENERICS");
        /*
         * WHAT IS A RECURSIVE TYPE BOUND?
         * A type parameter bound that includes the type parameter itself:
         *   <T extends Comparable<T>>
         *
         * Meaning:
         * "T can be any type that is comparable to instances of ITS OWN TYPE."
         * This prevents comparing an Apple to an Orange at compile-time!
         */

        List<String> words = List.of("banana", "apple", "cherry");
        List<Integer> scores = List.of(45, 99, 12, 78);

        System.out.println("Max word:  " + findMaximum(words));  // cherry
        System.out.println("Max score: " + findMaximum(scores)); // 99
    }

    // Generic method with recursive type bound:
    public static <T extends Comparable<T>> T findMaximum(List<T> list) {
        if (list.isEmpty()) throw new NoSuchElementException("Empty list");
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    // =========================================================================================
    // SECTION 7: TYPE ERASURE & SYNTHETIC BRIDGE METHODS
    // =========================================================================================
    private static void section7_TypeErasureAndBridgeMethods() {
        printHeader("7. TYPE ERASURE & THE SYNTHETIC BRIDGE METHOD MYSTERY");
        /*
         * WHAT IS TYPE ERASURE?
         * Generics were added in Java 5 with 100% backward compatibility with Java 1.4 bytecode.
         * To achieve this, the Java compiler ERASES all generic type information during compilation:
         *
         * 1. Replaces all type parameters with their first bound (or `Object` if unbounded).
         *    - `List<T>`               becomes `List` (raw type)
         *    - `<T extends Number>`    becomes `Number`
         * 2. Inserts explicit bytecode type casts where necessary.
         * 3. Generates synthetic BRIDGE METHODS to preserve polymorphism in subclasses!
         *
         * PROOF OF TYPE ERASURE:
         * At runtime, `ArrayList<String>` and `ArrayList<Integer>` have the EXACT SAME Class object!
         */

        List<String> stringList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        System.out.println("stringList.getClass() == integerList.getClass(): "
                + (stringList.getClass() == integerList.getClass())); // TRUE! Both are java.util.ArrayList.class

        /*
         * THE BRIDGE METHOD MYSTERY EXPLAINED:
         * Suppose class SubNode implements Comparable<SubNode>.
         * SubNode defines: public int compareTo(SubNode other)
         *
         * But after type erasure, interface Comparable's method is:
         *   public int compareTo(Object other)
         *
         * SubNode's method DOES NOT override Comparable.compareTo(Object) because the parameter
         * types do not match! Polymorphism would break!
         *
         * SOLUTION: The compiler automatically generates a hidden SYNTHETIC BRIDGE METHOD in SubNode:
         *   public synthetic bridge int compareTo(Object other) {
         *       return this.compareTo((SubNode) other); // casts and delegates!
         *   }
         */

        class StringNode implements Comparable<StringNode> {
            private final String data;
            StringNode(String data) { this.data = data; }

            @Override
            public int compareTo(StringNode other) {
                return this.data.compareTo(other.data);
            }
        }

        System.out.println("\nInspecting StringNode methods via Reflection:");
        for (Method m : StringNode.class.getDeclaredMethods()) {
            if (m.getName().equals("compareTo")) {
                System.out.printf("   Method: %s(%s) | isBridge: %b | isSynthetic: %b%n",
                        m.getName(), Arrays.toString(m.getParameterTypes()), m.isBridge(), m.isSynthetic());
            }
        }
    }

    // =========================================================================================
    // SECTION 8: THE 7 STRICT RESTRICTIONS OF JAVA GENERICS
    // =========================================================================================
    private static void section8_TheSevenRestrictions() {
        printHeader("8. THE 7 STRICT RESTRICTIONS OF JAVA GENERICS");
        /*
         * RESTRICTION 1: Cannot instantiate type parameter with primitive types.
         *   List<int> is illegal -> Must use List<Integer>.
         *
         * RESTRICTION 2: Cannot create instances of type parameters.
         *   T obj = new T(); is illegal because T is erased to Object at runtime!
         *   Workaround: Pass a Supplier<T> or Class<T> token.
         *
         * RESTRICTION 3: Cannot declare static fields whose types are type parameters.
         *   private static T shared; is illegal because static fields are shared across all instances!
         *
         * RESTRICTION 4: Cannot use casts or instanceof with parameterized types.
         *   if (obj instanceof List<String>) is illegal (erased to List<?>)!
         *   Workaround: if (obj instanceof List<?> list)
         *
         * RESTRICTION 5: Cannot create arrays of parameterized types.
         *   List<String>[] arr = new List<String>[10]; is illegal (Generic Array Creation error).
         *   Workaround: List<List<String>> or raw array cast: (List<String>[]) new List<?>[10];
         *
         * RESTRICTION 6: Cannot create, catch, or throw objects of parameterized Exception classes.
         *   class MyException<T> extends Exception is illegal!
         *
         * RESTRICTION 7: Cannot overload methods where formal parameters erase to the same raw type.
         *   void print(List<String> l) and void print(List<Integer> l) is illegal because both
         *   erase to void print(List l)!
         */

        // Workaround demonstration for Restriction 2 (Instantiating generic type):
        Supplier<StringBuilder> factory = StringBuilder::new;
        StringBuilder createdViaSupplier = createInstance(factory);
        createdViaSupplier.append("Instantiated generically via Supplier!");
        System.out.println("Workaround 2 (new T()): " + createdViaSupplier);

        // Workaround demonstration for Restriction 4 (instanceof check):
        Object mystery = List.of("A", "B", "C");
        if (mystery instanceof List<?> genericList) {
            System.out.println("Workaround 4 (instanceof List<?>): Size is " + genericList.size());
        }
    }

    public static <T> T createInstance(Supplier<T> factory) {
        return factory.get();
    }

    // =========================================================================================
    // SECTION 9: HEAP POLLUTION & @SafeVarargs
    // =========================================================================================
    private static void section9_HeapPollutionAndSafeVarargs() {
        printHeader("9. HEAP POLLUTION & THE @SafeVarargs CONTRACT");
        /*
         * WHAT IS HEAP POLLUTION?
         * Heap pollution occurs when a variable of a parameterized type refers to an object
         * that is NOT of that parameterized type.
         *
         * How generic varargs cause heap pollution:
         * When you declare a method with `T... elements`, the compiler creates an array: `new T[]`.
         * But generic arrays cannot be created safely, so it creates a raw `Object[]` array!
         *
         * THE @SafeVarargs ANNOTATION:
         * Added in Java 7 to suppress compiler warnings on methods with generic varargs.
         * IT IS A CONTRACT: You promise the compiler that:
         *   1. The method will NEVER store anything into the varargs array (no writes).
         *   2. The method will NEVER let a reference to the varargs array escape to outside code!
         */

        List<String> combined = safeCombine("Hello", "World", "Java");
        System.out.println("@SafeVarargs combined list: " + combined);
    }

    @SafeVarargs
    public static <T> List<T> safeCombine(T... elements) {
        // Safe: We only read from the array, we do not store anything into it or leak the array!
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return Collections.unmodifiableList(list);
    }

    // =========================================================================================
    // SECTION 10: REAL-WORLD ADVANCED GENERIC DESIGN PATTERNS
    // =========================================================================================
    private static void section10_RealWorldGenericPatterns() {
        printHeader("10. REAL-WORLD ADVANCED GENERIC DESIGN PATTERNS");

        // PATTERN A: Type-Safe Heterogeneous Container (Joshua Bloch)
        // A single map storing values of completely different types with 100% type safety!
        System.out.println("Pattern A: Type-Safe Heterogeneous Container");
        Favorites favorites = new Favorites();
        favorites.putFavorite(String.class, "Java Generics");
        favorites.putFavorite(Integer.class, 2026);
        favorites.putFavorite(Class.class, GenericsMastery.class);

        String favString = favorites.getFavorite(String.class);
        Integer favInt = favorites.getFavorite(Integer.class);
        System.out.println("   Retrieved String:  " + favString);
        System.out.println("   Retrieved Integer: " + favInt);

        // PATTERN B: Fluent Builder with Recursive Self-Types (Self-Referential Generics)
        // Problem: In an inheritance hierarchy, calling a superclass builder method returns
        // the superclass builder, losing access to subclass methods!
        // Solution: Recursive Generics `Builder<T extends Builder<T>>`!
        System.out.println("\nPattern B: Fluent Builder with Recursive Self-Types");
        Employee employee = new Employee.Builder()
                .withName("Asha")       // Defined in Person.Builder
                .withAge(30)            // Defined in Person.Builder
                .withDepartment("IT")   // Defined in Employee.Builder (Fluent chaining maintained!)
                .withSalary(95000.0)    // Defined in Employee.Builder
                .build();
        System.out.println("   Built Employee: " + employee);
    }

    // --- PATTERN A IMPLEMENTATION: Type-Safe Heterogeneous Container ---
    public static class Favorites {
        private final Map<Class<?>, Object> favorites = new HashMap<>();

        public <T> void putFavorite(Class<T> type, T instance) {
            Objects.requireNonNull(type);
            // Dynamic cast ensures type safety even if someone uses raw types:
            favorites.put(type, type.cast(instance));
        }

        public <T> T getFavorite(Class<T> type) {
            // Dynamic cast returns exact type without compiler warning:
            return type.cast(favorites.get(type));
        }
    }

    // --- PATTERN B IMPLEMENTATION: Recursive Generics Builder ---
    public static class Person {
        protected final String name;
        protected final int age;

        // Recursive self-type builder
        public abstract static class Builder<T extends Builder<T>> {
            protected String name;
            protected int age;

            // Self-referential return trick:
            protected abstract T self();

            public T withName(String name) {
                this.name = name;
                return self();
            }

            public T withAge(int age) {
                this.age = age;
                return self();
            }

            public abstract Person build();
        }

        protected Person(Builder<?> builder) {
            this.name = builder.name;
            this.age = builder.age;
        }
    }

    public static class Employee extends Person {
        private final String department;
        private final double salary;

        public static class Builder extends Person.Builder<Builder> {
            private String department;
            private double salary;

            @Override
            protected Builder self() {
                return this; // Returns exact subclass builder!
            }

            public Builder withDepartment(String dept) {
                this.department = dept;
                return self();
            }

            public Builder withSalary(double salary) {
                this.salary = salary;
                return self();
            }

            @Override
            public Employee build() {
                return new Employee(this);
            }
        }

        private Employee(Builder builder) {
            super(builder);
            this.department = builder.department;
            this.salary = builder.salary;
        }

        @Override
        public String toString() {
            return "Employee[name=" + name + ", age=" + age + ", dept=" + department + ", salary=" + salary + "]";
        }
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}


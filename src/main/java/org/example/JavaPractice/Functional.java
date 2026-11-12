package org.example.JavaPractice;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * =============================================================================================
 *                      MASTERCLASS: FUNCTIONAL PROGRAMMING IN JAVA
 * =============================================================================================
 * 
 * This file serves as a comprehensive, hands-on, runnable documentation and guide for mastering
 * Functional Programming (FP) in Java (from fundamentals to advanced production patterns).
 * 
 * TABLE OF CONTENTS:
 *  1. Core Principles of Functional Programming
 *  2. Functional Interfaces & The @FunctionalInterface Annotation
 *  3. Anonymous Classes vs. Lambdas & Lexical Scoping (Variable Capture)
 *  4. The 4 Types of Method References (::)
 *  5. Built-in Core Functional Interfaces (java.util.function)
 *  6. Two-Argument Variants (BiPredicate, BiFunction, BiConsumer)
 *  7. Primitive Specializations (Avoiding Boxing / Unboxing Overhead)
 *  8. Legacy Functional Interfaces Modernized (Runnable, Callable, Comparator)
 *  9. Custom Functional Interfaces & Handling Checked Exceptions
 * 10. Higher-Order Functions, Currying & Partial Application
 * 11. Lazy Evaluation & Function Memoization (Caching)
 * 12. Monadic Programming with Optional
 * 13. Functional Design Patterns & Railway-Oriented Error Handling (Result / Either)
 * 14. Tail Recursion & The Trampoline Pattern (Solving StackOverflow in Java)
 * 15. Algebraic Data Types (ADTs) & Java 21 Functional Pattern Matching
 * 16. Asynchronous Functional Composition with CompletableFuture
 * 17. Real-World FP Practice Problems & Interview Pearls
 * 18. Under the Hood (JVM invokedynamic) & FP Best Practices
 * =============================================================================================
 */
public class Functional {

    // -----------------------------------------------------------------------------------------
    // DOMAIN MODELS USED ACROSS EXAMPLES
    // -----------------------------------------------------------------------------------------
    public record Person(String name, int age) {}
    public record Employee(String name, String department, double salary) {}
    public record Product(String id, String name, String category, double price, boolean inStock) {}

    // -----------------------------------------------------------------------------------------
    // 1. CUSTOM FUNCTIONAL INTERFACE DEFINITION & ANATOMY
    // -----------------------------------------------------------------------------------------
    /**
     * An interface with EXACTLY ONE abstract method (SAM - Single Abstract Method).
     * The @FunctionalInterface annotation is optional but recommended:
     *   - It tells the compiler to verify SAM compliance.
     *   - If someone adds another abstract method, compilation fails immediately.
     */
    @FunctionalInterface
    public interface Greeter {
        // 1. The Single Abstract Method (SAM)
        String greet(String name);

        // 2. Default methods are ALLOWED (they provide implementation, not abstract)
        default Greeter twice() {
            return n -> greet(greet(n));
        }

        // 3. Static helper methods are ALLOWED
        static Greeter polite() {
            return n -> "Dear " + n;
        }

        // 4. Overriding public methods of java.lang.Object does NOT count towards SAM!
        // Why? Every class in Java inherits from Object, so any implementation is guaranteed
        // to already have an implementation for equals, hashCode, and toString.
        @Override
        boolean equals(Object o);
    }

    // Custom 3-argument functional interface (standard library only goes up to 2)
    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);

        default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c) -> after.apply(this.apply(a, b, c));
        }
    }

    // Custom throwing interface to demonstrate handling checked exceptions in FP
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }

    // -----------------------------------------------------------------------------------------
    // MAIN RUNNER: EXECUTES ALL SECTIONS SEQUENTIALLY
    // -----------------------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("        LEARNING FUNCTIONAL PROGRAMMING IN JAVA: ZERO TO HERO      ");
        System.out.println("===================================================================\n");

        section1_CorePrinciples();
        section2_FunctionalInterfaceAndLambdas();
        section3_VariableCaptureAndScoping();
        section4_MethodReferences();
        section5_BuiltInFunctionalInterfaces();
        section6_TwoArgumentVariants();
        section7_PrimitiveSpecializations();
        section8_LegacyInterfacesModernized();
        section9_CustomInterfacesAndExceptions();
        section10_HigherOrderFunctionsAndCurrying();
        section11_LazyEvaluationAndMemoization();
        section12_MonadicOptionalProgramming();
        section13_FunctionalPatternsAndResultEither();
        section14_TailRecursionAndTrampoline();
        section15_AlgebraicDataTypesAndPatternMatching();
        section16_AsyncFunctionalCompletableFuture();
        section17_RealWorldFPChallenges();
        section18_UnderTheHoodAndBestPractices();

        System.out.println("\n===================================================================");
        System.out.println("              ALL FUNCTIONAL PROGRAMMING LESSONS COMPLETE!         ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: CORE PRINCIPLES OF FUNCTIONAL PROGRAMMING (FP)
    // =========================================================================================
    private static void section1_CorePrinciples() {
        printHeader("1. CORE PRINCIPLES OF FUNCTIONAL PROGRAMMING");
        /*
         * Key FP Concepts:
         * 1. Declarative vs. Imperative:
         *    - Imperative: HOW to do it (step-by-step state mutations, loops, temp variables).
         *    - Declarative: WHAT to do (transformations, pipelines, mathematical mapping).
         * 2. Pure Functions:
         *    - Deterministic: Same input ALWAYS produces same output.
         *    - No side effects: Does not modify external state, file system, database, or args.
         * 3. Immutability:
         *    - Data structures cannot be changed once created. Transformations return NEW objects.
         * 4. First-Class Functions:
         *    - Functions can be assigned to variables, passed as arguments, and returned from functions.
         */

        // Imperative approach (mutates state, describes loop mechanics):
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        int imperativeSumOfSquares = 0;
        for (int n : numbers) {
            if (n % 2 == 0) {
                imperativeSumOfSquares += (n * n); // mutation
            }
        }
        System.out.println("Imperative sum of even squares: " + imperativeSumOfSquares);

        // Declarative / Functional approach (pure, immutable pipeline, zero mutation):
        int functionalSumOfSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * n)
                .sum();
        System.out.println("Functional sum of even squares: " + functionalSumOfSquares);

        // Pure vs Impure demonstration:
        class StateHolder { int counter = 0; }
        StateHolder state = new StateHolder();

        // IMPURE: Mutates external variable 'counter' (side effect!)
        Function<Integer, Integer> impureAdd = x -> {
            state.counter += x;
            return state.counter;
        };

        // PURE: Depends only on inputs, touches no outside state
        BinaryOperator<Integer> pureAdd = (a, b) -> a + b;

        System.out.println("Impure call 1: " + impureAdd.apply(5)); // 5
        System.out.println("Impure call 2: " + impureAdd.apply(5)); // 10 (Different output for same input! Breaks referential transparency)
        System.out.println("Pure call 1:   " + pureAdd.apply(5, 5));   // 10
        System.out.println("Pure call 2:   " + pureAdd.apply(5, 5));   // 10 (Always identical)
    }

    // =========================================================================================
    // SECTION 2: FUNCTIONAL INTERFACES & LAMBDA SYNTAX
    // =========================================================================================
    private static void section2_FunctionalInterfaceAndLambdas() {
        printHeader("2. FUNCTIONAL INTERFACES & LAMBDA EXPRESSION SYNTAX");

        // Way 1: Anonymous Inner Class (Pre-Java 8 approach)
        Greeter anon = new Greeter() {
            @Override
            public String greet(String name) {
                return "Hello from Anonymous Class, " + name;
            }
        };

        // Way 2: Lambda Expression (Standard syntax)
        // (parameter list) -> { body }
        Greeter lambda1 = (String name) -> {
            return "Hello from explicit lambda, " + name;
        };

        // Way 3: Concise Lambda (Type inferred, parentheses omitted for single param, expression body)
        Greeter lambdaConcise = name -> "Hello concise, " + name;

        // Calling default and static methods on functional interfaces:
        Greeter politeGreeter = Greeter.polite();             // static method
        Greeter politeTwice = politeGreeter.twice();          // default method chaining

        System.out.println(anon.greet("Alice"));
        System.out.println(lambda1.greet("Bob"));
        System.out.println(lambdaConcise.greet("Charlie"));
        System.out.println("Chained default method: " + politeTwice.greet("David")); // Dear Dear David

        /*
         * Lambda Syntax Variations:
         * 1. Zero params:          () -> System.out.println("Hi")
         * 2. Single param:         x -> x * 2          (parens optional)
         * 3. Multiple params:      (x, y) -> x + y     (parens required)
         * 4. Explicit types:       (int x, int y) -> x + y
         * 5. Java 11 var syntax:   (@Deprecated var x, var y) -> x + y (allows parameter annotations)
         * 6. Multi-line body:      x -> { int res = x * 2; return res; }
         */
        BinaryOperator<Integer> varLambda = (var a, var b) -> a * b;
        System.out.println("Java 11 var lambda multiply: " + varLambda.apply(6, 7));
    }

    // =========================================================================================
    // SECTION 3: VARIABLE CAPTURE & LEXICAL SCOPING
    // =========================================================================================
    private static void section3_VariableCaptureAndScoping() {
        printHeader("3. VARIABLE CAPTURE & LEXICAL SCOPING");

        /*
         * The "Effectively Final" Rule:
         * Any local variable accessed inside a lambda must either be declared 'final'
         * or be 'effectively final' (its value is assigned once and NEVER changed afterwards).
         * 
         * Why?
         * Java lambdas capture variables by VALUE (a copy is made), not by reference.
         * If the variable could change, the lambda and the enclosing method would have
         * diverging values, creating dangerous race conditions when executed asynchronously.
         */
        int multiplier = 10; // effectively final (not modified)
        Function<Integer, Integer> timesTen = n -> n * multiplier;
        System.out.println("Captured multiplier result: " + timesTen.apply(5));

        // Uncommenting the next line causes a COMPILE ERROR:
        // multiplier = 20; // Error: Local variable multiplier defined in an enclosing scope must be final or effectively final

        /*
         * Capturing vs Non-Capturing Lambdas (JVM Performance implication):
         * - Non-capturing (stateless): 'x -> x * 2'
         *   The JVM creates a SINGLETON instance at runtime. Zero allocation per call!
         * - Capturing (stateful): 'x -> x + multiplier'
         *   The JVM must instantiate an object to hold the captured state on each invocation.
         */

        /*
         * 'this' Reference: Anonymous Class vs Lambda
         * - In an Anonymous Class, 'this' refers to the anonymous class instance itself.
         * - In a Lambda, 'this' is LEXICALLY SCOPED: it refers to the enclosing class instance!
         */
        new Object() {
            void demonstrateScope() {
                Runnable rAnon = new Runnable() {
                    @Override
                    public void run() {
                        // 'this' refers to the new Runnable instance
                        System.out.println("Anonymous class this: " + this.getClass().getSimpleName());
                    }
                };

                Runnable rLambda = () -> {
                    // 'this' refers to the enclosing Object instance, NOT a lambda object!
                    System.out.println("Lambda 'this' is lexical enclosing class: " + this.getClass().getName());
                };

                rAnon.run();
                rLambda.run();
            }
        }.demonstrateScope();
    }

    // =========================================================================================
    // SECTION 4: THE 4 TYPES OF METHOD REFERENCES (::)
    // =========================================================================================
    private static void section4_MethodReferences() {
        printHeader("4. THE 4 TYPES OF METHOD REFERENCES (::)");
        /*
         * Method references are syntactic sugar for lambdas that do nothing other than
         * directly delegate to an existing method.
         *
         * Type 1: Reference to a STATIC method            (ClassName::staticMethod)
         * Type 2: Reference to an INSTANCE method of an
         *         EXISTING OBJECT (Bound reference)        (instanceRef::instanceMethod)
         * Type 3: Reference to an INSTANCE method of an
         *         ARBITRARY OBJECT of a type (Unbound)    (ClassName::instanceMethod)
         * Type 4: Reference to a CONSTRUCTOR              (ClassName::new)
         */

        // TYPE 1: Static Method Reference
        // Lambda: (str) -> Integer.parseInt(str)
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("1. Static method ref (Integer::parseInt): " + parser.apply("1234"));

        // TYPE 2: Bound Instance Method Reference (bound to a specific instance)
        // Lambda: (msg) -> System.out.println(msg)
        String prefix = "LOG: ";
        // Lambda: (str) -> prefix.concat(str)
        Function<String, String> logger = prefix::concat;
        System.out.println("2. Bound instance method ref (prefix::concat): " + logger.apply("System ready"));

        // TYPE 3: Unbound Instance Method Reference (arbitrary instance of a type)
        // Notice: The 1st argument to the function becomes the receiver ('this') of the call!
        // Lambda: (String s) -> s.toUpperCase()
        Function<String, String> upper = String::toUpperCase;
        System.out.println("3. Unbound method ref (String::toUpperCase): " + upper.apply("hello world"));

        // Another Unbound example with 2 arguments:
        // Lambda: (String a, String b) -> a.compareToIgnoreCase(b)
        Comparator<String> strComp = String::compareToIgnoreCase;
        System.out.println("   Unbound Bi-comparison: " + strComp.compare("apple", "APPLE")); // 0

        // TYPE 4: Constructor Reference
        // Lambda: () -> new ArrayList<String>()
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> dynamicList = listFactory.get();
        dynamicList.add("Item Added");
        System.out.println("4. Constructor ref (ArrayList::new): " + dynamicList);

        // Constructor with parameters:
        // Lambda: (name, age) -> new Person(name, age)
        BiFunction<String, Integer, Person> personCreator = Person::new;
        Person person = personCreator.apply("Sarah", 28);
        System.out.println("   Constructor ref with args (Person::new): " + person);

        // Array Constructor Reference:
        // Lambda: (size) -> new int[size]
        IntFunction<int[]> arrayMaker = int[]::new;
        int[] numbersArray = arrayMaker.apply(5);
        System.out.println("   Array constructor ref (int[]::new), length: " + numbersArray.length);
    }

    // =========================================================================================
    // SECTION 5: BUILT-IN CORE FUNCTIONAL INTERFACES (java.util.function)
    // =========================================================================================
    private static void section5_BuiltInFunctionalInterfaces() {
        printHeader("5. BUILT-IN CORE FUNCTIONAL INTERFACES (THE BIG 5)");
        /*
         *  Interface          | SAM Signature      | Role
         *  -------------------|--------------------|------------------------------------------
         *  Predicate<T>       | boolean test(T t)  | Filters, checks conditions (boolean test)
         *  Consumer<T>        | void accept(T t)   | Consumes value, performs side effects
         *  Supplier<T>        | T get()            | Produces / supplies a value lazily
         *  Function<T, R>     | R apply(T t)       | Transforms T into R
         *  UnaryOperator<T>   | T apply(T t)       | Special Function where input = output type
         *  BinaryOperator<T>  | T apply(T t1, T t2)| Special BiFunction where both inputs = output
         */

        // 1. PREDICATE<T> - Condition Testing & Composition
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isPositive = n -> n > 0;

        // Predicate composition: .and(), .or(), .negate(), Predicate.not()
        Predicate<Integer> isPositiveAndEven = isEven.and(isPositive);
        Predicate<Integer> isOdd = isEven.negate();
        Predicate<String> isNotEmpty = Predicate.not(String::isBlank); // Java 11 utility

        System.out.println("isPositiveAndEven.test(4):  " + isPositiveAndEven.test(4));  // true
        System.out.println("isPositiveAndEven.test(-4): " + isPositiveAndEven.test(-4)); // false
        System.out.println("isOdd.test(3):              " + isOdd.test(3));              // true
        System.out.println("Predicate.isEqual check:     " + Predicate.isEqual("Java").test("Java"));

        // 2. FUNCTION<T, R> - Transformations & Composition
        Function<String, Integer> stringLength = String::length;
        Function<Integer, Integer> square = x -> x * x;

        // andThen: f.andThen(g) => g(f(x))  [First stringLength, then square]
        Function<String, Integer> lengthSquared = stringLength.andThen(square);
        System.out.println("stringLength.andThen(square) of 'hello': " + lengthSquared.apply("hello")); // (5)^2 = 25

        // compose: f.compose(g) => f(g(x))  [First square, then stringLength]
        Function<Integer, Integer> addThree = x -> x + 3;
        Function<Integer, Integer> squareThenAdd = addThree.compose(square); // addThree(square(x))
        System.out.println("addThree.compose(square) of 4: " + squareThenAdd.apply(4)); // (4^2) + 3 = 19

        // Function.identity() -> returns whatever argument was passed (useful in Streams Collectors)
        Function<String, String> id = Function.identity();
        System.out.println("Function.identity(): " + id.apply("Unchanged"));

        // 3. CONSUMER<T> - Side Effects & Chaining
        Consumer<String> consolePrinter = msg -> System.out.print("[Output: " + msg + "]");
        Consumer<String> auditLogger = msg -> System.out.println(" -> [Audit Logged: " + msg.length() + " chars]");

        // Consumer chaining with andThen
        Consumer<String> printAndLog = consolePrinter.andThen(auditLogger);
        printAndLog.accept("Order #1092 processed");

        // 4. SUPPLIER<T> - Deferred / Lazy Execution & Factories
        Supplier<LocalDate> dateSupplier = LocalDate::now;
        Supplier<Double> randomSupplier = Math::random;
        System.out.println("Supplier LocalDate.now: " + dateSupplier.get());
        System.out.println("Supplier random double: " + randomSupplier.get());

        // Supplier used for lazy default in Optional (avoids expensive calculation unless needed!)
        String value = Optional.<String>empty().orElseGet(() -> {
            System.out.println("   [Supplier executed lazily because Optional was empty!]");
            return "Fallback Value";
        });
        System.out.println("orElseGet result: " + value);

        // 5. UNARYOPERATOR<T> & BINARYOPERATOR<T>
        UnaryOperator<String> cleanString = s -> s.trim().toLowerCase();
        System.out.println("UnaryOperator result: " + cleanString.apply("  HeLLo JAVA  "));

        BinaryOperator<Integer> sumOp = Integer::sum;
        System.out.println("BinaryOperator sum: " + sumOp.apply(15, 25));

        // BinaryOperator minBy / maxBy helper methods with Comparators:
        BinaryOperator<Person> olderPerson = BinaryOperator.maxBy(Comparator.comparingInt(Person::age));
        Person p1 = new Person("Aman", 24);
        Person p2 = new Person("Priya", 31);
        System.out.println("Older person (BinaryOperator.maxBy): " + olderPerson.apply(p1, p2));
    }

    // =========================================================================================
    // SECTION 6: TWO-ARGUMENT VARIANTS (Bi-)
    // =========================================================================================
    private static void section6_TwoArgumentVariants() {
        printHeader("6. TWO-ARGUMENT VARIANTS (BiPredicate, BiFunction, BiConsumer)");

        // 1. BiPredicate<T, U>: (T, U) -> boolean
        BiPredicate<String, Integer> isLongerThan = (str, len) -> str.length() > len;
        System.out.println("BiPredicate ('antigravity', 5): " + isLongerThan.test("antigravity", 5)); // true

        // 2. BiConsumer<T, U>: (T, U) -> void
        // Extensively used with Map.forEach
        BiConsumer<String, Integer> displayEntry = (key, val) -> System.out.println("   " + key + " => " + val);
        Map<String, Integer> inventory = Map.of("Apples", 50, "Bananas", 20, "Cherries", 75);
        System.out.println("BiConsumer iterating over Map:");
        inventory.forEach(displayEntry);

        // 3. BiFunction<T, U, R>: (T, U) -> R
        BiFunction<Double, Double, Double> calculateTotal = (price, taxRate) -> price + (price * taxRate);
        // Chaining BiFunction with andThen(Function)
        BiFunction<Double, Double, String> formattedTotal = calculateTotal
                .andThen(total -> String.format("$%.2f", total));

        System.out.println("BiFunction chained formatted total: " + formattedTotal.apply(100.0, 0.18)); // $118.00
    }

    // =========================================================================================
    // SECTION 7: PRIMITIVE SPECIALIZATIONS (ZERO-COST PERFORMANCE)
    // =========================================================================================
    private static void section7_PrimitiveSpecializations() {
        printHeader("7. PRIMITIVE SPECIALIZATIONS (AVOIDING BOXING OVERHEAD)");
        /*
         * Why Primitive Specializations?
         * Generics in Java only work on reference types (Objects).
         * Using Function<Integer, Integer> forces every primitive 'int' to be boxed into 'Integer'.
         * In performance-sensitive code or high-throughput loops, this creates:
         *   - Millions of heap allocations.
         *   - Severe Garbage Collection (GC) pressure.
         *   - Cache misses due to pointer dereferencing.
         *
         * Java provides 43 primitive specializations for: int, long, double, and boolean.
         * Key Specializations:
         *   - Predicates:  IntPredicate, LongPredicate, DoublePredicate
         *   - Consumers:   IntConsumer, LongConsumer, DoubleConsumer, ObjIntConsumer<T>
         *   - Suppliers:   IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier
         *   - Functions:   IntFunction<R>, ToIntFunction<T>, IntToDoubleFunction, IntToLongFunction
         *   - Operators:   IntUnaryOperator, IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator
         */

        // IntPredicate (no boxing of int to Integer):
        IntPredicate isEvenPrimitive = n -> n % 2 == 0;
        System.out.println("IntPredicate.test(10): " + isEvenPrimitive.test(10));

        // ToIntFunction<T>: Takes object, extracts primitive int
        ToIntFunction<String> stringLength = String::length;
        System.out.println("ToIntFunction applied to 'Java': " + stringLength.applyAsInt("Java"));

        // IntBinaryOperator: Operates on 2 primitive ints and returns a primitive int
        IntBinaryOperator intMax = Math::max;
        System.out.println("IntBinaryOperator max(40, 99): " + intMax.applyAsInt(40, 99));

        // IntSupplier: Generates primitive int
        IntSupplier diceRoller = () -> ThreadLocalRandom.current().nextInt(1, 7);
        System.out.println("IntSupplier dice roll: " + diceRoller.getAsInt());

        // ObjIntConsumer<T>: Takes an object and a primitive int (great for indexed operations)
        ObjIntConsumer<String> printWithIndex = (item, idx) -> System.out.println("   [" + idx + "] " + item);
        printWithIndex.accept("First Task", 1);
        printWithIndex.accept("Second Task", 2);

        // Primitive Stream reduction with IntBinaryOperator:
        int sumPrimitive = IntStream.rangeClosed(1, 100).reduce(0, (a, b) -> a + b);
        System.out.println("IntStream sum of 1..100 without any boxing: " + sumPrimitive);
    }

    // =========================================================================================
    // SECTION 8: LEGACY FUNCTIONAL INTERFACES MODERNIZED
    // =========================================================================================
    private static void section8_LegacyInterfacesModernized() {
        printHeader("8. LEGACY FUNCTIONAL INTERFACES MODERNIZED (PRE-JAVA 8 REPURPOSED)");
        /*
         * Pre-Java 8 interfaces that had a single abstract method automatically became
         * functional interfaces in Java 8!
         *
         *  - java.lang.Runnable:            void run()
         *  - java.util.concurrent.Callable: V call() throws Exception
         *  - java.util.Comparator:          int compare(T o1, T o2)
         */

        // 1. RUNNABLE with Lambda:
        Runnable task = () -> System.out.println("   Runnable executed on thread: " + Thread.currentThread().getName());
        Thread thread = new Thread(task);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ignored) {}

        // 2. CALLABLE with Lambda:
        Callable<String> asyncCalculation = () -> {
            TimeUnit.MILLISECONDS.sleep(50);
            return "Calculation Result: 42";
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executor.submit(asyncCalculation);
            System.out.println("Callable Future output: " + future.get());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        // 3. COMPARATOR Fluent API (Modern Factory & Chaining methods):
        List<Person> people = new ArrayList<>(List.of(
                new Person("Zack", 25),
                new Person("Alice", 30),
                new Person("Alice", 22),
                new Person("Bob", 28)
        ));

        // Sorting using Comparator.comparing and thenComparing:
        // Sort by name ASCENDING, then by age DESCENDING
        Comparator<Person> personComparator = Comparator
                .comparing(Person::name)
                .thenComparing(Comparator.comparingInt(Person::age).reversed());

        people.sort(personComparator);
        System.out.println("Sorted People (by name, then age desc):");
        people.forEach(p -> System.out.println("   " + p));

        // Null-safe Comparator:
        List<String> nullableList = Arrays.asList("Banana", null, "Apple", "Mango", null);
        nullableList.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        System.out.println("Nulls-last sorted list: " + nullableList);
    }

    // =========================================================================================
    // SECTION 9: CUSTOM FUNCTIONAL INTERFACES & THE CHECKED EXCEPTION DILEMMA
    // =========================================================================================
    private static void section9_CustomInterfacesAndExceptions() {
        printHeader("9. CUSTOM FUNCTIONAL INTERFACES & HANDLING CHECKED EXCEPTIONS");

        // 1. Using our custom 3-argument functional interface:
        TriFunction<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;
        TriFunction<Integer, Integer, Integer, String> sumAndFormat = sum3.andThen(res -> "Sum is: " + res);
        System.out.println("Custom TriFunction: " + sumAndFormat.apply(10, 20, 30));

        /*
         * 2. The Checked Exception Dilemma in Java FP:
         * Standard Java functional interfaces (Function, Consumer, Supplier, etc.) do NOT declare
         * 'throws Exception'. Therefore, if code inside a lambda throws a checked exception
         * (e.g. IOException, InterruptedException, SQLException), the compiler forces you to wrap
         * it in an ugly try/catch block inside the lambda!
         *
         * Solution: Create a Higher-Order "Unchecked" Wrapper Utility!
         */

        List<String> classNames = List.of("java.lang.String", "java.util.List", "non.existent.Class");

        // Clean Functional way using mapMulti:
        List<Class<?>> loadedClasses = classNames.stream()
                .<Class<?>>mapMulti((className, consumer) -> {
                    try {
                        consumer.accept(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        System.out.println("   Gracefully skipped missing class: " + className);
                    }
                })
                .toList();
        System.out.println("Loaded classes via mapMulti: " + loadedClasses);

        // Demonstrating our generic uncheck() helper function:
        Function<String, Class<?>> safeLoader = uncheck(Class::forName);
        try {
            Class<?> stringClass = safeLoader.apply("java.lang.String");
            System.out.println("Successfully loaded via uncheck(): " + stringClass.getName());
        } catch (RuntimeException e) {
            System.out.println("Caught wrapped exception: " + e.getMessage());
        }
    }

    /**
     * Functional Exception Wrapper: Converts a ThrowingFunction into a standard Function.
     * Checked exceptions are safely wrapped into unchecked RuntimeExceptions.
     */
    public static <T, R> Function<T, R> uncheck(ThrowingFunction<T, R, ?> throwingFunction) {
        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException("Functional execution failed: " + e.getMessage(), e);
            }
        };
    }

    // =========================================================================================
    // SECTION 10: HIGHER-ORDER FUNCTIONS, CURRYING & PARTIAL APPLICATION
    // =========================================================================================
    private static void section10_HigherOrderFunctionsAndCurrying() {
        printHeader("10. HIGHER-ORDER FUNCTIONS, CURRYING & PARTIAL APPLICATION");
        /*
         * Higher-Order Function (HOF):
         * A function that either:
         *   1. Takes one or more functions as arguments, OR
         *   2. Returns another function as its result.
         */

        // Example A: A Higher-Order Function that returns a configured Validator function
        Function<Integer, Predicate<String>> minLengthValidator = minLen -> str -> str != null && str.length() >= minLen;
        Predicate<String> passwordValidator = minLengthValidator.apply(8);
        Predicate<String> pinValidator = minLengthValidator.apply(4);

        System.out.println("Password 'secret123' valid (>=8): " + passwordValidator.test("secret123")); // true
        System.out.println("Password 'pass' valid (>=8):      " + passwordValidator.test("pass"));      // false
        System.out.println("PIN '1234' valid (>=4):           " + pinValidator.test("1234"));           // true

        // Example B: Decorator / Function Wrapper (Timing execution of any function)
        Function<Integer, Long> fibonacci = n -> {
            long a = 0, b = 1;
            for (int i = 0; i < n; i++) {
                long temp = a + b;
                a = b;
                b = temp;
            }
            return a;
        };

        Function<Integer, Long> timedFibonacci = withLogging("Fibonacci Calculation", fibonacci);
        System.out.println("Result of timed calculation: " + timedFibonacci.apply(40));

        /*
         * Currying vs Partial Application:
         *
         * Standard function:     f(a, b) -> result
         * Curried function:      f(a) -> (b -> result)
         * Currying decomposes a multi-argument function into a chain of single-argument functions!
         */

        // Uncurried BiFunction: (rate, amount) -> total
        BiFunction<Double, Double, Double> calculateTax = (rate, amount) -> amount + (amount * rate);

        // Curried version: rate -> amount -> total
        Function<Double, Function<Double, Double>> curriedTaxCalculator = rate -> amount -> amount + (amount * rate);

        // PARTIAL APPLICATION:
        // We supply the 'rate' now (18% VAT), and obtain a specialized function that only needs 'amount'!
        Function<Double, Double> applyStandardVat = curriedTaxCalculator.apply(0.18);
        Function<Double, Double> applyReducedTax = curriedTaxCalculator.apply(0.05);

        System.out.println("Partial Application (18% on $200): $" + applyStandardVat.apply(200.0)); // $236.00
        System.out.println("Partial Application (5% on $200):  $" + applyReducedTax.apply(200.0));  // $210.00
    }

    /**
     * Higher-order decorator function: Wraps any Function with timing & logging diagnostics.
     */
    public static <T, R> Function<T, R> withLogging(String operationName, Function<T, R> fn) {
        return input -> {
            long start = System.nanoTime();
            System.out.println("   --> [START] " + operationName + " with input: " + input);
            R result = fn.apply(input);
            long elapsedUs = (System.nanoTime() - start) / 1_000;
            System.out.println("   <-- [END]   " + operationName + " completed in " + elapsedUs + " us (microseconds)");
            return result;
        };
    }

    // =========================================================================================
    // SECTION 11: LAZY EVALUATION & FUNCTION MEMOIZATION (CACHING)
    // =========================================================================================
    private static void section11_LazyEvaluationAndMemoization() {
        printHeader("11. LAZY EVALUATION & MEMOIZATION (CACHING)");

        /*
         * 1. Lazy Evaluation:
         * Postponing computation until the exact moment its value is needed.
         * In Java, Suppliers model lazy values: () -> expensiveOperation()
         */
        boolean debugEnabled = false;
        // Lazy evaluation (GOOD): Computes ONLY if needed
        logDebugLazily(debugEnabled, () -> {
            System.out.println("   [Expensive diagnostic computation running...]");
            return "Detailed system debug dump";
        });

        /*
         * 2. Memoization:
         * An optimization technique where pure functions cache their results.
         * If called with the same argument, the cached result is returned instantly
         * without re-executing the expensive logic.
         */
        Function<Integer, Long> slowFactorial = n -> {
            System.out.println("   [COMPUTING FACTORIAL for " + n + "...]");
            long result = 1;
            for (int i = 2; i <= n; i++) result *= i;
            return result;
        };

        // Wrap with generic memoize higher-order function:
        Function<Integer, Long> memoizedFactorial = memoize(slowFactorial);

        System.out.println("Call 1: 5! = " + memoizedFactorial.apply(5)); // Computes
        System.out.println("Call 2: 5! = " + memoizedFactorial.apply(5)); // Instant cache hit!
        System.out.println("Call 3: 6! = " + memoizedFactorial.apply(6)); // Computes 6
        System.out.println("Call 4: 5! = " + memoizedFactorial.apply(5)); // Instant cache hit!
    }

    private static void logDebugLazily(boolean enabled, Supplier<String> messageSupplier) {
        if (enabled) {
            System.out.println("DEBUG: " + messageSupplier.get());
        }
    }

    /**
     * Generic thread-safe Function Memoizer using ConcurrentHashMap.
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> function) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return input -> cache.computeIfAbsent(input, function);
    }

    // =========================================================================================
    // SECTION 12: MONADIC PROGRAMMING WITH OPTIONAL
    // =========================================================================================
    private static void section12_MonadicOptionalProgramming() {
        printHeader("12. MONADIC PROGRAMMING WITH java.util.Optional");
        /*
         * What is a Monad conceptually?
         * A wrapper around a value that lets you chain transformations (map, flatMap)
         * while abstracting away side concerns (like null checks or error propagation).
         *
         * In Java, Optional<T> acts as a monadic container for 0 or 1 element:
         *   - Optional.of(v) / Optional.ofNullable(v) / Optional.empty()
         *   - .map(Function): Transforms value if present, returns Optional<R>
         *   - .flatMap(Function returning Optional): Prevents Optional<Optional<T>> nesting
         *   - .filter(Predicate): Keeps value if predicate passes, else Optional.empty()
         *   - .or(Supplier<Optional>): Provides fallback Optional pipeline
         *   - .ifPresentOrElse(Consumer, Runnable): Pattern-matching style handling
         */

        record Address(String city, String zipCode) {}
        record UserProfile(String username, Optional<Address> address) {}

        UserProfile userWithAddress = new UserProfile("johndoe", Optional.of(new Address("Bengaluru", "560001")));
        UserProfile userWithoutAddress = new UserProfile("guest", Optional.empty());

        // Deep functional extraction without a SINGLE if (x != null) check!
        Function<UserProfile, String> getCitySafely = profile -> Optional.ofNullable(profile)
                .flatMap(UserProfile::address)                   // flatMap unwraps Optional<Address>
                .map(Address::city)                             // map extracts city String
                .map(String::toUpperCase)                       // map transforms to uppercase
                .filter(city -> !city.isBlank())                // filter validates
                .orElse("UNKNOWN CITY");                        // safe default

        System.out.println("User 1 City: " + getCitySafely.apply(userWithAddress));    // BENGALURU
        System.out.println("User 2 City: " + getCitySafely.apply(userWithoutAddress)); // UNKNOWN CITY
        System.out.println("Null Profile: " + getCitySafely.apply(null));              // UNKNOWN CITY

        // Java 9+ .or() method for chaining fallback Optionals:
        Optional<String> primaryConfig = Optional.empty();
        Optional<String> envVarConfig = Optional.empty();
        Supplier<Optional<String>> fileConfig = () -> Optional.of("file.properties config");

        String resolvedConfig = primaryConfig
                .or(() -> envVarConfig)
                .or(fileConfig)
                .orElse("default.config");
        System.out.println("Resolved config via .or() chain: " + resolvedConfig);
    }

    // =========================================================================================
    // SECTION 13: FUNCTIONAL DESIGN PATTERNS & RAILWAY-ORIENTED PROGRAMMING
    // =========================================================================================
    private static void section13_FunctionalPatternsAndResultEither() {
        printHeader("13. FUNCTIONAL DESIGN PATTERNS & RAILWAY PROGRAMMING (RESULT/EITHER)");

        /*
         * PATTERN 1: Strategy Pattern without class boilerplate!
         * In classic OOP, Strategy requires an interface and 5 separate class implementations.
         * In Functional Java, a Strategy is just a Function / Predicate!
         */
        record Order(String id, double amount, String paymentType) {}

        // Payment strategies defined as pure functions:
        Function<Double, Double> creditCardDiscount = amount -> amount * 0.95; // 5% discount
        Function<Double, Double> cryptoDiscount = amount -> amount * 0.90;     // 10% discount
        Function<Double, Double> standardPricing = Function.identity();         // No discount

        Map<String, Function<Double, Double>> strategies = Map.of(
                "CREDIT_CARD", creditCardDiscount,
                "CRYPTO", cryptoDiscount,
                "CASH", standardPricing
        );

        Order order = new Order("ORD-1", 1000.0, "CRYPTO");
        double finalPrice = strategies.getOrDefault(order.paymentType(), standardPricing).apply(order.amount());
        System.out.println("Strategy pattern final price for " + order.paymentType() + ": $" + finalPrice);

        /*
         * PATTERN 2: Railway-Oriented Programming (Result / Either pattern)
         * Throwing exceptions breaks referential transparency and disrupts function composition.
         * In FP, functions return a container: Result = Success(value) | Failure(error).
         * Success continues down the happy track; Failure bypasses remaining steps.
         */
        System.out.println("\nRailway-Oriented Processing Pipeline:");

        // Validation pipeline steps:
        Function<String, Result<String>> validatePresent = input ->
                (input != null && !input.isBlank())
                        ? Result.success(input.trim())
                        : Result.failure("Input string cannot be null or blank");

        Function<String, Result<Integer>> parseInteger = input -> {
            try {
                return Result.success(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                return Result.failure("'" + input + "' is not a valid integer");
            }
        };

        Function<Integer, Result<Integer>> validateAge = age ->
                (age >= 18 && age <= 120)
                        ? Result.success(age)
                        : Result.failure("Age " + age + " is outside permitted range (18-120)");

        // Pipeline execution for valid and invalid inputs:
        List<String> rawInputs = List.of("  25  ", "abc", "-5", " ");
        for (String raw : rawInputs) {
            Result<Integer> outcome = validatePresent.apply(raw)
                    .flatMap(parseInteger)
                    .flatMap(validateAge);

            outcome.fold(
                    validAge -> System.out.println("   [SUCCESS] Validated age: " + validAge),
                    error -> System.out.println("   [FAILURE] Rejected: " + error)
            );
        }
    }

    /**
     * Functional Result (Either) monad implementation using Java 21 Sealed Interfaces & Records.
     */
    public sealed interface Result<T> {
        record Success<T>(T value) implements Result<T> {}
        record Failure<T>(String error) implements Result<T> {}

        static <T> Result<T> success(T value) { return new Success<>(value); }
        static <T> Result<T> failure(String error) { return new Failure<>(error); }

        default boolean isSuccess() { return this instanceof Success; }

        default <R> Result<R> map(Function<? super T, ? extends R> mapper) {
            if (this instanceof Success<T> s) {
                return Result.success(mapper.apply(s.value()));
            }
            return Result.failure(((Failure<T>) this).error());
        }

        default <R> Result<R> flatMap(Function<? super T, Result<R>> mapper) {
            if (this instanceof Success<T> s) {
                return mapper.apply(s.value());
            }
            return Result.failure(((Failure<T>) this).error());
        }

        default void fold(Consumer<? super T> onSuccess, Consumer<String> onFailure) {
            if (this instanceof Success<T> s) {
                onSuccess.accept(s.value());
            } else if (this instanceof Failure<T> f) {
                onFailure.accept(f.error());
            }
        }
    }

    // =========================================================================================
    // SECTION 14: TAIL RECURSION & THE TRAMPOLINE PATTERN
    // =========================================================================================
    /**
     * The Trampoline Pattern:
     * Problem: In functional programming, loops are often replaced by recursion. However,
     * the JVM does NOT automatically optimize tail-calls (TCO). A deep recursion (e.g. 10,000+
     * calls) throws a StackOverflowError!
     * 
     * Solution: A Trampoline turns tail-recursive algorithms into iterative loops on the HEAP
     * using lazy Supplier evaluation, achieving infinite recursion depth with constant stack space!
     */
    @FunctionalInterface
    public interface Trampoline<T> {
        Trampoline<T> bounce();

        default boolean isComplete() {
            return false;
        }

        default T result() {
            throw new UnsupportedOperationException("Computation not yet complete");
        }

        default T run() {
            Trampoline<T> current = this;
            while (!current.isComplete()) {
                current = current.bounce();
            }
            return current.result();
        }

        static <T> Trampoline<T> done(T result) {
            return new Trampoline<>() {
                @Override public boolean isComplete() { return true; }
                @Override public T result() { return result; }
                @Override public Trampoline<T> bounce() { throw new UnsupportedOperationException(); }
            };
        }

        static <T> Trampoline<T> more(Supplier<Trampoline<T>> next) {
            return next::get;
        }
    }

    private static Trampoline<Long> tailRecursiveSum(long n, long acc) {
        if (n <= 0) {
            return Trampoline.done(acc);
        }
        // Instead of directly calling tailRecursiveSum (which pushes a stack frame),
        // we return a lazy bounce wrapped in a lambda Supplier!
        return Trampoline.more(() -> tailRecursiveSum(n - 1, acc + n));
    }

    private static void section14_TailRecursionAndTrampoline() {
        printHeader("14. TAIL RECURSION & THE TRAMPOLINE PATTERN");

        System.out.println("Demonstrating 50,000 recursive steps without blowing the JVM call stack:");
        long deepRecursionDepth = 50_000L;
        long totalSum = tailRecursiveSum(deepRecursionDepth, 0).run();
        System.out.println("Trampoline successfully summed 1.." + deepRecursionDepth + " = " + totalSum);
        System.out.println("Notice: Constant stack frame depth maintained by iterative bouncing!");
    }

    // =========================================================================================
    // SECTION 15: ALGEBRAIC DATA TYPES (ADTs) & JAVA 21 PATTERN MATCHING
    // =========================================================================================
    /**
     * Algebraic Data Types (ADTs) in Functional Java:
     *  - Product Types: Records (data defined by A AND B AND C)
     *  - Sum Types: Sealed Interfaces (data defined by X OR Y OR Z)
     * 
     * Combining Sealed Hierarchies with Java 21 exhaustive pattern matching yields
     * mathematical purity and eliminates runtime TypeCastExceptions.
     */
    public sealed interface Expression {
        record Constant(double value) implements Expression {}
        record Add(Expression left, Expression right) implements Expression {}
        record Multiply(Expression left, Expression right) implements Expression {}
        record Negate(Expression expr) implements Expression {}
    }

    public static double evaluateExpression(Expression expr) {
        // Pure functional recursive pattern-matching evaluator:
        return switch (expr) {
            case Expression.Constant(double v) -> v;
            case Expression.Add(var l, var r) -> evaluateExpression(l) + evaluateExpression(r);
            case Expression.Multiply(var l, var r) -> evaluateExpression(l) * evaluateExpression(r);
            case Expression.Negate(var inner) -> -evaluateExpression(inner);
        };
    }

    private static void section15_AlgebraicDataTypesAndPatternMatching() {
        printHeader("15. ALGEBRAIC DATA TYPES (ADTs) & JAVA 21 PATTERN MATCHING");

        // Building expression: -( (5 + 3) * 2 ) = -16.0
        Expression expr = new Expression.Negate(
                new Expression.Multiply(
                        new Expression.Add(new Expression.Constant(5), new Expression.Constant(3)),
                        new Expression.Constant(2)
                )
        );

        double evaluated = evaluateExpression(expr);
        System.out.println("Evaluated AST Expression -( (5 + 3) * 2 ): " + evaluated);
    }

    // =========================================================================================
    // SECTION 16: ASYNCHRONOUS FUNCTIONAL COMPOSITION WITH CompletableFuture
    // =========================================================================================
    private static void section16_AsyncFunctionalCompletableFuture() {
        printHeader("16. ASYNCHRONOUS FUNCTIONAL PIPELINES (CompletableFuture as a Monad)");
        /*
         * CompletableFuture implements monadic concepts for asynchronous tasks:
         *  - supplyAsync(Supplier):     Monadic unit/return (wraps async computation)
         *  - thenApply(Function):       Functor map (transforms value once completed)
         *  - thenCompose(Function->CF): Monad flatMap (chains dependent async calls)
         *  - thenCombine(CF, BiFn):     Applicative zip (runs 2 futures in parallel and joins)
         *  - exceptionally(Function):   Error recovery
         */

        CompletableFuture<String> asyncPipeline = CompletableFuture
                .supplyAsync(() -> {
                    // Step 1: Fetch order
                    return "Order-9872";
                })
                .thenApply(String::toUpperCase) // Functor Map
                .thenCompose(orderId -> CompletableFuture.supplyAsync(() -> {
                    // Step 2: Dependent fetch of account details (Monad flatMap)
                    return "Account(ID=ACC-44, " + orderId + ")";
                }))
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> 149.95), // Step 3: Run independent pricing in parallel
                        (acc, price) -> acc + " -> Charge: $" + price // Step 4: Zip results together
                )
                .exceptionally(ex -> "Async pipeline fallback: " + ex.getMessage());

        try {
            String asyncResult = asyncPipeline.get(2, TimeUnit.SECONDS);
            System.out.println("Async Monadic Pipeline Output: " + asyncResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================================
    // SECTION 17: REAL-WORLD FP PRACTICE PROBLEMS & INTERVIEW PEARLS
    // =========================================================================================
    private static void section17_RealWorldFPChallenges() {
        printHeader("17. REAL-WORLD FP PRACTICE PROBLEMS & INTERVIEW PEARLS");

        // CHALLENGE 1: Dynamic Multi-Criteria Predicate Builder
        // In real-world search APIs, users may or may not supply filters (category, minPrice, inStock).
        // Using FP, we combine optional filters dynamically using reduce(Predicate::and)!
        System.out.println("Challenge 1: Dynamic Predicate Filtering");
        List<Product> catalog = List.of(
                new Product("P1", "Laptop", "Electronics", 1200.0, true),
                new Product("P2", "Phone", "Electronics", 800.0, false),
                new Product("P3", "Desk", "Furniture", 250.0, true),
                new Product("P4", "Monitor", "Electronics", 300.0, true)
        );

        // Active filter criteria from a search query (some may be null / empty):
        String filterCategory = "Electronics";
        Double filterMaxPrice = 1000.0;
        Boolean filterOnlyInStock = true;

        List<Predicate<Product>> filterChain = new ArrayList<>();
        if (filterCategory != null) {
            filterChain.add(p -> p.category().equalsIgnoreCase(filterCategory));
        }
        if (filterMaxPrice != null) {
            filterChain.add(p -> p.price() <= filterMaxPrice);
        }
        if (filterOnlyInStock != null && filterOnlyInStock) {
            filterChain.add(Product::inStock);
        }

        // Fold all predicates into one single predicate (or default to 'always true'):
        Predicate<Product> compositeFilter = filterChain.stream()
                .reduce(p -> true, Predicate::and);

        List<Product> matchedProducts = catalog.stream().filter(compositeFilter).toList();
        System.out.println("   Dynamically filtered products (Electronics, <=1000, inStock): " + matchedProducts);

        // CHALLENGE 2: Word Frequency Counter (Pure FP with Collectors)
        System.out.println("\nChallenge 2: Word Frequency Counter");
        String text = "java streams and functional java programming with functional purity";
        Map<String, Long> wordFrequencies = Arrays.stream(text.split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println("   Word Frequencies: " + wordFrequencies);

        // CHALLENGE 3: First Non-Repeating Character in a String
        System.out.println("\nChallenge 3: First Non-Repeating Character");
        String streamInput = "swiss";
        Optional<Character> firstUnique = streamInput.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == 1L)
                .map(Map.Entry::getKey)
                .findFirst();
        System.out.println("   First non-repeating character in '" + streamInput + "': " + firstUnique.orElse('?')); // 'w'

        // CHALLENGE 4: Infinite Sequence Generation with Stream.iterate
        System.out.println("\nChallenge 4: Infinite Sequences (Fibonacci Pairs)");
        // Using an int pair [a, b] where next is [b, a + b]
        List<Integer> fibonacciSeries = Stream.iterate(new int[]{0, 1}, pair -> new int[]{pair[1], pair[0] + pair[1]})
                .limit(10)
                .map(pair -> pair[0])
                .toList();
        System.out.println("   First 10 Fibonacci numbers generated functionally: " + fibonacciSeries);
    }

    // =========================================================================================
    // SECTION 18: UNDER THE HOOD & FP BEST PRACTICES
    // =========================================================================================
    private static void section18_UnderTheHoodAndBestPractices() {
        printHeader("18. UNDER THE HOOD (JVM invokedynamic) & BEST PRACTICES");
        /*
         * How Java Lambdas Work Under The Hood:
         * -------------------------------------
         * Q: Does a lambda compile to an Anonymous Inner Class (e.g. Functional$1.class)?
         * A: NO! That was intentionally avoided to prevent class explosion and slow startup.
         *
         * Bytecode Mechanics:
         * 1. The Java compiler takes the lambda body and desugars it into a private synthetic method
         *    inside the host class (e.g., 'private static synthetic lambda$main$0(...)').
         * 2. At the call site, it emits an 'invokedynamic' (INDY) bytecode instruction.
         * 3. The first time INDY is encountered at runtime, it calls a Bootstrap Method (BSM):
         *    'java.lang.invoke.LambdaMetafactory.metafactory(...)'.
         * 4. LambdaMetafactory dynamically spins an internal class implementing the interface using
         *    direct MethodHandle invocation.
         * 5. Non-capturing lambdas are cached as singletons. Capturing lambdas instantiate on demand.
         *
         * Best Practices & Rules of Thumb:
         * --------------------------------
         *  DO:
         *   - Keep lambdas short, concise, and focused (1-3 lines).
         *   - Extract complex logic into a private helper method and use a Method Reference (::).
         *   - Keep lambdas pure (free of side effects and external mutable state).
         *   - Favor primitive specializations (IntPredicate, etc.) in performance-critical code.
         *   - Use @FunctionalInterface on custom interfaces to guarantee SAM contract.
         *
         *  DON'T:
         *   - Do not mutate local variables through array tricks (e.g. final int[] counter = {0}).
         *   - Do not pass null into functional interfaces or Optional methods.
         *   - Do not overuse lambdas where a standard loop is significantly clearer.
         *   - Do not use Stream.peek() for business logic (it's strictly intended for debugging).
         */

        System.out.println("Java Lambda Under-the-hood summary:");
        System.out.println(" - Compiled via: invokedynamic (INDY)");
        System.out.println(" - Bootstrapped by: java.lang.invoke.LambdaMetafactory");
        System.out.println(" - Memory footprint: Non-capturing lambdas = singletons (zero allocation)");
        System.out.println(" - Scoping: Lexical scoping for 'this', no enclosing class shadow");
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}

package org.example.JavaPractice;

public class Functional {
    @FunctionalInterface
    interface Greeter {
        String greet(String name);          // single abstract method

        default Greeter twice() {           // allowed
            return n -> greet(greet(n));
        }
        static Greeter polite() {           // allowed
            return n -> "Dear " + n;
        }
        boolean equals(Object o);           // Object method: doesn't count
    }



    public static void main(String[] args) {
            Greeter g = name -> "Hello, " + name;
            System.out.println(g.greet("Asha"));    // Hello, Asha
// 1. Anonymous class
        Greeter a = new Greeter() {
            public String greet(String n) { return "Hi " + n; }
        };

// 2. Lambda
        Greeter b = n -> "Hi " + n;

// 3. Method reference
        Greeter c = String::toUpperCase;

//        3. Core Built-in Interfaces (java.util.function)
//        Predicate<T>: boolean test(T)
//                java
//        Predicate<String> isEmpty = String::isEmpty;
//        Predicate<Integer> even = n -> n % 2 == 0;
//        Predicate<Integer> positive = n -> n > 0;
//
//        even.and(positive).test(4);     // true
//        even.or(positive).test(-3);     // false
//        even.negate().test(3);          // true
//        Predicate.isEqual("a").test("a"); // true
//        Predicate.not(String::isBlank);   // Java 11
//        Function<T,R>: R apply(T)
//                java
//        Function<String, Integer> len = String::length;
//        Function<Integer, Integer> square = x -> x * x;
//
//        len.andThen(square).apply("abc");    // square(len) = 9
//        square.compose(len).apply("abc");    // same: 9
//        Function.<Integer>identity().apply(5); // 5
//        Consumer<T>: void accept(T)
//                java
//        Consumer<String> print = System.out::println;
//        Consumer<String> log = s -> System.out.println("LOG: " + s);
//
//        print.andThen(log).accept("data");   // prints twice, in order
//        Supplier<T>: T get()
//        java
//        Supplier<LocalDate> today = LocalDate::now;
//        Supplier<List<String>> factory = ArrayList::new;
//
//        Optional.empty().orElseGet(() -> "default");   // lazy default
//        UnaryOperator<T> and BinaryOperator<T>
//                java
//        UnaryOperator<String> upper = String::toUpperCase;      // Function<T,T>
//        BinaryOperator<Integer> add = Integer::sum;             // BiFunction<T,T,T>
//
//        BinaryOperator.minBy(Comparator.<Integer>naturalOrder()).apply(3, 9); // 3
//        BinaryOperator.maxBy(Comparator.<Integer>naturalOrder()).apply(3, 9); // 9
//        4. Two-Argument Variants
//                java
//        BiFunction<Integer, Integer, Integer> mul = (a, b) -> a * b;
//        BiPredicate<String, Integer> longer = (s, n) -> s.length() > n;
//        BiConsumer<String, Integer> show = (k, v) -> System.out.println(k + "=" + v);
//
//        Map<String, Integer> map = Map.of("a", 1, "b", 2);
//        map.forEach(show);                                   // BiConsumer in action
//
//        mul.andThen(x -> x + 1).apply(3, 4);                 // 13
//        5. Primitive Specializations (avoid boxing)
//        Family	Examples
//        Predicate	IntPredicate, LongPredicate, DoublePredicate
//        Function	IntFunction<R>, ToIntFunction<T>, IntToDoubleFunction
//        Supplier	IntSupplier, BooleanSupplier, DoubleSupplier
//        Consumer	IntConsumer, ObjIntConsumer<T>
//                Operator	IntUnaryOperator, IntBinaryOperator
//        java
//        IntPredicate odd = n -> n % 2 != 0;
//        ToIntFunction<String> length = String::length;
//        IntBinaryOperator max = Math::max;
//        IntSupplier dice = () -> new Random().nextInt(6) + 1;
//
//        int total = IntStream.rangeClosed(1, 5).reduce(0, (a, b) -> a + b);  // 15
//        6. Legacy Functional Interfaces (pre-Java 8, now usable with lambdas)
//        java
//        Runnable r = () -> System.out.println("running");        // void run()
//        Callable<Integer> c = () -> 42;                          // V call() throws Exception
//        Comparator<String> byLen = Comparator.comparing(String::length);
//        Comparator<String> desc = byLen.reversed().thenComparing(Comparator.naturalOrder());
//
//        new Thread(r).start();
//        ExecutorService ex = Executors.newSingleThreadExecutor();
//        Future<Integer> f = ex.submit(c);
//        7. Custom Functional Interfaces
//        Generic custom interface
//        java
//        @FunctionalInterface
//        interface TriFunction<A, B, C, R> {
//            R apply(A a, B b, C c);
//        }
//
//        TriFunction<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;
//        sum3.apply(1, 2, 3);    // 6
    }
}

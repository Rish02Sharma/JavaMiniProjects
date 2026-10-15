package org.example.JavaPractice.streams;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Streams {

    record Employee(String name, String dept, double salary, int age) {}

    public static void main(String[] args){
        Map<String,String> map = new HashMap<>();
        map.put("IT","Ramesh");
        map.put("IT","Rahul");
        map.put("IT","Akshay");
        map.put("Admin","ABC");
        map.put("Finance","Rakesh");
        map.put("Admin","Rasabh");
        map.put("Library","Ram");

        Map<String, List<String>> result = map.entrySet().stream().collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
        ));

        List<Integer> myList = Arrays.asList(45,36,78,12,55,22);
        List<Integer> sortedList = myList.stream()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        List<Integer> maxThree = sortedList.stream()
                .limit(3)
                .collect(Collectors.toList());

        List<Integer> minThree = sortedList.reversed().stream()
                .limit(3)
                .collect(Collectors.toList());

        System.out.print("Max three ");
        maxThree.forEach(v -> System.out.print(v));

        System.out.print("Min three ");
        minThree.forEach(v -> System.out.print(v));

        //      WAYS TO CREATE STREAMS
//        list.stream();                       // from collection
//        Arrays.stream(arr);                  // from array
//        Stream.of("a", "b", "c");            // from values
//        IntStream.range(1, 5);               // 1,2,3,4
//        IntStream.rangeClosed(1, 5);         // 1..5
//        Stream.iterate(1, x -> x * 2);       // infinite: 1,2,4,8...
//        Stream.generate(Math::random);       // infinite

        List<Employee> emps = List.of(
                new Employee("Asha",  "IT",      90000, 30),
                new Employee("Ravi",  "IT",      70000, 25),
                new Employee("Meera", "HR",      50000, 40),
                new Employee("Karan", "HR",      65000, 35),
                new Employee("Sita",  "Finance", 80000, 45)
        );

        List<String> r = emps.stream()               // source
                .filter(e -> e.salary() > 60000)              // intermediate
                .map(Employee::name)                          // intermediate
                .collect(Collectors.toList());                // terminal
        // [Asha, Ravi, Karan, Sita]


        // Names sorted by salary descending
        List<String> salarySortedNames = emps.stream()
                .sorted(Comparator.comparing(Employee::salary).reversed())
                .map(Employee::name)
                .toList();

        System.out.println("Salary sorted Names");
        salarySortedNames.forEach(v -> System.out.print(v));

        // Total salary
        Optional<Double> sumSalary = emps.stream().map(Employee::salary).reduce((s1, s2) -> s1 + s2);
        System.out.println("Salary sum " + sumSalary.get());

        // Max Salary
        List<Employee> maxSalary = emps.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).limit(1).toList();
        Optional<Employee> oldest = emps.stream().max(Comparator.comparingInt(Employee::age));

        System.out.println("Max salary " + maxSalary.get(0));
        System.out.println("Max age " + oldest.get());

        // toSet, toMap
        Set<String> depts = emps.stream().map(Employee::dept).collect(Collectors.toSet());
        Map<String, Double> nameToSalary =
                emps.stream().collect(Collectors.toMap(Employee::name, Employee::salary));

        // joining
        String names = emps.stream().map(Employee::name).collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Joining example " + names);

        // groupingBy
        Map<String, List<Employee>> byDept = emps.stream().collect(Collectors.groupingBy(Employee::dept));

        // groupingBy + downstream collectors
        Map<String, Long>   countByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
        Map<String, Double> avgSalary   = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.averagingDouble(Employee::salary)));
        Map<String, List<String>> namesByDept = emps.stream()
                .collect(Collectors.groupingBy(Employee::dept,
                        Collectors.mapping(Employee::name, Collectors.toList())));

        // partitioningBy (always two keys: true/false)
        Map<Boolean, List<Employee>> highLow =
                emps.stream().collect(Collectors.partitioningBy(e -> e.salary() > 70000));

        // summarizing statistics
        DoubleSummaryStatistics stats =
                emps.stream().collect(Collectors.summarizingDouble(Employee::salary));
        // stats.getMin(), getMax(), getAverage(), getSum(), getCount()

//        Q12. What happens with toMap if there are duplicate keys?
//        It throws IllegalStateException: Duplicate key. Provide a merge function:
//
//        java
//        Collectors.toMap(Employee::dept, Employee::salary, Double::sum)

        // FLAT MAP

        List<List<Integer>> nested = List.of(List.of(1,2), List.of(3,4), List.of(5));
        List<Integer> flat = nested.stream().flatMap(List::stream).toList();  // [1,2,3,4,5]

        // Words from sentences
        Stream.of("hello world", "java streams")
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .toList();   // [hello, world, java, streams]

        // teeing (Java 12): two collectors, merge results
        var result4 = emps.stream().collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.averagingDouble(Employee::salary),
                (count, avg) -> count + " employees, avg " + avg));

        // collectingAndThen: post-process result
        List<String> unmod = emps.stream().map(Employee::name)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        // Top earner per department
        Map<String, Optional<Employee>> top = emps.stream().collect(
                Collectors.groupingBy(Employee::dept,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::salary))));

        // 1. Second highest salary
        emps.stream().map(Employee::salary).distinct()
                .sorted(Comparator.reverseOrder()).skip(1).findFirst();

// 2. Count word frequency
        Map<String, Long> freq = Arrays.stream("to be or not to be".split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// 3. First non-repeating character
        String s = "swiss";
        Optional<Character> ch = s.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey).findFirst();   // w

// 4. Find duplicates in a list
        Set<Integer> seen = new HashSet<>();
//        List<Integer> dups = nums.stream().filter(n -> !seen.add(n)).distinct().toList();

// 5. Sum of squares of even numbers
        IntStream.rangeClosed(1, 10).filter(n -> n % 2 == 0).map(n -> n * n).sum();   // 220

// 6. Check if a string is a palindrome
        boolean pal = IntStream.range(0, s.length() / 2)
                .allMatch(i -> s.charAt(i) == s.charAt(s.length() - 1 - i));

// 7. Employee with highest salary in each department
        Map<String, Employee> best = emps.stream().collect(Collectors.toMap(
                Employee::dept, Function.identity(),
                BinaryOperator.maxBy(Comparator.comparingDouble(Employee::salary))));

//// 8. Merge two lists without duplicates, sorted
//        Stream.concat(a.stream(), b.stream()).distinct().sorted().toList();
//
//// 9. Group names by first letter
//        names.stream().collect(Collectors.groupingBy(n -> n.charAt(0)));
//
//// 10. Nth highest element (n = 3)
//        nums.stream().distinct().sorted(Comparator.reverseOrder()).skip(2).findFirst();


        // Word with highest length
        String str = "I am learning streams in JAVA";
        String word = Stream.of(str.split(" ")).reduce((s1, s2) -> s1.length() > s2.length()?s1:s2).get();
        System.out.print("Longest word " + word);
    }
}

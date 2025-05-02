package org.example.JavaPractice.streams;

import java.util.*;
import java.util.stream.Collectors;

public class Streams {

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
    }
}

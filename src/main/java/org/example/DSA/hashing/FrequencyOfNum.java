package org.example.DSA.hashing;

import java.util.HashMap;
import java.util.Map;

public class FrequencyOfNum {

    public static void Frequency(int[] arr, int n) {
        // Create a HashMap to store frequency of each element
        HashMap<Integer, Integer> map = new HashMap<>();

        // Traverse the array and count frequencies
        for (int i = 0; i < n; i++) {
            map.put(arr[i], map.getOrDefault(arr[i], 0) + 1);
        }

        // Traverse through the HashMap and print frequencies
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public static void main(String[] args){
        int[] arr = new int[]{1, 2, 1, 2, 3, 4, 3, 5, 6};
        Frequency(arr, arr.length);
    }
}

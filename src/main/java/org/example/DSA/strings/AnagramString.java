package org.example.DSA.strings;

import java.util.*;

public class AnagramString {
    // This solution is Using sorted words as keys - O(n*k*log(k)) Time and O(n*k) Space
        public static List<List<String>> groupAnagrams(String[] strs) {

            Map<String, List<String>> map = new HashMap<>();

            for(int i=0; i<strs.length; i++){
                // convert to char array, sort and
                // then re-convert to string
                String word = strs[i];
                char[] letters = word.toCharArray();
                Arrays.sort(letters);
                String newWord = new String(letters);

                // calculate hashcode of string
                // after sorting
                if (map.containsKey(newWord)) {

                    map.get(newWord).add(word);
                }
                else {

                    // This is the first time we are
                    // adding a word for a specific
                    // hashcode
                    List<String> words = new ArrayList<>();
                    words.add(word);
                    map.put(newWord, words);
                }

            }


            List<List<String>> list = new ArrayList<>();
            for (String s : map.keySet()) {
                List<String> values = map.get(s);
                list.add(values);
            }
            return list;
        }

//    Using Frequency as keys - O(n*k) Time and O(n*k) Space
    // Function to generate hash of word s
    static String getHash(String s) {
        StringBuilder hash = new StringBuilder();
        int[] freq = new int[26];

        // Count frequency of each character
        for (char ch : s.toCharArray()) {
            freq[ch - 'a']++;
        }

        // Append the frequency to construct the hash
        for (int i = 0; i < 26; i++) {
            hash.append(freq[i]);
            hash.append("$");
        }

        return hash.toString();
    }

    static ArrayList<ArrayList<String>> anagrams(String[] arr) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        Map<String, Integer> mp = new HashMap<>();

        for (int i = 0; i < arr.length; i++) {
            String key = getHash(arr[i]);

            // If key is not present, create a new group
            if (!mp.containsKey(key)) {
                mp.put(key, res.size());
                res.add(new ArrayList<>());
            }

            // Add the string to its correct group
            res.get(mp.get(key)).add(arr[i]);
        }

        return res;
    }

    public static void main(String[] args) {
        String[] str = new String[]{"eat","tea","tan","ate","nat","bat"};
        System.out.print("Anagram String " + groupAnagrams(str));
    }
}

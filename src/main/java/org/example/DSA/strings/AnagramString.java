package org.example.DSA.strings;

import java.util.*;

public class AnagramString {
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

    public static void main(String[] args) {
        String[] str = new String[]{"eat","tea","tan","ate","nat","bat"};
        System.out.print("Anagram String " + groupAnagrams(str));
    }
}

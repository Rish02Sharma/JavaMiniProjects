package org.example.DSA.strings;

public class LongestCommonPrefix {
    public static String longestCommonPrefix(String[] strs) {
        String s = strs[0];

        for (int i = 1; i < strs.length; i++) {
            while (!strs[i].startsWith(s)) {
                s = s.substring(0, s.length() - 1);
            }
        }

        return s;
    }

    public static void main(String[] args){
        String[] arr = {"flower","flow","flight"};
        String ans = longestCommonPrefix(arr);
        System.out.println("Longest common prefix: " + ans);
    }
}

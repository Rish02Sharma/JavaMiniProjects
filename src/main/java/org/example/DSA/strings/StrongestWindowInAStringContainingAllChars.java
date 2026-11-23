package org.example.DSA.strings;

//Smallest window in a String containing all characters of other String
//Last Updated :
//        25 Oct, 2026
//Given two strings s and p, find the smallest substring in s consisting of all the characters (including duplicates) of the string p.
//
//Return an empty string in case no such substring is present.
//If there are multiple such substrings of the same length found, return the one with the least starting index.
//        Examples:
//
//Input: s = "timetopractice", p = "toc"
//Output: "toprac"
//Explanation: "toprac" is the smallest substring in which "toc" can be found.
//
//        Input: s = "zoomlazapzo", p = "oza"
//Output: "apzo"
//Explanation: "apzo" is the smallest substring in which "oza" can be found.
//
//        Input: s = "zoom", p = "zooe"
//Output: ""
//Explanation: No substring is present containing all characters of p.

public class StrongestWindowInAStringContainingAllChars {

    public static void main(String[] args) {
        String s = "timetopractice";
        String p = "toc";

        System.out.println("Smallest window containing all chars of other string is " + smallestWindow(s, p));

    }

    private static String smallestWindow(String s, String p) {



        return null;
    }
}

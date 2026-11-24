package org.example.DSA.strings;

public class LongestPalindrome {


    // This is a O(n^2) solution
    static String check(String s) {
        int n = s.length();

        int start = 0, maxLen = 1;

        for (int i = 0; i < n; i++) {

            // this runs two times for both odd and even
            // length palindromes.
            // j = 0 means odd and j = 1 means even length
            for (int j = 0; j <= 1; j++) {
                int low = i;
                int high = i + j;

                // expand substring while it is a palindrome
                // and in bounds
                while (low >= 0 && high < n && s.charAt(low) == s.charAt(high))
                {
                    int currLen = high - low + 1;
                    if (currLen > maxLen) {
                        start = low;
                        maxLen = currLen;
                    }
                    low--;
                    high++;
                }
            }
        }

        return s.substring(start, start + maxLen);
    }


    // O(n) time and O(n) space solution
    public static String getLongestPal(String s)
    {
        if (s.isEmpty())
            return "";

        // Transform the string using separators
        // and sentinels.
        StringBuilder ms = new StringBuilder("@");

        for (char c : s.toCharArray()) {
            ms.append("#").append(c);
        }

        ms.append("#$");

        int m = ms.length();
        int[] p = new int[m];

        int l = 0, r = 0;

        // Manacher's algorithm
        for (int i = 1; i < m - 1; ++i) {

            // Use the mirror position
            if (i < r) {
                int mirror = l + r - i;
                p[i] = Math.min(r - i, p[mirror]);
            }

            // Expand around the current center
            while (ms.charAt(i + 1 + p[i])
                    == ms.charAt(i - 1 - p[i])) {
                ++p[i];
            }

            // Update the current palindrome range
            if (i + p[i] > r) {
                l = i - p[i];
                r = i + p[i];
            }
        }

        int maxLen = 1;
        int start = 0;

        // Find the longest palindrome
        for (int i = 1; i < m - 1; ++i) {
            if (p[i] > maxLen) {
                maxLen = p[i];
                start = (i - p[i]) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    public static void main(String[] args) {
        String s = "forgeeksskeegfor";
        System.out.println("Longest palindrome is : " + getLongestPal(s));
    }
}

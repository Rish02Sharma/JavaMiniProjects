package org.example.DSA.strings;

public class SentencedPalindrome {

    public static void main(String[] args) {
        String s = "Too hot to hoot.";
        System.out.println("Sentence palindrome : " + check(s));
    }

    private static boolean check(String s) {
        int i=0, j=s.length()-1;

        // Compares character until they are equal
        while (i < j) {

            // Skip non alphabet character from left side
            if (!Character.isLetterOrDigit(s.charAt(i)))
                i++;

                // Skip non alphabet character from right side
            else if (!Character.isLetterOrDigit(s.charAt(j)))
                j--;

                // If characters are equal update the pointers
            else if (Character.toLowerCase(s.charAt(i))
                    == Character.toLowerCase(s.charAt(j))) {
                i++;
                j--;
            }

            // If characters are not equal then sentence
            // is not palindrome
            else
                return false;
        }
        return true;
    }
}

package org.example.DSA.recursion;

public class PalindromeString {

    static boolean isPalindrome(int s, int e, String newstr){
        if(s==e && newstr.charAt(s) == newstr.charAt(e)) return true;

        if(newstr.charAt(s) == newstr.charAt(e)){
            return isPalindrome(++s, --e, newstr);
        }else {
            return false;
        }
    }

    public static void main(String[] args){
        String s = "A man, a plan, a canal: Panama";
        s = s.toLowerCase();
        s = s.replaceAll("[^a-z0-9]", "");
        int len = s.length();
        System.out.print("Is it palindrome" + isPalindrome(0, len-1, s));
    }
}

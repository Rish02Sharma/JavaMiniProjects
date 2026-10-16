package org.example.DSA.strings;

import java.util.HashSet;

public class LongestSubstringWithoutRepeatingChar {

    public static int lengthOfLongestSubstring(String s) {
        if(s.length() == 0) return 0;
        HashSet<Character> set = new HashSet<>();
        int left = 0 , right = 0 , ans = 0;
        while(left < s.length() && right < s.length()){
            if(!set.contains(s.charAt(right))){
                set.add(s.charAt(right++));
                ans = Math.max(ans,right - left);
            }else{
                set.remove(s.charAt(left++));
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        String str = "abcabcbb";
        System.out.print("Longest substring " + lengthOfLongestSubstring(str));
    }
}

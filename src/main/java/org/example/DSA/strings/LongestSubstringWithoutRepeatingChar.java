package org.example.DSA.strings;

import java.util.Arrays;
import java.util.HashSet;

public class LongestSubstringWithoutRepeatingChar {

    // This solution will take O(n) time and o(1) extra space
    public static int optLengthOfLongestUniqueSubstring(String s){
        if(s.length()==0) return 0;
        int[] li = new int[26];
        Arrays.fill(li, -1);
        int max=0;
        int l=0;

        for(int i=0; i<s.length(); i++){
            l = Math.max(l, li[s.charAt(i) - 'a']+1);
            max = Math.max(max, i-l+1);
            li[s.charAt(i)-'a'] = i;
        }
        return max;
    }

    // This will take O(n) but it is taking O(n) extra space as well so we can look for an optimal solution
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
        System.out.print("Longest substring " + optLengthOfLongestUniqueSubstring(str));
    }
}

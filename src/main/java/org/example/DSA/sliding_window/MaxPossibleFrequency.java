package org.example.DSA.sliding_window;

import java.util.Arrays;

public class MaxPossibleFrequency {
    public static int maxFrequency(int[] nums, int k) {
        Arrays.sort(nums);
        int maxLen=0;
        int left=0;
        long cost=0,winSum=0;//long because of int overflow
        for(int right=0;right<nums.length;right++){
            winSum+=nums[right]; //sum of elements inside the current window
            cost=(long)nums[right]*(right-left+1)-winSum;//key formulae
            while(cost>k){
                // Window is too expensive i.e. our cost exceeds k operation, so
                // Remove left element and shrink the window
                winSum-=nums[left];
                left++;
                cost=nums[right]*(right-left+1)-winSum;//calculate cost again
            }
            maxLen=Math.max(maxLen,right-left+1);//update maxLen

        }
        return maxLen;
    }

    public static void main(String[] args){
        int[] arr = new int[]{1, 2, 1, 2, 3, 4, 3, 5, 6};
        System.out.print("Max " + maxFrequency(arr, 5));

    }
}

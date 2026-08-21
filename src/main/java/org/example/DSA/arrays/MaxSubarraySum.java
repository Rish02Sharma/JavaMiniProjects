package org.example.DSA.arrays;

import java.util.Arrays;

public class MaxSubarraySum {

    public static int[] printMaxSubarray(int[] nums){
        int currMax=nums[0];
        int maxSoFar=nums[0];
        int len=nums.length;
        int start=0;
        int end=0;
        int tempStart=0;

        for(int i=1; i<len; i++){
            if (nums[i] > currMax + nums[i]) {
                currMax = nums[i];
                tempStart = i;
            } else {
                currMax += nums[i];
            }

            // Update overall maximum and boundary indices
            if (currMax > maxSoFar) {
                maxSoFar = currMax;
                start = tempStart;
                end = i;
            }
        }

        return Arrays.copyOfRange(nums, start, end + 1);
    }

    public static void main(String[] args){
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int[] ans = printMaxSubarray(arr);
        for(int a: ans){
            System.out.print(" " + a);
        }
    }
}

package org.example.DSA.arrays.slidingWindow;

public class MinimumSizeSubarrayLength {
    public static int minSubArrayLen(int target, int[] nums) {

        int i = 0;
        int sum = 0;
        int minLength = Integer.MAX_VALUE;

        for (int j = 0; j < nums.length; j++) {

            sum += nums[j];

            while (sum >= target) {

                minLength = Math.min(minLength, j - i + 1);

                sum -= nums[i];
                i++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    public static void main(String[] args){
        int[] arr = new int[]{2,3,1,2,4,3};

        int sol = minSubArrayLen(7, arr);
            System.out.print("Minimum size: " + sol);
    }
}

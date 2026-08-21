package org.example.DSA.arrays.prefixSum;

import java.util.HashMap;

public class SubArrayWithSumK {

    //This is the optimal solution which will work for both positive and negative numbers
    // This is using prefix sum algo
    public int subarraySum(int[] nums, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);

        int count = 0;
        int sum = 0;

        for(int i=0; i<nums.length; i++){
            sum += nums[i];
            int n = map.getOrDefault(sum-k, 0);
            count += n;

            map.put(sum, map.getOrDefault(sum,0)+1);
        }

        return count;
    }

    // This will work only for the case of Non-negative numbers
    public static int subarraySumTwoPointers(int[] nums, int k) {
        int left = 0;
        int currentSum = 0;
        int count = 0;

        for (int right = 0; right < nums.length; right++) {
            currentSum += nums[right];

            // Shrink window from the left if currentSum exceeds k
            while (left <= right && currentSum > k) {
                currentSum -= nums[left];
                left++;
            }

            // Note: If array has zeros, shrinking until currentSum == k needs careful handling
            if (currentSum == k) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 1, 1, 1};
        int k = 3;
        System.out.println("Count: " + subarraySumTwoPointers(nums, k)); // Output: 3 ([1,2], [3], [1,1,1])
    }
}

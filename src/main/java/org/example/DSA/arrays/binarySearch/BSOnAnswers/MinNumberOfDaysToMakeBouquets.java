package org.example.DSA.arrays.binarySearch.BSOnAnswers;

/*
1482. Minimum Number of Days to Make m Bouquets
Solved
        Medium
Topics
premium lock icon
        Companies
Hint
You are given an integer array bloomDay, an integer m and an integer k.

You want to make m bouquets. To make a bouquet, you need to use k adjacent flowers from the garden.

The garden consists of n flowers, the ith flower will bloom in the bloomDay[i] and then can be used in exactly one bouquet.

Return the minimum number of days you need to wait to be able to make m bouquets from the garden. If it is impossible to make m bouquets return -1.



Example 1:

Input: bloomDay = [1,10,3,10,2], m = 3, k = 1
Output: 3
Explanation: Let us see what happened in the first three days. x means flower bloomed and _ means flower did not bloom in the garden.
We need 3 bouquets each should contain 1 flower.
After day 1: [x, _, _, _, _]   // we can only make one bouquet.
After day 2: [x, _, _, _, x]   // we can only make two bouquets.
After day 3: [x, _, x, _, x]   // we can make 3 bouquets. The answer is 3.

 */

public class MinNumberOfDaysToMakeBouquets {
    public static int minDays(int[] bloomDay, int m, int k) {

        if ((long) m * k > bloomDay.length) {
            return -1;
        }

        int max=0;
        int min=Integer.MAX_VALUE;
        for(int i=0; i<bloomDay.length; i++){
            min = Math.min(min, bloomDay[i]);
            max = Math.max(max, bloomDay[i]);
        }

        int l=min;
        int r=max;
        int ans=-1;
        while(l<=r){
            int mid = l + (r-l)/2;

            if(days(bloomDay, k, mid)>=m){
                r = mid-1;
                ans = mid;
            }
            else
                l=mid+1;
        }
        return ans;
    }

    static int days(int[] bd, int k, int d){
        int count=0;
        int tc=0;

        for(int i=0; i<bd.length; i++){
            if(bd[i]<=d){
                tc++;

                if(tc==k){
                    count++;
                    tc=0;
                }
            }else{
                tc=0;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1,10,3,10,2};
        System.out.println("Count: " + minDays(nums, 3, 2));
    }
}

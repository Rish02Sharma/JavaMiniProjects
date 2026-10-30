package org.example.DSA.arrays;

import java.util.Arrays;

public class RotateByKTimes {


    // MEDIUM SOLUTION N*K times

    public static void rotateByKTimes(int[] arr){
        int s=1;
        int e= arr.length-1;
        int prev=arr[0];

        while (s<=e){
            int curr = arr[s];
            arr[s] = prev;
            prev = curr;
            s++;
        }

        arr[0]=prev;
    }



    //// OPTIMISED SOLUTION

    public static void rotate(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private static void reverse(int[] nums, int l, int r) {
        while (l < r)
            swap(nums, l++, r--);
    }

    private static void swap(int[] nums, int l, int r) {
        final int temp = nums[l];
        nums[l] = nums[r];
        nums[r] = temp;
    }

    public static void main(String[] args) {
        int arr[] = new int[]{1, 2, 3, 4, 5, 6};
        int k=2;
        int itr = k % arr.length;

        // O( N * K ) solution
        for(int i=0; i<itr; i++){
            rotateByKTimes(arr);
        }

        // O(N) solution
        rotate(arr, k);

        System.out.print("Rotated Array: " + Arrays.toString(arr));
    }
}

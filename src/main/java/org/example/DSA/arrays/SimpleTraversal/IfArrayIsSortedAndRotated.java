package org.example.DSA.arrays.SimpleTraversal;

public class IfArrayIsSortedAndRotated {
    public static boolean check(int[] nums) {
        int n = nums.length;
        int count = 0;
        for(int i= 0; i<n; i++){
            if(nums[i]>nums[(i+1) % n]){
                count++;
            }
        }
        return count<=1;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3, 4, -2, -1};
        boolean ans = check(arr);
        System.out.print("Ans " + ans);
    }
}

package org.example.DSA.arrays.binarySearch;

public class SearchInRotatedArray {
    public static int search(int[] nums, int target) {
        return binarySearch(nums, 0, nums.length-1, target);
    }

    public static int binarySearch(int[] nums, int left, int right, int target){
        if(left>right)
            return -1;

        int mid = left + (right-left)/2;

        if(target == nums[mid])
            return mid;

        if(nums[left] <= nums[mid]){
            if(nums[left]<=target && target<nums[mid]){
                return binarySearch(nums,left, mid-1, target);
            }else{
                return  binarySearch(nums, mid+1, right, target);
            }
        }else {
            if(nums[mid]<target&& target<=nums[right]){
                return  binarySearch(nums,mid+1, right, target);
            }else{
                return  binarySearch(nums, left, mid-1, target);
            }
        }
    }

    public static void main(String[] args) {
        int[] nums = {3, 4, 5, 6, 1, 2};
        int k = 3;
        System.out.println("Count: " + search(nums, k));
    }
}

package org.example.DSA.arrays.binarySearch;

public class SearchInRotatedWithDuplicateValues {
    public static boolean search(int[] nums, int target) {
        return binarySearch(nums, 0, nums.length-1, target);
    }

    public static boolean binarySearch(int[] nums, int left, int right, int target){
        if(left>right)
            return false;

        int mid = left + (right-left)/2;

        if(target == nums[mid])
            return true;

        if(nums[left]==nums[mid] && nums[mid]== nums[right]){
            return binarySearch(nums, ++left, --right, target);
        }else if(nums[left] <= nums[mid]){
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
        int[] nums = {2,5,6,0,0,1,2};
        int k = 3;
        System.out.println("Count: " + search(nums, k));
    }
}

package org.example.DSA.arrays.binarySearch;

public class FindPeakElement {

    public static int findPeakElement(int[] nums) {
        int l=0;
        int r = nums.length-1;

        while(l<r){
            int mid= l + (r-l)/2;

            if(nums[mid]>nums[mid+1]){
                r=mid;
            }else{
                l=mid+1;
            }
        }

        if(l==r)
            return r;

        return -1;
    }

    public static void main(String[] args) {
        int[] nums = {1,2,1,3,5,6,4};
        int k = 3;
        System.out.println("Count: " + findPeakElement(nums));
    }
}

package org.example.DSA.arrays.binarySearch;

public class SingleNonRepeatingNumber {

    public static int singleNonDuplicate(int[] nums) {
        int l=0, r=nums.length-1;

        while(l<r){
            int mid = l + (r-l)/2;

            if(mid % 2 ==0){
                if(nums[mid] == nums[mid+1]){
                    l=mid+2;
                }else{
                    r = mid;
                }
            }

            if(mid%2 != 0){
                if(nums[mid] == nums[mid-1]){
                    l=mid+1;
                }else{
                    r=mid-1;
                }
            }

        }

        if(l == r){
            return nums[l];
        }


        return -1;
    }

    public static void main(String[] args) {
        int[] nums = {1,1,2,3,3,4,4,8,8};
        int k = 3;
        System.out.println("Non repeating integer: " + singleNonDuplicate(nums));
    }
}

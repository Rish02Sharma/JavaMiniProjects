package org.example.DSA.arrays.twoPointer;

public class SquareSortedArray {
    public static int[] sortedSquares(int[] nums) {
        int n=nums.length;
        int result[]=new int[n];
        int l=0;
        int r=n-1;
        int index=n-1;

        while(l<=r){
            int ls=nums[l]*nums[l];
            int rs=nums[r]*nums[r];
            if(ls>rs){
                result[index]=ls;
                l++;
            }else{
                result[index]=rs;
                r--;
            }
            index--;
        }
        return result;
    }

    public static void main(String[] args){
        int[] arr = new int[]{-4,-1,0,3,10};

        int[] leaderArr = sortedSquares(arr);
        for(int i: leaderArr){
            System.out.print(i + " ");
        }
    }
}

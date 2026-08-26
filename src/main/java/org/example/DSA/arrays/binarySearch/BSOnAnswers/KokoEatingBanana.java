package org.example.DSA.arrays.binarySearch.BSOnAnswers;

public class KokoEatingBanana {
    public static int minEatingSpeed(int[] piles, int h) {
        int max = Integer.MIN_VALUE;
        int r = piles.length;
        for(int i=0; i<r; i++){
            max=Math.max(max,piles[i]);
        }

        int l=1;
        int hi=max;
        while(l<=hi){
            int mid = l + (hi-l)/2;

            if(findAns(piles, mid)<=h)
            {
                hi=mid-1;
            }else{
                l=mid+1;
            }
        }
        return l;
    }

    public static int findAns(int [] piles,int speed){
        int value=0;
        for(int i=0;i<piles.length;i++){
            value += Math.ceil((double) piles[i] / speed);
        }
        return value;
    }

    public static void main(String[] args) {
        int[] nums = {30,11,23,4,20};
        System.out.println("Count: " + minEatingSpeed(nums, 5));
    }
}

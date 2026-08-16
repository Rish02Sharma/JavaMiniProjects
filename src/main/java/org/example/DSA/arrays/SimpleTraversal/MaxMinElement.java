package org.example.DSA.arrays.SimpleTraversal;

public class MaxMinElement {

    public static int max(int[] arr){
        int max = Integer.MIN_VALUE;
        for(int i: arr){
            max = Math.max(i, max);
        }
        return max;
    }

    public static int min(int[] arr){
        int min = Integer.MAX_VALUE;
        for(int i: arr){
            min = Math.min(i, min);
        }
        return min;
    }

    public static int secondLarget(int[] arr){
        int max = Integer.MIN_VALUE;
        int secondMax = Integer.MIN_VALUE+1;

        for(int i: arr){
            if(i>max){
                secondMax = max;
                max=i;
            }else if(i>secondMax){
                secondMax = i;
            }
        }
        return secondMax;
    }


    public static void main(String[] args){
        int[] arr = new int[]{1, 2, 3, 4, 5, 11, 22, 15, 66, 44};
        System.out.println("MAX: " + max(arr));
        System.out.println("MIN: " + min(arr));
        System.out.println("MIN: " + secondLarget(arr));
    }
}

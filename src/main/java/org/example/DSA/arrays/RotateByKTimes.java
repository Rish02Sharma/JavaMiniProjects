package org.example.DSA.arrays;

import java.util.Arrays;

public class RotateByKTimes {


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

    public static void main(String[] args) {
        int arr[] = new int[]{1, 2, 3, 4, 5, 6};
        int k=2;
        int itr = k % arr.length;
        for(int i=0; i<itr; i++){
            rotateByKTimes(arr);
        }

        System.out.print("Rotated Array: " + Arrays.toString(arr));
    }
}

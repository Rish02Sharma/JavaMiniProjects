package org.example.DSA.arrays.SimpleTraversal;

public class LeaderArray {
        public static int[] replaceElements(int[] arr) {
            int len = arr.length;
            int[] out = new int[len];
            out[len-1]=-1;
            int max = arr[len-1];
            for(int i=len-2; i>=0; i--){
                out[i] = max;
                max = Math.max(max, arr[i]);
            }
            return out;
        }

        public static void main(String[] args){
            int[] arr = new int[]{4, 3, 2, 5, 7, 8, 2, 1, 4};

            int[] leaderArr = replaceElements(arr);
            for(int i: leaderArr){
                System.out.print(i + " ");
            }
        }
}

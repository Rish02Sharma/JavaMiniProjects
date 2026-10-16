package org.example.DSA.arrays.binarySearch.BSOnAnswers;

public class HouseRobbery {

        public static int maxAmount(int[] arr){
            int [] sol = new int[arr.length+3];
            int len=arr.length;
            int maxA = 0;
            for(int i=arr.length-1; i>=0; i--){
                sol[i] = arr[i] + Math.max(sol[i+2], sol[i+3]);
                maxA = Math.max(sol[i], maxA);
            }
            return maxA;
        }

        public static void main(String[] args) {
            int[] arr2 = new int[]{2, 7, 9, 3, 1};

            int ans2 = maxAmount(arr2);
            System.out.print("Max " + ans2);
        }
}

package org.example.DSA.arrays.SimpleTraversal;

public class TwoSum {
    public static int[] twoSum(int[] numbers, int target) {
        int i=0, j=numbers.length-1;
        int[] output = new int[2];
        while(i!=j){
            if(numbers[i]+numbers[j]>target){
                j--;
            }else if(numbers[i]+numbers[j]<target){
                i++;
            }else if(numbers[i]+numbers[j]==target){
                output[0]=i+1;
                output[1]=j+1;
                break;
            }
        }
        return output;
    }

    public static void main(String[] args){
        int[] arr = new int[]{4, 3, 2, 5, 7, 8, 2, 1, 4};

        int[] leaderArr = twoSum(arr, 6);
        for(int i: leaderArr){
            System.out.print(i + " ");
        }
    }
}

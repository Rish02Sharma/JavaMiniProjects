package org.example.DSA.arrays.SimpleTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreeSum {

    public static List<List<Integer>> threeSum(int[] arr, int tar){
        List<List<Integer>> out = new ArrayList<>();
        Arrays.sort(arr);

        for(int i=0; i<arr.length; i++){
            if(i>0 && arr[i]==arr[i-1]){
                continue;
            }

            int j=i+1;
            int k=arr.length-1;

            while(j<k){
                int sum = arr[i] + arr[j] + arr[k];

                if(sum>tar){
                    k--;
                }else if(sum<tar){
                    j++;
                }else{
                    out.add(Arrays.asList(arr[i], arr[j], arr[k]));
                    j++;
                    while(arr[j]==arr[j+1] && j<k){
                        j++;
                    }
                }
            }
        }
        return out;
    }

    public static void main(String[] args){
        int[] arr = new int[]{-1,0,1,2,-1,-4};

        List<List<Integer>> ans = threeSum(arr, 0);
        ans.forEach(v -> System.out.print(v + " "));

    }
}

package org.example.DSA.arrays.TwoDArray;

import java.util.ArrayList;
import java.util.List;

public class PrintSpiralMatrix {

    public static List<Integer> spiralOrder(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int top =0, left=0;
        int right= n-1, bottom=m-1;

        List<Integer> ot = new ArrayList<>();

        if(m==0 || n==0) return ot;

        while(top<=bottom && left <= right){
            for(int i=left; i<=right; i++){
                ot.add(matrix[top][i]);
            }
            top++;

            for(int i=top; i<=bottom; i++){
                ot.add(matrix[i][right]);
            }
            right--;

            if(top <= bottom){
                for(int i=right; i>=left; i--){
                    ot.add(matrix[bottom][i]);
                }
                bottom--;
            }

            if(left <= right){
                for(int i=bottom; i>=top; i--){
                    ot.add(matrix[i][left]);
                }
                left++;
            }
        }
        return ot;
    }


    public static void main(String[] args) {
        int[][] arr = new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};

        List<Integer> out = spiralOrder(arr);

        System.out.println(out);
    }
}

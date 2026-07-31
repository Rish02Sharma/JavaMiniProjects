package org.example.DSA.recursion;

public class SumOfFirstXNumbers {

    static int sumOfXNumbers(int x){
        if(x==0) return 0;

        return x + sumOfXNumbers(--x);
    }

    public static void main(String[] args){
        int x = 4;
        System.out.print("Sum: " + sumOfXNumbers(x));
    }
}

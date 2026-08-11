package org.example.DSA.recursion;

public class FibonacciNumber {

    static int fibNum(int n){
        if(n==0) return 0;

        if(n==1) return 1;

        return fibNum(n-1) + fibNum(n-2);
    }

    public static int fibonacci(int n, int[] memo)
    {
        if (memo[n] != 0)
            return memo[n];
        if (n == 1 || n == 2)
            return 1;
        else {
            memo[n] = fibonacci(n - 1, memo)
                    + fibonacci(n - 2, memo);
            return memo[n];
        }
    }

    public static void main(String[] args){
        int n = 10;
        System.out.println("Fib number of " + n +" is " + fibNum(n));

        int[] memo = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            System.out.print(fibonacci(i, memo) + " ");
        }
    }
}

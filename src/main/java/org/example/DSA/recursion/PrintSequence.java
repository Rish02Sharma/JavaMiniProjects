package org.example.DSA.recursion;

public class PrintSequence {

    static void print1ToN(int N, int x){
        if(x >= N) return;

        System.out.println(x);
        print1ToN(N, ++x);
    }

    static void printNTo1(int N, int x){
        if(x >= N) return;

        printNTo1(N, ++x);
        System.out.println(x);
    }

    public static void main(String[] args){
        int N = 10;

        print1ToN(N, 0);
        printNTo1(N, 0);
    }
}

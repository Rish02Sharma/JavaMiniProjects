package org.example.DSA.arrays.twoPointer;

/*
Given an array of meeting time interval objects consisting of start and end
times [[start_1,end_1],[start_2,end_2],...] (start_i < end_i), find the minimum number of rooms
required to schedule all meetings without any conflicts.

Input: intervals = [(0,40),(5,10),(15,20)]

Output: 2
*/


import java.util.Arrays;

public class MeetingRooms2 {

    public static int sortAndCheck(int[][] s){
        int len = s.length;
        int[] st = new int[len];
        int[] et = new int[len];

        for(int i=0; i<len; i++){
            st[i]=s[i][0];
            et[i]=s[i][1];
        }

        Arrays.sort(st);
        Arrays.sort(et);

        int sc=0;
        int ec=0;
        int gc=0;
        int lc=0;
        while(sc<len && ec<len){
            if(st[sc]<=et[ec]){
                lc++;
                sc++;
            }else{
                lc--;
                ec++;
            }

            gc = Math.max(gc, lc);
        }
        return gc;
    }


    public static void main(String[] args) {
//        int[][] schedule = new int[][]{{0,30}, {5, 10}, {2,7}, {15,20}};
        int[][] schedule = new int[][]{{0,30}};
        int rooms = sortAndCheck(schedule);
        System.out.println("Maximum number of rooms required: " + rooms);
    }
}

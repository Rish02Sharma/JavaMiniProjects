package org.example.DSA.strings;

public class MaxConsecutiveRepeatingChar {

    public static void main(String[] args) {
        String s = "GeekkkkkkkkkksForGeeeeeks";
        System.out.println("Max repeating character: "+ findMaxRepeatingChar(s));
    }

    private static char findMaxRepeatingChar(String s) {
        int count=0;
        char ans = s.charAt(0);
        int maxC = 0;

        for(int i=0; i<s.length()-1; i++){

            if(s.charAt(i) == s.charAt(i+1)){
                count++;
            }else{
                count = 0;
                continue;
            }

            if(count>maxC){
                maxC = count;
                ans=s.charAt(i);
            }
        }
        return ans;
    }
}

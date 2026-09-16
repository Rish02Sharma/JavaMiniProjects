package org.example.DSA.linkList;

public class PallindromeLL {

    static class ListNode{
        int val;
        ListNode next;

        public ListNode(int val){
            this.val=val;
            this.next=null;
        }
    }

    public static boolean isPalindrome(ListNode head) {

        ListNode slow = head;
        ListNode fast = head;
        ListNode rev = null;

        while(fast!=null &&fast.next!=null){
            ListNode temp = slow;
            slow=slow.next;
            fast=fast.next.next;
            temp.next= rev;
            rev = temp;
        }
        if(fast!=null){
            slow=slow.next;
        }
        while(slow!=null && rev!=null && slow.val==rev.val){
            slow=slow.next;
            rev=rev.next;
        }
        return slow==null;
    }

    public static void main(String[] args){
        ListNode head = new ListNode(0);
        head.next = new ListNode(1);
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(2);
        head.next.next.next.next = new ListNode(1);
        head.next.next.next.next.next = new ListNode(0);
        System.out.println("isPalindrome LL" + isPalindrome(head));
    }

}

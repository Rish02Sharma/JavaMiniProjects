package org.example.DSA.linkList;

public class AddTwoNumbers {

    public static class ListNode{
        int val;
        ListNode next;

        public ListNode(int val){
            this.val=val;
            this.next=null;
        }
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int carry=0, num=0;
        ListNode l3 = new ListNode(0);
        ListNode temp = l3;

        while(l1!=null && l2!=null){
            int val = l1.val + l2.val;
            if(carry==1){
                val++;
                carry=0;
            }

            if(val>=10){
                val = val%10;
                carry=1;
            }
            temp.next = new ListNode(val);
            temp = temp.next;
            l1 = l1.next;
            l2 = l2.next;
            val=0;
        }

        ListNode temp2=null;
        if(l1!=null)
            temp2 = l1;
        else
        if(l2!=null)
            temp2 = l2;

        while(carry!=0 && temp2!=null){
            int val= temp2.val + 1;
            carry=0;

            if(val>=10){
                val = val%10;
                carry=1;
            }
            temp.next = new ListNode(val);
            temp = temp.next;
            temp2 = temp2.next;
            val=0;
        }

        if(carry==0 && temp2!=null){
            temp.next = temp2;
        }

        if(carry==1){
            temp.next = new ListNode(1);
        }
        return l3.next;
    }

    public static void printDLL(ListNode head){
        System.out.println("Print Linked List");
        ListNode temp = head;
        while(temp!=null){
            System.out.println(temp.val);
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);

        ListNode head1 = new ListNode(5);
        head1.next = new ListNode(4);
        head1.next.next = new ListNode(2);

        ListNode result = addTwoNumbers(head, head1);
        printDLL(result);
    }

}

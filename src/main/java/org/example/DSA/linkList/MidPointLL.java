package org.example.DSA.linkList;

public class MidPointLL {

    static class Node{
        int val;
        Node next;

        public Node(int val){
            this.val=val;
            this.next=null;
        }
    }

    public static Node midPoint(Node head){

        if(head==null || head.next==null) return head;

        Node slow = head;
        Node fast = head;

        while(fast!=null && fast.next!=null){
            slow=slow.next;
            fast=fast.next.next;
        }
        return slow;
    }

    public static void main(String[] args){
        Node head = new Node(0);
        head.next = new Node(1);
        head.next.next = new Node(2);
        head.next.next.next = new Node(3);
        head.next.next.next.next = new Node(4);
        System.out.println("Mid point of LL" + midPoint(head).val);
    }
}

package org.example.DSA.linkList;

public class RotateByK {
    public static class Node{
        int val;
        Node next;
        public Node(int val){
            this.val=val;
            this.next=null;
        }
    }

    public static void printDLL(Node head){
        System.out.println("Print Linked List");
        Node temp = head;
        while(temp!=null){
            System.out.println(temp.val);
            temp = temp.next;
        }
    }

    public static void main(String[] args) {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        head.next.next.next.next.next = new Node(6);
        Node newHead = rotateList(head, 2);
        printDLL(newHead);
    }
    
    public static Node rotateList(Node head, int k){
        int count=k;
        Node right = head;
        Node left = head;
        while(count>0){
            right=right.next;
            count--;
        }

        while(right.next!=null){
            right=right.next;
            left=left.next;
        }

        right.next=head;
        Node ans = left.next;
        left.next=null;
        return ans;
    }
}

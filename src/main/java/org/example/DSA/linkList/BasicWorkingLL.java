package org.example.DSA.linkList;

public class BasicWorkingLL {

    public static class Node{
        int val;
        Node next;

        public Node(int val){
            this.val=val;
            this.next=null;
        }
    }

    public static Node insertAtHead(Node head, int val){
        Node newHead = new Node(val);
        newHead.next = head;
        return newHead;
    }

    public static Node deleteFromHead(Node head) {
        Node tempHead = head.next;
        head.next=null;
        return tempHead;
    }

    public static int lengthOfLL(Node head){
        Node temp = head;
        int count=0;
        while(temp!=null){
            temp=temp.next;
            count++;
        }
        return count;
    }

    public static Node reverseLL(Node head){
        Node temp = head;
        Node prev = null;

        while(temp!=null){
            Node temp1 = temp.next;
            temp.next = prev;
            prev = temp;
            temp=temp1;
        }
        return prev;
    }

    public static void printDLL(Node head){
        System.out.println("Print Linked List");
        Node temp = head;
        while(temp!=null){
            System.out.println(temp.val);
            temp = temp.next;
        }
    }

    public static void main(String[] args){
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        System.out.println("Length of LL: " + lengthOfLL(head));

        Node newHead = insertAtHead(head, 0);
        System.out.println("Length of LL: " + lengthOfLL(newHead));
        Node newHead2 = deleteFromHead(newHead);
        int length = lengthOfLL(newHead2);
        System.out.println("Length of LL: " + length);

        printDLL(reverseLL(newHead2));
    }
}

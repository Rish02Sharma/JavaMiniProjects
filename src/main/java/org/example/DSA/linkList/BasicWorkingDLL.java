package org.example.DSA.linkList;

public class BasicWorkingDLL {

    public static class Node{
        int val;
        Node prev;
        Node next;

        public Node(int val, Node prev){
            this.val = val;
            this.prev = prev;
            this.next = null;
        }
    }

    public static Node insertBeforeHead(int val, Node head){
        Node newHead = new Node(val, null);
        newHead.next = head;
        return newHead;
    }

    public static Node deleteAndReturnNewHead(Node head){
        Node newHead = head.next;
        head.next = null;
        return newHead;
    }

    public static int lengthOfDLL(Node head){
        Node temp = head;
        int len=0;
        while(temp!=null){
            temp = temp.next;
            len++;
        }
        return len;
    }

    public static Node reverseDLL(Node head){
        Node next = head;
        Node prev = null;
        if(head==null || head.next==null) return head;

        while(next!=null){
            Node temp = next.next;
            next.prev = temp;
            next.next = prev;
            prev = next;
            next = temp;
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
        Node head = new Node(0, null);
        head.next = new Node(1, head);
        head.next.next = new Node(2, head.next);
        head.next.next.next = new Node(3, head.next.next);
        System.out.println("Length of DLL: " + lengthOfDLL(head));

        Node newHead = insertBeforeHead(0, head);
        System.out.println("Length of DLL: " + lengthOfDLL(newHead));
        Node newHead2 = deleteAndReturnNewHead(newHead);
        int length = lengthOfDLL(newHead2);
        System.out.println("Length of DLL: " + length);

        printDLL(head);

        Node revHead = reverseDLL(head);

        printDLL(revHead);

    }
}

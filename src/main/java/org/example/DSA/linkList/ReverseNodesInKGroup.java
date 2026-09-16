package org.example.DSA.linkList;

public class ReverseNodesInKGroup {
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
        Node newHead = reverseKGroup(head, 2);
        printDLL(newHead);
    }

    public static Node reverseKGroup(Node head, int k) {
        Node dummy = new Node(0);
        dummy.next = head;
        Node tail = dummy;
        Node temp = head;
        Node start = head;
        Node prev = null;
        Node curr = head;
        int count = 0;
        if(head == null || head.next == null){
            return head;
        }
        while(temp != null){
            count++;
            temp = temp.next;
            if(count == k){
                for(int i = 0; i < k; i++){
                    Node next = curr.next;
                    curr.next = prev;
                    prev = curr;
                    curr = next;
                }
                tail.next = prev;
                start.next = curr;
                tail = start;
                start = curr;
                prev = null;
                count = 0;
            }
        }
        return dummy.next;
    }
}

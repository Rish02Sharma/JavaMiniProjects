package org.example.DSA.linkList;

public class IntersectionOfLL {
    
    public static class Node{
        int val;
        Node next;
        public Node(int val){
            this.val=val;
            this.next=null;
        }
    }
    
    public static void main(String[] args){
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        head.next.next.next = new Node(4);
        head.next.next.next.next = new Node(5);
        head.next.next.next.next.next = new Node(6);

        Node head1 = new Node(10);
        head1.next = new Node(11);
        head1.next.next = new Node(12);
        head1.next.next.next = head.next.next.next;

        Node intersection = detectIntersectionPoint(head, head1);
        System.out.println("Interserction point of LL: " + intersection.val);
    }

    private static Node detectIntersectionPoint(Node head, Node head1) {
        Node temp1 = head;
        Node temp2 = head1;
        int len1=0, len2=0;

        while(temp1!=null){
            temp1=temp1.next;
            len1++;
        }

        while(temp2!=null){
            temp2=temp2.next;
            len2++;
        }

        temp1 = head;
        temp2 = head1;

        while(len1!=len2){
            if(len1>len2){
                temp1 = temp1.next;
                len1--;
            }else{
                temp2 = temp2.next;
                len2--;
            }
        }

        while(temp1!=null && temp2!=null){
            if(temp1 == temp2){
                return temp2;
            }else{
                temp1=temp1.next;
                temp2=temp2.next;
            }
        }
        return null;
    }
}

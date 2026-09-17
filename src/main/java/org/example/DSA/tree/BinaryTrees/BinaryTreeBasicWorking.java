package org.example.DSA.tree.BinaryTrees;

import java.util.LinkedList;
import java.util.Queue;

public class BinaryTreeBasicWorking {

    public static class Node{
        int val;
        Node left;
        Node right;

        public Node(int val){
            this.val=val;
            this.left=null;
            this.right=null;
        }
    }

    public static void printPreorder(Node root){
        if(root==null) return;

        System.out.print(root.val + " ");
        printPreorder(root.left);
        printPreorder(root.right);
    }

    public static void printPostorder(Node root){
        if(root==null) return;

        printPostorder(root.left);
        printPostorder(root.right);
        System.out.print(root.val + " ");
    }

    public static void printInorder(Node root){
        if(root==null) return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void printLevelOrder(Node root){
        Queue<Node> q = new LinkedList<>();
        q.add(root);

        while(!q.isEmpty()){
            Node temp = q.poll();
            System.out.print(temp.val + " ");

            if(temp.left!=null){
                q.add(temp.left);
            }

            if(temp.right!=null){
                q.add(temp.right);
            }
        }
    }

    public static void main(String[] args) {
        Node root = new Node(4);
        root.left = new Node(2);
        root.right = new Node(5);
        root.right.left = new Node(7);
        root.right.right = new Node(6);
        root.right.right.left = new Node(8);

        root.left.left = new Node(3);
        root.left.left.right = new Node(9);
        root.left.left.right.left = new Node(1);

        System.out.println("printPreorder");
        printPreorder(root);
        System.out.println("printPostorder");
        printPostorder(root);
        System.out.println("printInorder");
        printInorder(root);
        System.out.println("Level order traversal");
        printLevelOrder(root);

    }
}

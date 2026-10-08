package org.example.DSA.tree.BinaryTrees;

import java.util.ArrayList;
import java.util.List;

public class BoundaryTraversal {

    static class Node {
        int data;
        Node left, right;

        Node(int x) {
            data = x;
            left = right = null;
        }
    }

    static boolean isLeaf(Node node) {
        return node.left == null && node.right == null;
    }

    // Function to collect left boundary nodes
    // (top-down order)
    static void collectLeft(Node root, ArrayList<Integer> res) {

        // exclude leaf node
        if (root == null || isLeaf(root))
            return;

        res.add(root.data);
        if (root.left != null)
            collectLeft(root.left, res);

        else if (root.right != null)
            collectLeft(root.right, res);
    }

    // Function to collect all leaf nodes
    static void collectLeaves(Node root, ArrayList<Integer> res) {
        if (root == null)
            return;

        // Add leaf nodes
        if (isLeaf(root)) {
            res.add(root.data);
            return;
        }

        collectLeaves(root.left, res);
        collectLeaves(root.right, res);
    }

    // Function to collect right boundary nodes
    // (bottom-up order)
    static void collectRight(Node root, ArrayList<Integer> res) {

        // exclude leaf nodes
        if (root == null || isLeaf(root))
            return;

        if (root.right != null)
            collectRight(root.right, res);

        else if (root.left != null)
            collectRight(root.left, res);

        res.add(root.data);
    }

    // Function to find Boundary Traversal of Binary Tree
    static ArrayList<Integer> boundaryTraversal(Node root) {
        ArrayList<Integer> res = new ArrayList<>();

        if (root == null)
            return res;

        // Add root data if it's not a leaf
        if (!isLeaf(root))
            res.add(root.data);

        // Collect left boundary
        collectLeft(root.left, res);

        // Collect leaf nodes
        collectLeaves(root, res);

        // Collect right boundary
        collectRight(root.right, res);

        return res;
    }

    public static void main(String[] args) {

        // Input Binary tree
        //            1
        //         /     \
        //       2         3
        //     /   \     /   \
        //    4     5   6     7
        //         / \
        //        8   9

        Node root = new Node(1);
        root.left = new Node(2);
        root.right = new Node(3);

        root.left.left = new Node(4);
        root.left.right = new Node(5);

        root.right.left = new Node(6);
        root.right.right = new Node(7);

        root.left.right.left = new Node(8);
        root.left.right.right = new Node(9);

        List<Integer> boundary = boundaryTraversal(root);

        for (int x : boundary)
            System.out.print(x + " ");
    }
}

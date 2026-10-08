package org.example.DSA.tree.BinaryTrees;

public class BalancedBinaryTree {

    static class TreeNode{
        int value;
        TreeNode left;
        TreeNode right;
        
        public TreeNode(int v){
            this.value = v;
            this.left = null;
            this.right = null;
        }
    }
    
    static int isBalanced(TreeNode node, int h){
        if(node==null){
            return h;
        }

        int lh = isBalanced(node.left, h+1);
        int rh = isBalanced(node.right, h+1);

        if(Math.abs(lh-rh)>1) return -1;

        return Math.max(lh, rh);
    }
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(6);
        root.right.right.left = new TreeNode(8);

        root.left.left = new TreeNode(3);
        root.left.left.right = new TreeNode(9);
        root.left.left.right.left = new TreeNode(1);

        boolean ans = isBalanced(root, 0) != -1;
        System.out.println("Balanced boolean: " + ans);

    }
}

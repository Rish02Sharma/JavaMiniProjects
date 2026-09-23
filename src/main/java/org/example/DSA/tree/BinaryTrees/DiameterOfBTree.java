package org.example.DSA.tree.BinaryTrees;

public class DiameterOfBTree {
    static int mh;

    public static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
        
        public TreeNode(int val){
            this.val = val;
            this.left = null;
            this.right = null;
        }
    }

    public static int diameterMax(TreeNode n, int h){
        if(n==null){
            return 0;
        }

        int lh = diameterMax(n.left, h+1);
        int rh = diameterMax(n.right, h+1);

        mh = Math.max(mh, lh+rh);
        return Math.max(lh, rh)+1;
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

        int lh = diameterMax(root.left, 0);
        int rh = diameterMax(root.right, 0);
        System.out.println("Max diameter = " + Math.max(mh, lh+rh));
    }
}

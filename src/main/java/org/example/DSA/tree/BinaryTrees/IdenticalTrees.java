package org.example.DSA.tree.BinaryTrees;

public class IdenticalTrees {

    public static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
        
        public TreeNode(int val){
            this.val=val;
            this.left=null;
            this.right=null;
        }
    }

    public static boolean checkIdentical(TreeNode l, TreeNode r){
        if(l==null && r==null){
            return true;
        }else if (l==null && r!=null){
            return false;
        } else if (l != null && r == null) {
            return false;
        }

        if(l.val != r.val){
            return false;
        }

        boolean lt = checkIdentical(l.left, r.left);
        boolean rt = checkIdentical(l.right, r.right);

        if(lt == true && rt == true){
            return true;
        }else {
            return false;
        }

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

        TreeNode root2 = new TreeNode(4);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(5);
        root2.right.left = new TreeNode(7);
        root2.right.right = new TreeNode(6);
        root2.right.right.left = new TreeNode(8);

        root2.left.left = new TreeNode(3);
        root2.left.left.right = new TreeNode(9);
        root2.left.left.right.left = new TreeNode(1);

        boolean ans = checkIdentical(root, root2);
        System.out.println("Identical tree: " + ans);
    }
}

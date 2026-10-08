package org.example.DSA.tree.BinaryTrees;

public class SymmetricTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    //        4
    //    2        2
    //  3    7   7   3
    //     9       9

    public static boolean isMirrorTree(TreeNode l, TreeNode r){
        if(l==null && r==null){
            return true;
        }else if (l==null && r!=null){
            return false;
        } else if (l != null && r == null) {
            return false;
        }

        boolean lt = isMirrorTree(l.left, r.right);
        boolean rt = isMirrorTree(l.right, r.left);

        if(lt == rt && lt==true){
            return true;
        }else{
            return false;
        }

    }


    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(2);
        root.right.left = new TreeNode(7);
        root.right.left.right = new TreeNode(9);
        root.right.right = new TreeNode(3);

//        4
//    2          2
//  3    7   7      3
//      9      9

                
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.left.right.left = new TreeNode(9);

        System.out.println("Are trees mirror image? " + isMirrorTree(root.left, root.right));
    }
}

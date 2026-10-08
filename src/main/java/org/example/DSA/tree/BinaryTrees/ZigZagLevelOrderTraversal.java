package org.example.DSA.tree.BinaryTrees;

import java.util.*;

public class ZigZagLevelOrderTraversal {

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
    
    public static List<List<Integer>> zigZagLevelOrder(TreeNode root){
        if(root==null)
            return Collections.emptyList();

        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        boolean flag=true;
        List<List<Integer>> result = new ArrayList<>();
        while(!q.isEmpty()){
            List<Integer> list = new ArrayList<>();
            int size = q.size();
            for(int i=0; i<size; i++){
                TreeNode n = q.poll();
                list.add(n.val);
                if(n.left!=null) q.add(n.left);

                if(n.right!=null) q.add(n.right);
            }

            if(flag){
                result.add(list);
            }else{
                Collections.reverse(list);
                result.add(list);
            }

            flag = !flag;
        }

        return result;
    }
    
    
    public static void main(String[] args) {
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.right.left = new TreeNode(7);
        root.right.left.right = new TreeNode(9);
        root.right.right = new TreeNode(3);

//        4
//    2          3
//  3    7   7      3
//      9      10

        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.left.right.left = new TreeNode(10);

        List<List<Integer>> result = zigZagLevelOrder(root);
        System.out.print("[");
        for(List<Integer> list: result){
            System.out.print("[");
            for(Integer num: list){
                System.out.print(num + ", ");
            }
            System.out.print("]");
        }
        System.out.print("]");
    }
}

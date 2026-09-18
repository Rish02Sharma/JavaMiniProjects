package org.example.DSA.tree.BinaryTrees;

import java.util.LinkedList;
import java.util.Queue;

public class MaxDepthOfTree {


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

  public static int maxDepth(TreeNode root){
      if(root==null) return 0;

      Queue<TreeNode> q = new LinkedList<>();
      q.add(root);
      int height=0;

      while(!q.isEmpty()){
          int size = q.size();

          for(int i=0; i<size; i++){
              TreeNode curr = q.poll();

              if(curr.left!=null) q.add(curr.left);

              if(curr.right!=null) q.add(curr.right);
          }
          height++;
      }
      return height;
  }

    public static void main(String[] args) {

//                4
//           2         5
//        3          7      6
//            9          8
//        1
//
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(6);
        root.right.right.left = new TreeNode(8);

        root.left.left = new TreeNode(3);
        root.left.left.right = new TreeNode(9);
        root.left.left.right.left = new TreeNode(1);

        int height = maxDepth(root);
        System.out.println("Height of tree is: " + height);
    }
}

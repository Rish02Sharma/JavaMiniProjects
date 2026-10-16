package org.example.DSA.arrays.binarySearch;

public class SingleElementInSortedArray {

        public static int findLoneSignal(int[] towerIds) {
            int low = 0;
            int high = towerIds.length - 1;

            while (low < high) {
                int mid = low + (high - low) / 2;

                // make mid even so it points to the start of a potential pair
                if (mid % 2 == 1) {
                    mid--;
                }

                if (towerIds[mid] == towerIds[mid + 1]) {
                    // pair is intact -> lone element lies to the right
                    low = mid + 2;
                } else {
                    // pair is broken -> lone element is at mid or to the left
                    high = mid;
                }
            }
            return towerIds[low];
        }

        public static void main(String[] args) {
            int[] towerIds = {1, 1, 2, 2, 3, 4, 4, 5, 5};
            System.out.println(findLoneSignal(towerIds)); // 3
        }
}

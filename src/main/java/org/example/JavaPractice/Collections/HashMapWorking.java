package org.example.JavaPractice.Collections;


//HashMap Internal Mechanics: Stores key-value pairs in an array of Node<K, V> buckets. hash(key.hashCode())
//computes an index via (n - 1) & hash. Collisions form a linked list. In Java 8+, once a bucket reaches TREEIFY_THRESHOLD = 8
//and total capacity $\ge 64$, the list converts to a Red-Black Tree (TreeNode), improving worst-case search from $O(n)$ to
//$O(\log n)$. Rehash doubles capacity when entries exceed capacity * loadFactor (default $0.75$).

public class HashMapWorking {

    public class SimpleHashMap<K, V> {

        // 1. Core Internal Data Structure: The Node (Bucket)
        static class Node<K, V> {
            final int hash;
            final K key;
            V value;
            Node<K, V> next; // Linked list pointer to handle collisions

            Node(int hash, K key, V value, Node<K, V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        // Default configuration matching standard Java behavior
        private static final int DEFAULT_INITIAL_CAPACITY = 16;
        private static final float DEFAULT_LOAD_FACTOR = 0.75f;

        private Node<K, V>[] table; // The internal array of buckets
        private int size;
        private int threshold;

        @SuppressWarnings("unchecked")
        public SimpleHashMap() {
            this.table = new Node[DEFAULT_INITIAL_CAPACITY];
            this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        }

        // 2. Internal Hashing Mechanics
        // Java 8+ uses this bit-mixing spreading function to prevent poor hash distributions
        private int hash(Object key) {
            if (key == null) return 0;
            int h = key.hashCode();
            return h ^ (h >>> 16);
        }

        // 3. The put() Operation
        public void put(K key, V value) {
            int hash = hash(key);
            // Index calculation: (n - 1) & hash
            int index = (table.length - 1) & hash;

            Node<K, V> head = table[index];

            // Case A: The bucket is empty, create the first node
            if (head == null) {
                table[index] = new Node<>(hash, key, value, null);
                size++;
            } else {
                // Case B: Collision! Traverse the linked list in the bucket
                Node<K, V> current = head;
                while (true) {
                    // Check if the key already exists using hash equality AND equals()
                    if (current.hash == hash &&
                            (current.key == key || (key != null && key.equals(current.key)))) {
                        current.value = value; // Update value if key matches
                        return;
                    }
                    if (current.next == null) break;
                    current.next = current.next;
                }
                // Key wasn't found in the list; append new node to the chain
                current.next = new Node<>(hash, key, value, null);
                size++;
            }

            // 4. Dynamic Resizing Check
            if (size > threshold) {
                resize();
            }
        }

        // 5. The get() Operation
        public V get(K key) {
            int hash = hash(key);
            int index = (table.length - 1) & hash;

            Node<K, V> current = table[index];

            // Traverse the bucket chain to find the key
            while (current != null) {
                if (current.hash == hash &&
                        (current.key == key || (key != null && key.equals(current.key)))) {
                    return current.value;
                }
                current = current.next;
            }
            return null; // Key not found
        }

        // 6. Resizing and Rehashing
        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] oldTable = table;
            int oldCapacity = oldTable.length;
            int newCapacity = oldCapacity * 2; // Capacity doubles

            Node<K, V>[] newTable = new Node[newCapacity];
            threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);

            // Redistribute all existing elements into the new larger array
            for (int i = 0; i < oldCapacity; i++) {
                Node<K, V> e = oldTable[i];
                while (e != null) {
                    Node<K, V> next = e.next; // Save reference to next node

                    // Re-calculate the index based on the new array capacity
                    int newIndex = (newCapacity - 1) & e.hash;

                    // Insert node at the head of the new bucket's list
                    e.next = newTable[newIndex];
                    newTable[newIndex] = e;

                    e = next;
                }
            }
            table = newTable;
        }

        public int size() {
            return this.size;
        }
    }

}

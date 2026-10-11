package org.example.JavaPractice.Collections;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class ConcurrentHashMap {
    public class SimpleConcurrentHashMap<K, V> {

        // 1. The Core Node Structure
        // Value and Next pointer MUST be volatile to ensure cross-thread visibility
        static class Node<K, V> {
            final int hash;
            final K key;
            volatile V value;
            volatile Node<K, V> next;

            Node(int hash, K key, V value, Node<K, V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        // Volatile table array reference ensures that resizing shifts are instantly visible
        private transient volatile Node<K, V>[] table;
        private static final int DEFAULT_CAPACITY = 16;

        // Atomic VarHandle infrastructure to safely execute CAS operations on array elements
        private static final VarHandle QA;
        static {
            QA = MethodHandles.arrayElementVarHandle(Node[].class);
        }

        @SuppressWarnings("unchecked")
        public SimpleConcurrentHashMap() {
            this.table = new Node[DEFAULT_CAPACITY];
        }

        private int spread(int h) {
            return (h ^ (h >>> 16)) & 0x7fffffff; // Ensures positive hash values
        }

        // Atomic Helper Methods matching ConcurrentHashMap source patterns
        @SuppressWarnings("unchecked")
        private Node<K, V> tabAt(Node<K, V>[] tab, int i) {
            return (Node<K, V>) QA.getVolatile(tab, i); // Volatile read of a bucket
        }

        private boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
            return QA.compareAndSet(tab, i, c, v); // Atomic Compare-And-Swap write
        }

        /**
         * Concurrent Put Operation
         */
        public V put(K key, V value) {
            // Rule: ConcurrentHashMap strictly forbids null keys and values
            if (key == null || value == null) throw new NullPointerException();

            int hash = spread(key.hashCode());
            Node<K, V>[] tab = table;

            while (true) { // Infinite loop to retry if a CAS operation fails
                int n = tab.length;
                int i = (n - 1) & hash;
                Node<K, V> f = tabAt(tab, i); // Fetch the bucket head visually

                // MECHANISM 1: If bucket is empty, try to place it lock-free using CAS
                if (f == null) {
                    if (casTabAt(tab, i, null, new Node<>(hash, key, value, null))) {
                        break; // Node successfully added without taking a single lock!
                    }
                    // If CAS fails, another thread inserted a node first. The loop retries.
                }
                // MECHANISM 2: Bucket is occupied. We must synchronize on the bucket head.
                else {
                    V oldVal = null;
                    synchronized (f) { // Lock ONLY this specific bucket head
                        if (tabAt(tab, i) == f) { // Double check to ensure layout hasn't shifted
                            Node<K, V> e = f;
                            while (true) {
                                if (e.hash == hash &&
                                        (e.key == key || (key.equals(e.key)))) {
                                    oldVal = e.value;
                                    e.value = value; // Key exists, update value
                                    break;
                                }
                                Node<K, V> pred = e;
                                if ((e = e.next) == null) {
                                    // Reached the end of the chain, append the new node
                                    pred.next = new Node<>(hash, key, value, null);
                                    break;
                                }
                            }
                        }
                    }
                    if (oldVal != null || f != null) {
                        return oldVal;
                    }
                }
            }
            return null;
        }

        /**
         * Lock-Free Concurrent Get Operation
         */
        public V get(K key) {
            Node<K, V>[] tab = table;
            int n = tab.length;
            int hash = spread(key.hashCode());
            int i = (n - 1) & hash;

            Node<K, V> e = tabAt(tab, i); // Volatile read

            // Traverse the bucket chain. Because 'next' and 'value' are volatile,
            // reads are 100% thread-safe and never require any blocking/locking.
            while (e != null) {
                if (e.hash == hash && (e.key == key || key.equals(e.key))) {
                    return e.value;
                }
                e = e.next;
            }
            return null;
        }
    }
}

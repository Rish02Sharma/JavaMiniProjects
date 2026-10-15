package org.example.DSA.linkList;

import java.util.HashMap;

class LRUCache {

    class DLL{
        DLL prev, next;
        int val, key;

        DLL(int key, int val){
            this.key=key;
            this.val=val;
        }
    }

    HashMap<Integer, DLL> map;
    int cap;
    DLL head;
    DLL tail;

    public LRUCache(int capacity) {
        this.map = new HashMap<>();
        this.head = new DLL(-1, -1);
        this.tail = new DLL(-1, -1);
        head.next=tail;
        tail.prev=head;
        this.cap = capacity;
    }

    public int get(int key) {
        int ans;
        if(map.containsKey(key)){
            DLL n = map.get(key);
            ans=n.val;
            deleteNode(n);
            insertAtTop(n);
        }else{
            ans=-1;
        }

        return ans;
    }

    public void put(int key, int value) {
        if(map.containsKey(key)){
            DLL n = map.get(key);
            n.val=value;
            deleteNode(n);
            insertAtTop(n);
            map.put(key, n);
        }else{
            if(this.cap==map.size()){
                map.remove(tail.prev.key);
                deleteNode(tail.prev);
            }

            DLL nn = new DLL(key, value);
            insertAtTop(nn);
            map.put(key, nn);
        }
    }

    private void deleteNode(DLL n){
        DLL prevNode = n.prev;
        prevNode.next = n.next;
        n.next.prev = prevNode;
    }

    private void insertAtTop(DLL n){
        DLL headnext = head.next;
        head.next=n;
        n.prev=head;
        headnext.prev=n;
        n.next=headnext;
    }
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
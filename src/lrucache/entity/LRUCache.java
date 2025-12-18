package lrucache.entity;

import java.util.HashMap;
import java.util.Map;


//所有修改链表的操作都需要在同一个锁的保护下
//链表结构修改必须与map操作在同一个临界区内
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.pre = head;
    }

    public synchronized V get(K key) {

        Node<K, V> node = map.get(key);
        if (node != null) {
            moveToFront(node);
            return node.getValue();
        }
        return null;

    }

    public synchronized V remove(K key) {
        Node<K, V> removed = map.remove(key);
        if (removed != null) {
            remove(removed);
            return removed.getValue();
        }

        return null;
    }

    //复用已有节点，返回的是旧值
    public synchronized V put(K key, V value) {
        Node<K, V> existedNode = map.get(key);

        if (existedNode != null) {
            V old = existedNode.getValue();
            existedNode.setValue(value);
            moveToFront(existedNode);
            return old;
        }


        if (map.size() >= capacity) {
            //remove least recently used
            Node<K, V> removed = removeLast();
            map.remove(removed.getKey());
        }

        Node<K, V> node = new Node<>(key, value);
        map.put(key, node);
        addFirst(node);

        return null;
    }

    private void addFirst(Node<K, V> node) {
        Node<K, V> first = head.next;
        head.next = node;
        node.pre = head;
        node.next = first;
        first.pre = node;

    }

    private void moveToFront(Node<K, V> node) {
        remove(node);
        addFirst(node);
    }

    private void remove(Node<K, V> node) {
        Node<K, V> pre = node.pre;
        Node<K, V> next = node.next;
        pre.next = next;
        next.pre = pre;
        //clear pointers
        node.pre = null;
        node.next = null;
    }

    private Node<K, V> removeLast() {
        Node<K, V> last = tail.pre;
        last.pre.next = tail;
        tail.pre = last.pre;
        last.pre = null;
        last.next = null;
        return last;
    }
}

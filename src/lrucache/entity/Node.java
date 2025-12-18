package lrucache.entity;

public class Node<K, V> {
    private final K key;
    //读写方法的调用是受synchronized保护的，可以不加volatile
    private V value;
    Node<K, V> pre;
    Node<K, V> next;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.pre = null;
        this.next = null;
    }

    Node() {
        this.key = null;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }


    public void setValue(V value) {
        this.value = value;
    }
}

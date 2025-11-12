package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * LRUCache is a fixed-capacity cache implementing the Least Recently Used eviction policy.
 * It supports thread-safe get and put operations, and offers an optional TTL for entries.
 */
public class LRUCacheImplementation<K, V> {
    /**
     * Node is an entry in the doubly-linked list used to track LRU order.
     * It holds a key, value, and pointers to previous and next nodes in the list.
     * It also optionally holds an expiry time (epoch millis) for TTL support.
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        long expiryTime; // timestamp in millis when this entry expires (0 or negative if no TTL)

        Node(K key, V value, long expiryTime) {
            this.key = key;
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map; // HashMap for O(1) key-node lookup
    private final Node<K, V> head; // Dummy head of doubly linked list (most recent items near head)
    private final Node<K, V> tail; // Dummy tail of doubly linked list (least recent items near tail)
    private final long ttlMillis;

    /**
     * Constructs an LRUCache with the specified capacity.
     */
    public LRUCacheImplementation(int capacity, long ttlMillis) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        this.ttlMillis = ttlMillis;
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
// Initialize dummy head and tail nodes to avoid null checks for end operations
        head = new Node<>(null, null, 0);
        tail = new Node<>(null, null, 0);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Retrieves the value associated with the given key in the cache.
     * If the key exists, the entry is marked as recently used (moved to front).
     * If the key does not exist or has expired, returns null.
     * This method is thread-safe.
     */

    public synchronized V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null; // cache miss
        }
// If TTL is set and entry is expired, remove it and return null
        if (node.expiryTime > 0 && System.currentTimeMillis() > node.expiryTime) {
            removeNode(node);
            map.remove(key);
            return null;
        }
        else {
// Move the accessed node to the head (most recently used position)
            moveToHead(node);
            return node.value;
        }
    }

    /**
     * Inserts or updates a cache entry with an associated time-to-live (TTL).
     * The entry will expire after TTL milliseconds from now.
     * If TTL <= 0, the entry will not expire (treated as no TTL).
     */
    public synchronized void put(K key, V value) {
        Node<K, V> node = map.get(key);
        long expiry = (ttlMillis > 0) ? (System.currentTimeMillis() + ttlMillis) : 0;
        if (node != null) {
// Update existing node's value and expiry, then move to head
            node.value = value;
            node.expiryTime = expiry;
            moveToHead(node);
        } else {
// Create new node
            Node<K, V> newNode = new Node<>(key, value, expiry);
// Add to HashMap and linked list
            map.put(key, newNode);
            addToHead(newNode);
// If over capacity, evict LRU node (tail.prev)
            if (map.size() > capacity) {
                Node<K, V> lru = tail.prev;
                if (lru != null && lru != head) {
// Remove LRU node
                    removeNode(lru);
                    map.remove(lru.key);
                }
            }
        }
// Note: If needed, could also check and remove any other expired entries here (not just LRU).
    }

    /**
     * Removes a given node from the doubly-linked list, adjusting pointers.
     * (Helper method – assumes node is in the list.)
     */
    private void removeNode(Node<K, V> node) {
        Node<K, V> prevNode = node.prev;
        Node<K, V> nextNode = node.next;
        if (prevNode != null) prevNode.next = nextNode;
        if (nextNode != null) nextNode.prev = prevNode;
    }

    /**
     * Adds a given node right after the dummy head of the doubly-linked list.
     * Marks it as most recently used.
     * (Helper method – used when inserting new nodes or moving existing nodes to head.)
     */
    private void addToHead(Node<K, V> node) {
        Node<K, V> firstRealNode = head.next;
        node.expiryTime = System.currentTimeMillis() + ttlMillis;
        head.next = node;
        node.prev = head;
        node.next = firstRealNode;
        firstRealNode.prev = node;
    }

    /**
     * Moves an existing node to the head of the list (MRU position).
     * This involves removing the node from its current position and then adding it to head.
     */
    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * Removes a specific key from the cache if present.
     * Also removes it from the linked list. This can be used for manual cache invalidation.
     * true if the key was found and removed, false if not present.
     */
    public synchronized boolean remove(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return false;
        }
        removeNode(node);
        map.remove(key);
        return true;
    }

    /**
     * Returns the current number of entries in the cache.
     * Useful for testing or monitoring.
     */
    public synchronized int getCacheSize() {
        return map.size();
    }
}

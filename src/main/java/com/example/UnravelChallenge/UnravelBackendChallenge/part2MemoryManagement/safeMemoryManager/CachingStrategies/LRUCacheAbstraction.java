package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies;

// Need to improve ttl implementation

public class LRUCacheAbstraction<K,V> implements CachingStrategy<K,V> {

    private final LRUCacheImplementation<K,V> cache;

    public LRUCacheAbstraction(int maxSize, int givenTTLMillis) {
        cache = new LRUCacheImplementation<>(maxSize, givenTTLMillis);
    }

    @Override
    public long getCacheSize() {
        return cache.getCacheSize();
    }

    @Override
    public void put(K key, V value) {
        try {
            cache.put(key, value);
        }
        catch (Exception e) {
            System.out.println("Error received while adding " + key);
        }
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public boolean isPresent(K key) {
        return cache.get(key) != null;
    }
}
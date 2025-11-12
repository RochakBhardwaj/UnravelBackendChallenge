package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapCache<K, V> implements CachingStrategy<K, V> {

    private final Map<K, V> cache = new ConcurrentHashMap<>();

    @Override
    public void put(K sessionId, V data) {
        cache.put(sessionId, data);
    }

    @Override
    public boolean isPresent(K sessionId) {
        return cache.containsKey(sessionId);
    }

    @Override
    public void remove(K sessionId) {
        cache.remove(sessionId);
    }

    @Override
    public long getCacheSize() {
        return cache.size();
    }
}

package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public interface CachingStrategy<K,V> {
    void put(K sessionId, V data);
    public boolean isPresent(K sessionId);
    void remove(K sessionId);
    long getCacheSize();
}

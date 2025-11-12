package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineCache<K,V> implements CachingStrategy<K,V> {

    protected final Cache<K,V> largeSessionData;

    public CaffeineCache(int maxSize, int ttlMilliSeconds) {
        this.largeSessionData = Caffeine.newBuilder()
                .maximumSize(maxSize)  // Max Capacity
                .expireAfterAccess(ttlMilliSeconds, TimeUnit.MILLISECONDS)  // Currenly it is milliseconds, for actual deployment we can switch to hours
                .build();
    }

    public void put(K sessionId, V data) {
        largeSessionData.put(sessionId, data);
    }

    public boolean isPresent(K sessionId) {
        return largeSessionData.asMap().containsKey(sessionId);
    }

    public void remove(K sessionId) {
        largeSessionData.invalidate(sessionId);
    }

    public long getCacheSize() {
        return largeSessionData.estimatedSize();
    }

}

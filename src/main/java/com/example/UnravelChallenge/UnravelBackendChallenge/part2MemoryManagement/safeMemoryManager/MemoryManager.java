package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager;
import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies.CachingStrategy;
import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies.CaffeineCache;

public class MemoryManager {

    protected static CachingStrategy<String, byte[]> largeSessionData;
    private static MemoryManager instance;

    private MemoryManager(CachingStrategy<String,byte[]> cachingStrategy) {
        largeSessionData = cachingStrategy;
    }

//  During first initialisation, we need to set a CachingStrategy

    public static MemoryManager initialiseInstance(CachingStrategy<String, byte[]> cachingStrategy) {
        if (instance == null) {
            synchronized (MemoryManager.class) {
                if (instance == null) {
                    instance = new MemoryManager(cachingStrategy);
                }
                else{
                    throw new IllegalStateException("MemoryManager already initialized");
                }
            }
        }
        else{
            throw new IllegalStateException("MemoryManager already initialized");
        }
        return instance;
    }

    public static MemoryManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MemoryManager not initialized yet. Call getInstance(cachingStrategy) first.");
        }
        return instance;
    }

    public void addSessionData(String sessionId) {
        largeSessionData.put(sessionId, new byte[10*1024*1024]); // 10MB per session
    }
    public void removeSessionData(String sessionId) {
        largeSessionData.remove(sessionId);
    }
    public boolean isPresent(String sessionId) {
        return largeSessionData.isPresent(sessionId);
    }
    public long getCacheSize() {
        return largeSessionData.getCacheSize();
    }

}

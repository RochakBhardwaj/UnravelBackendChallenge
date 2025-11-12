package com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManager;

import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies.CaffeineCache;
import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies.HashMapCache;
import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.CachingStrategies.LRUCacheAbstraction;
import com.example.UnravelChallenge.UnravelBackendChallenge.part2MemoryManagement.safeMemoryManager.MemoryManager;
import org.junit.jupiter.api.Test;

public class MemoryManagementTest {

    int capacity = 100;
    int ttl = 100;

    MemoryManager memoryManager = MemoryManager.initialiseInstance(new CaffeineCache<String, byte[]>(capacity,ttl));
//    MemoryManager memoryManager = MemoryManager.initialiseInstance(new LRUCacheAbstraction<String, byte[]>(capacity,ttl));
//    MemoryManager memoryManager = MemoryManager.initialiseInstance(new HashMapCache<String, byte[]>());

    @Test
    public void testBasicMemoryManagement() throws InterruptedException {

        for(int i = 0; i < 10000; i++){
            Thread.sleep(10);
            String sessionId = "session" + i;
            memoryManager.addSessionData(sessionId);
        }

        System.out.println(memoryManager.getCacheSize());

//        If the code runs without error we say the test is successful

    }

    @Test
    void spaceBasedEviction() {

        for(int i=0; i<1000; i++) {
            String sessionId = "test-session " + i;
            memoryManager.addSessionData(sessionId);


//  For caffeine cache eviction it is not necessary that the LRU Element is always removed; it uses random pooling and determines the LRU from that pool

            if(i>=capacity * 1.5) {
                System.out.println(i);
                System.out.println(memoryManager.isPresent("test-session " + (i - 10)));
                System.out.println(memoryManager.isPresent("test-session " + (i - capacity*1.5)));
            }
        }

    }

    @Test
    void timeBasedEviction() throws InterruptedException {

        for(int i=0; i<50; i++) {
            String sessionId = "test-session " + i;
            memoryManager.addSessionData(sessionId);

//      For custom LRU Cache built by me the TTL implementation is done as a prototype only and will not function as expected
            Thread.sleep(100);
            System.out.println(memoryManager.getCacheSize());
        }

    }

}

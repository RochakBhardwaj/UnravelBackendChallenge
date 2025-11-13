package com.example.UnravelChallenge.UnravelBackendChallenge.part4DeadlockAvoidance;

import com.example.UnravelChallenge.UnravelBackendChallenge.part4DeadLockExample.DeadlockSafeCode.DeadlockSafeExecution;
import com.example.UnravelChallenge.UnravelBackendChallenge.part4DeadLockExample.OldDeadlockCode.DeadlockSimulator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeadlockAvoidanceTest {

    @Test
    public void testDeadlockCase() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(10000);

        DeadlockSimulator deadlockSimulator = new DeadlockSimulator();

        for(int i = 0; i < 10000; i++){
            int finalI = i;
            executorService.submit(()-> {
                        try {
                            if(finalI%2 == 0)
                                deadlockSimulator.method1();
                            else
                                deadlockSimulator.method2();
                            System.out.println(finalI);
                        }
                        catch (Exception e) {
                            System.out.println("Error in unsafe run" + e);
                        }
                        finally {
                            latch.countDown();
                        }
                    });
        }

        boolean completed = latch.await(2, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed);

    }

    @Test
    public void testDeadlockSafeCase() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(10000);

        DeadlockSafeExecution deadlockSimulator = new DeadlockSafeExecution();

        for(int i = 0; i < 10000; i++){
            int finalI = i;
            executorService.submit(()-> {
                try {
                    if(finalI%2 == 0)
                        deadlockSimulator.safeExecute(deadlockSimulator::method1);
                    else
                        deadlockSimulator.safeExecute(deadlockSimulator::method2);

                    System.out.println(finalI);
                }
                catch (Exception e) {
                    System.out.println("Error in safe execution " + e);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(2, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed);

    }
}

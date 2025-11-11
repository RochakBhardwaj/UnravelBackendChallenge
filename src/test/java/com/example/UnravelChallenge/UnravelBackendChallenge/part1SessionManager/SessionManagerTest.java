package com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManager;

import com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.oldSessionManager.RiskySessionManager;
import com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.threadSafeSessionManager.RepeatedLoginException;
import com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.threadSafeSessionManager.SessionManager;
import com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.threadSafeSessionManager.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    String extractSessionId(String input) {
        if (input == null || input.isEmpty()) return null;
        String[] parts = input.trim().split("\\s+"); // split on one or more spaces
        return parts[parts.length - 1]; // return last word
    }

    @Test
    void testSessionIdRaceCondition() throws InterruptedException {
        SessionManager manager = SessionManager.getInstance();
//      Uncomment this and comment above manager to see the error case, please run it multiple times to ensure race condition occurs
//        RiskySessionManager manager = new RiskySessionManager();

        ArrayList<String> userIds = new ArrayList<>();

        for(int i = 0; i < 100; i++){
            userIds.add("user"+String.valueOf(i%5));
        }


        Map<String, AtomicReference<String>> sessionHolders = new ConcurrentHashMap<>();
        for (String userId : new HashSet<>(userIds)) {
            sessionHolders.put(userId, new AtomicReference<>(null));
        }


        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(userIds.size());

        for (String userId : userIds) {
            executor.submit(() -> {
                try {
                    String response = manager.login(userId);

                    if (response.contains("Session ID: ")) {
                        String sessionId = extractSessionId(response);
                        sessionHolders.get(userId).compareAndSet(null, sessionId);
                    }

                } catch (RepeatedLoginException e) {
//                    Better and pinpointed error handling for RepeatedLoginCase
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error while logging in " + e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        for (String userId : sessionHolders.keySet()) {
//          Fetched from local storage of the service calling the Session Manager
            String expected = sessionHolders.get(userId).get();
//          Fetched from Session Manager memory
            String actual = extractSessionId(manager.getSessionDetails(userId));

            System.out.println("User: " + userId +
                    " | expected: " + expected +
                    " | actual: " + actual);

//            Expected and actual values are expected to be equal
            assertEquals(expected, actual,
                    "Race condition detected: multiple sessions for " + userId);
        }
    }


    @Test
    void multipleLogout() throws InterruptedException{

        ArrayList<String> userIds = new ArrayList<>();

        int numberOfUsers = 2;

        for(int i = 0; i < 10000; i++){
            userIds.add("user"+String.valueOf(i%numberOfUsers));
        }

        SessionManager manager = SessionManager.getInstance();

//        Uncomment below line and comment above one to see the race condition in action with user getting multiple acknowledgements
//        RiskySessionManager manager = new RiskySessionManager();

        int threads = 20;

        for(int j = 0; j < numberOfUsers; j++){
            manager.login("user"+j);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(userIds.size());

        AtomicInteger successfulLogouts = new AtomicInteger(0);

        for (String userId : userIds) {
            executor.submit(() -> {
                try {
                    String response = manager.logout(userId);
                    System.out.println(response);
                    if(response.equals("Logout successful.")) {
                        successfulLogouts.incrementAndGet();
                    }
                } catch (UserNotFoundException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error while logging out " + e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(numberOfUsers, successfulLogouts.get(),
                "Expected exactly " + numberOfUsers +
                        " successful logouts, but got " + successfulLogouts.get());

    }

}

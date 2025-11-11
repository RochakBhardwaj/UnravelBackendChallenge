package com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.threadSafeSessionManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Singleton Implementation (Eager)
    private final static SessionManager sessionManager = new SessionManager();
    private SessionManager() {}

    public static SessionManager getInstance() {
        return sessionManager;
    }

    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    //  User is logged in atomically now so that multiple login does not happen and there is no race condition

    public String login(String userId) {
        String newSessionId = "SESSION_" + UUID.randomUUID();

    //  Atomic log in and checking
        String existingSessionId = sessions.putIfAbsent(userId, newSessionId);

        if (existingSessionId != null) {
            throw new RepeatedLoginException("User already logged in. Session ID: " + existingSessionId);
        }

        return "Login successful. Session ID: " + newSessionId;
    }

    /** Logout a user atomically. User does not recieve multiple acknowledgements */

    public String logout(String userId) {

        String sessionId = sessions.get(userId);

        if (sessionId == null) {
            throw new UserNotFoundException("Not found User with user ID " + userId);
        }

        // remove only if this user still maps to the same session ID
        boolean removed = sessions.remove(userId, sessionId);

        if (removed) {
            return "Logout successful.";
        } else {
            // Another thread may have changed it mid-operation
            throw new UserNotFoundException("User logged out by another thread " + userId);
        }
    }

    /** Get session details atomically.
     * Reads are safe with ConcurrentHashMap (no locks needed).
     */
    public String getSessionDetails(String userId) {
        String sessionId = sessions.get(userId);
        if (sessionId == null) {
            throw new UserNotFoundException("User with user ID" + userId + "not found");
        }
        return "Session ID for user " + userId + ": " + sessionId;
    }
}

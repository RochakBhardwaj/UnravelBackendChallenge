package com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.oldSessionManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// This class needs to be singleton
public class RiskySessionManager {
    private Map<String, String> sessions = new ConcurrentHashMap<>();

    public String login(String userId) {
//        This complete step should have been atomic
        if (sessions.containsKey(userId)) {
            return "User already logged in.";
        }
        sessions.put(userId, "SESSION_" + UUID.randomUUID().toString());
        return "Login successful. Session ID: " + sessions.get(userId);
    }
    public String logout(String userId) {
        if (!sessions.containsKey(userId)) {
            return "User not logged in.";
        }
//        Multiple logouts acknowledgement
        sessions.remove(userId);
        return "Logout successful.";
    }
    public String getSessionDetails(String userId) {
        if (!sessions.containsKey(userId)) {
            throw new RuntimeException("Session not found for user " + userId);
        }
        return "Session ID for user " + userId + ": " + sessions.get(userId);
    }
};
package com.example.UnravelChallenge.UnravelBackendChallenge.part1SessionManagement.threadSafeSessionManager;

public class RepeatedLoginException extends RuntimeException {
        public RepeatedLoginException(String message) {
            super(message);
        }
    }
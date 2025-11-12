package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.oldExecution;

import java.util.LinkedList; import java.util.Queue;

public class LogProcessor {

    private Queue<String> logQueue = new LinkedList<>();

    public synchronized void produceLog(String log) {
        logQueue.add(log);
        notify();
    }

    public synchronized String consumeLog() throws InterruptedException {
        while (logQueue.isEmpty()) {
            wait();
        }
        return logQueue.poll();
    }

}
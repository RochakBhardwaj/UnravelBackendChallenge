package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.oldExecution;

class Producer extends Thread {

    private LogProcessor processor;

    public Producer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            processor.produceLog("Log " + i);
        }
    }
}
package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.oldExecution;

class Consumer extends Thread {

    private LogProcessor processor;

    public Consumer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        try {
            while (true) {
                String log = processor.consumeLog();
                System.out.println("Consumed: " + log);
            }
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        }
    }
}
package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogProcessingApp {
    public static void main(String[] args) throws InterruptedException {

        LogProcessor processor = LogProcessor.getInstance();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Producer producer = new Producer(processor);
        Consumer consumer = new Consumer(processor);

        executor.submit(producer);
        executor.submit(consumer);

        executor.shutdown();
    }
}

package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.oldExecution;

public class LogProcessingApp {
    public static void main(String[] args) {

        LogProcessor processor = new LogProcessor();
        Producer producer = new Producer(processor);
        Consumer consumer = new Consumer(processor);

        producer.start();
        consumer.start();

    }
}
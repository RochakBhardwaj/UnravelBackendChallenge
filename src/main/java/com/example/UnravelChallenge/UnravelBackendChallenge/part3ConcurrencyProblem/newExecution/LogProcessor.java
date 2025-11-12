package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class LogProcessor {

    private LogProcessor() {}

    public static LogProcessor logProcessor = new LogProcessor();

    public static LogProcessor getInstance(){
        return logProcessor;
    }

    private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<Task>(100,new TaskComparator() {});

    public void produceLog(Task logTask) {
        queue.add(logTask);
    }

    public Task consumeLog() throws InterruptedException {
        return queue.take();
    }

}
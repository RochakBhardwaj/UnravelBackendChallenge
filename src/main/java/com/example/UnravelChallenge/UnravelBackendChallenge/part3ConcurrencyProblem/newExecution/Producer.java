package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

// Producer is creating new tasks with random priorities. These tasks are being created at a 30 milliseconds delay
public class Producer implements Runnable {

    private LogProcessor processor;

    public Producer(LogProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
//              Adding delay while adding new elements
                Thread.sleep(30);
            }
            catch (InterruptedException e) {}

            String log = "Log " + i;
            Task producedTask = new Task(PriorityEnum.values()[i % 3], log);
            processor.produceLog(producedTask);
        }
    }
}
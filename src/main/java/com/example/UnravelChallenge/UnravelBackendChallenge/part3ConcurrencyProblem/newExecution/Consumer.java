package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

// Consumer is consuming tasks based on there priority. The priority of new tasks is decreased by one after 100 milliseconds
// Added a sleep here for letting tasks to accumualate

public class Consumer implements Runnable {

    private LogProcessor processor;

    public Consumer(LogProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
//                    Letting elements get accumulated
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Task task = processor.consumeLog();
                System.out.println("Consumed: " + task.getMessage() + " | " + task.getTaskPriority());
            }
        } catch (InterruptedException e) {
            System.out.println("Consumer thread interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

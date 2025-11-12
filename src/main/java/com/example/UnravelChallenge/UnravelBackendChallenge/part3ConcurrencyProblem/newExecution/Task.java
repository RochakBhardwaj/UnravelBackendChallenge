package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

// This is the Task Class which creates the Tasks based on provided capacity and the creation time
// The priority is decreased by one after 100 milliseconds

public class Task {
    private final PriorityEnum taskPriority;
    private final int finalPriority;
    private final String message;
    private final long createdTimeNano;

//  Hundred milliseconds (Priority decreases after 100 millisecond wait)
    private final long timeQuantam = 100000000L;

    public Task(PriorityEnum priority, String message) {
        this.taskPriority = priority;
        this.message = message;
        this.createdTimeNano = System.nanoTime();
        this.finalPriority = priority.ordinal() - (int)(createdTimeNano/timeQuantam);
    }

    public PriorityEnum getTaskPriority() {
        return taskPriority;
    }

    public int getFinalPriority() {
        return finalPriority;
    }

    public long getCreatedTimeNano() {
        return createdTimeNano;
    }

    public String getMessage() {
        return message;
    }

};

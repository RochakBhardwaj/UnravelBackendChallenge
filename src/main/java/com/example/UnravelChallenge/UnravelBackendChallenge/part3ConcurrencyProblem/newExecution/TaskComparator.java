package com.example.UnravelChallenge.UnravelBackendChallenge.part3ConcurrencyProblem.newExecution;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    public int compare(Task t1, Task t2) {

//        positive means t2 has more priority than t1
        int priorityCompare = t2.getFinalPriority() - t1.getFinalPriority();

        if(priorityCompare != 0){
            return priorityCompare;
        }

        return (int)(t1.getCreatedTimeNano() - t2.getCreatedTimeNano());
    }
};
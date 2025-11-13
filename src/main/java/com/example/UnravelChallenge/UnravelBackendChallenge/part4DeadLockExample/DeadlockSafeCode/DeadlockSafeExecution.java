package com.example.UnravelChallenge.UnravelBackendChallenge.part4DeadLockExample.DeadlockSafeCode;

// One way to solve this problem would have been to change the order of the locks being acquired
// Another way is to use trylock with some delay and retry the operation after some time in case lock was not acquired intially
// But in the question it is given that we are using external library so I am assuming that method1 and method2 are from external library and I cant modify them

public class DeadlockSafeExecution {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    private final Object lock3 = new Object();

    //    I am assuming I am not allowed to change the order of execution of lock2 and lock1
    public void method1() {
        synchronized (lock1) {
            synchronized (lock2) {
                System.out.println("Method1: Acquired lock1 and lock2");
            }
        }
    }

//    I am assuming I am not allowed to change the order of execution of lock2 and lock1
    public void method2() {
        synchronized (lock2) {
            synchronized (lock1) {
                System.out.println("Method2: Acquired lock2 and lock1");
            }
        }
    }

    public void safeExecute(Runnable riskyFunction){
        synchronized (lock3){
            riskyFunction.run();
        }
    }

    public static void main(String[] args) {
        DeadlockSafeExecution simulator = new DeadlockSafeExecution();
        Thread t1 = new Thread(()-> simulator.safeExecute(simulator::method1));
        Thread t2 = new Thread(()-> simulator.safeExecute(simulator::method2));
        t1.start();
        t2.start();
    }
}

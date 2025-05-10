package joe.experiment.juc.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockReentrantExample {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        lock.lock();
        try {
            System.out.println("I'm lock for the first time");
            reentrant();
        } finally {
            lock.unlock();
        }
    }

    private static void reentrant() {
        lock.lock();
        try {
            System.out.println("I'm having the lock but try to lock it twice");
        } finally {
            lock.unlock();
        }
    }
}

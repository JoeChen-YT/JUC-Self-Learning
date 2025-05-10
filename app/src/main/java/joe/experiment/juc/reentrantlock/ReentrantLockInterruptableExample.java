package joe.experiment.juc.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockInterruptableExample {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            try {
                lock.lockInterruptibly();
                System.out.println("Thread 1 has successfully acquire the lock");
            } catch (InterruptedException e) {
                System.out.println("Thread 1 has been interrupted by main thread");
                return;
            }

            try {
                System.out.println("Doing some business logic");
            } finally {
                lock.unlock();
            }
        }, "Thread1");

        lock.lock();
        System.out.println("Main thread acquire the lock first");
        thread.start();

        Thread.sleep(2000);
        thread.interrupt();
    }
}

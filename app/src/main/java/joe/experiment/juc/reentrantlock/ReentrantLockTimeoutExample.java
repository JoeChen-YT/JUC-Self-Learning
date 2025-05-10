package joe.experiment.juc.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTimeoutExample {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            try {
                if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                    System.out.println("t1 couldn't acquire the lock within 1s");
                    return;
                }
            } catch (InterruptedException e) {
                System.out.println("t1 has been interrupted while waiting for the lock within 1s");
                return;
            }
            System.out.println("t1 has successfully acquire the lock within 1s");

            try {
                System.out.println("t1 Doing business....");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        t1.start();
        try {
            System.out.println("Main thread doing business");
            Thread.sleep(1500);
        } finally {
            lock.unlock();
            System.out.println("Main thread finished the business");
        }
    }
}

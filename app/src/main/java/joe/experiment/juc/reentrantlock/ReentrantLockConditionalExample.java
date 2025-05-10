package joe.experiment.juc.reentrantlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockConditionalExample {
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean hasCigarette = false;
    private static boolean hasTakeout = false;
    private static final Condition cigaretteWaitingRoom = lock.newCondition();
    private static final Condition takeoutWaitingRoom = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        final Thread waitForCigarette = new Thread(() -> {
            // first we need to acquire the lock
            lock.lock();
            try {
                while (!hasCigarette) {
                    try {
                        System.out.println("Waiting for the cigarette....");
                        // Then we wait for a condition
                        cigaretteWaitingRoom.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Got cigatette, now I'm doing the business");
            } finally {
                lock.unlock();
            }
        }, "waitForCigarette");
        final Thread waitForTakeout = new Thread(() -> {
            // first we need to acquire the lock
            lock.lock();
            try {
                while (!hasTakeout) {
                    try {
                        System.out.println("Waiting for the takeout....");
                        // Then we wait for a condition
                        takeoutWaitingRoom.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Got takeout, now I'm doing the business");
            } finally {
                lock.unlock();
            }
        }, "waitForTakeout");
        final Thread deliverCigarette = new Thread(() -> {
            lock.lock();
            try {
                hasCigarette = true;
                cigaretteWaitingRoom.signal();
            } finally {
                lock.unlock();
            }
        }, "deliverCigarette");
        final Thread deliverTakeout = new Thread(() -> {
            lock.lock();
            try {
                hasTakeout = true;
                takeoutWaitingRoom.signal();
            } finally {
                lock.unlock();
            }
        }, "deliverTakeout");
        waitForCigarette.start();
        waitForTakeout.start();
        Thread.sleep(1000);
        deliverCigarette.start();
        deliverTakeout.start();
    }
}

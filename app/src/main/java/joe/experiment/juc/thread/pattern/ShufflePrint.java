package joe.experiment.juc.thread.pattern;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ShufflePrint {
    static Thread la, lb, lc;
    public static void main(String[] args) {
//        shufflePrintWithSynchronized();
//        shufflePrintWithReentrantLock();
        shufflePrintWithLockSupport();
    }

    private static void shufflePrintWithLockSupport() {
        final int[] times = new int[3];

        la = new Thread(() -> {
            while(times[0] < 5) {
                LockSupport.park();
                System.out.print("a");
                times[0] = times[0] + 1;
                LockSupport.unpark(lb);
            }
        }, "a");
        lb = new Thread(() -> {
            while(times[1] < 5) {
                LockSupport.park();
                System.out.print("b");
                times[1] = times[1] + 1;
                LockSupport.unpark(lc);

            }
        }, "b");
        lc = new Thread(() -> {
            while(times[2] < 5) {
                LockSupport.park();
                System.out.print("c");
                times[2] = times[2] + 1;
                LockSupport.unpark(la);

            }
        }, "c");

        la.start();
        lb.start();
        lc.start();

        LockSupport.unpark(la);
    }

    private static void shufflePrintWithReentrantLock() {
        final ReentrantLock lock = new ReentrantLock();
        final int[] times = new int[3];
        final boolean[] flags = new boolean[3];
        final Condition[] waitRooms = new Condition[] {lock.newCondition(), lock.newCondition(), lock.newCondition()};

        final Thread a = new Thread(() -> {
            lock.lock();
            try {
                while(times[0] < 5) {
                    while(!flags[0]) {
                        waitRooms[0].await();
                    }
                    System.out.print("a");
                    times[0] = times[0] + 1;
                    flags[0] = false;
                    flags[1] = true;
                    waitRooms[1].signal();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "a");
        final Thread b = new Thread(() -> {
            lock.lock();
            try {
                while(times[1] < 5) {
                    while(!flags[1]) {
                        waitRooms[1].await();
                    }
                    System.out.print("b");
                    times[1] = times[1] + 1;
                    flags[1] = false;
                    flags[2] = true;
                    waitRooms[2].signal();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "b");
        final Thread c = new Thread(() -> {
            lock.lock();
            try {
                while(times[2] < 5) {
                    while(!flags[2]) {
                        waitRooms[2].await();
                    }
                    System.out.print("c");
                    times[2] = times[2] + 1;
                    flags[2] = false;
                    flags[0] = true;
                    waitRooms[0].signal();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "c");

        flags[0] = true;
        a.start();
        b.start();
        c.start();
    }

    private static void shufflePrintWithSynchronized() {
        final Object monitor = new Object();
        final int[] times = new int[3];
        final boolean[] flags = new boolean[3];

        final Thread a = new Thread(() -> {
            synchronized (monitor) {
                while(times[0] < 5) {
                    while(!flags[0]) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Satisfy the condition
                    System.out.print("a");
                    times[0] = times[0] + 1;
                    flags[0] = false;
                    flags[1] = true;
                    monitor.notifyAll();
                }
            }
        }, "a");

        final Thread b = new Thread(() -> {
            synchronized (monitor) {
                while(times[1] < 5) {
                    while(!flags[1]) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Satisfy the condition
                    System.out.print("b");
                    times[1] = times[1] + 1;
                    flags[1] = false;
                    flags[2] = true;
                    monitor.notifyAll();
                }
            }
        }, "b");

        final Thread c = new Thread(() -> {
            synchronized (monitor) {
                while(times[2] < 5) {
                    while(!flags[2]) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Satisfy the condition
                    System.out.print("c");
                    times[2] = times[2] + 1;
                    flags[2] = false;
                    flags[0] = true;
                    monitor.notifyAll();
                }
            }
        }, "b");

        flags[0] = true;
        a.start();
        b.start();
        c.start();
    }
}

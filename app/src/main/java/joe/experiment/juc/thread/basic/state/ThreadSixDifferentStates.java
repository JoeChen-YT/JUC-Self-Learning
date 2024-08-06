package joe.experiment.juc.thread.basic.state;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class ThreadSixDifferentStates {

    public static void main(String[] args) throws InterruptedException {
        final Thread thread1 = new Thread(() -> {
            log.info("thread1 is created");
        }, "thread1");

        final Thread thread2 = new Thread(() -> {
            log.info("thread2 is created");
            log.info("thread2 is doing something");
            synchronized (ThreadSixDifferentStates.class) {
                while (true) {

                }
            }
        }, "thread2");
        thread2.setDaemon(true);
        thread2.start();

        final Thread thread3 = new Thread(() -> {
            log.info("thread3 is created");
            try {
                log.info("thread3 is doing a long-running tasks");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "thread3");
        thread3.start();

        final Thread thread4 = new Thread(() -> {
            log.info("thread4 is created");
            log.info("thread4 is waiting for thread3");
            try {
                thread3.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "thread4");
        thread4.start();

        final Thread thread5 = new Thread(() -> {
            log.info("thread5 is created");
            // We have to
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (ThreadSixDifferentStates.class) {
                log.info("thread5 is blocked because of thread2 holding the lock");
            }
        }, "thread5");
        thread5.setDaemon(true);
        thread5.start();

        final Thread thread6 = new Thread(() -> {
            log.info("thread6 is created");
            log.info("thread6 is doing something quick");
            log.info("thread6 is done");
        }, "thread6");
        thread6.start();

        TimeUnit.SECONDS.sleep(1);
        log.info("Thread1's current state is {}", thread1.getState());
        log.info("Thread2's current state is {}", thread2.getState());
        log.info("Thread3's current state is {}", thread3.getState());
        log.info("Thread4's current state is {}", thread4.getState());
        log.info("Thread5's current state is {}", thread5.getState());
        log.info("Thread6's current state is {}", thread6.getState());
    }
}

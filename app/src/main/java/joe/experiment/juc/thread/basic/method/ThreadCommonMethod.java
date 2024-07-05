package joe.experiment.juc.thread.basic.method;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class ThreadCommonMethod {
    private static int SHARED_DATA = 0;
    public static void main(String[] args) throws InterruptedException {
//        threadStartMethod();
//        threadGetStateMethod();
//        threadSleepMethod();
//        threadJoinMethod();
        threadSetDaemonMethod();
    }

    private static Thread getThread() {
        return new Thread("t1") {
            @Override
            public void run() {
                log.info("I'm running in t1 thread");
            }
        };
    }

    private static Thread getThreadWithSleepInside() {
        return new Thread("t1_sleep") {
            @Override
            public void run() {
                log.info("I'm running in t1_sleep thread");
                log.info("now I'll sleep");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.info("I'm being interrupted, so annoying!!! Why bother me?");
                }
                log.info("now I'm awake naturally");
            }
        };
    }

    private static Thread getThreadToBeJoined() {
        return new Thread("t1_to_be_joined") {
            @Override
            public void run() {
                log.info("I'm running in t1_to_be_joined thread");
                log.info("now I'll retrieving something from network");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.info("I'm being interrupted, so annoying!!! Why bother me?");
                }
                SHARED_DATA = 2;
                log.info("now I finished the retrieval task");
            }
        };
    }

    private static void threadStartMethod() {
        final Thread thread = getThread();
        thread.start();
        // Once thread has already started, another call to start() method will throw IllegalThreadStateException
        thread.start();
    }

    private static void threadGetStateMethod() {
        final Thread thread = getThread();
        log.info("I'm currently in state as {}.", thread.getState());
        thread.start();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        log.info("I'm currently in state as {}.", thread.getState());
    }

    private static void threadSleepMethod() {
        // It's the thread that you call Thread.sleep() method will actually sleep

        // In this case, it's the main thread will sleep for 1s.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final Thread threadWithSleep = getThreadWithSleepInside();
        threadWithSleep.start();
        // Try to get the status of the thread who in sleep state: It's TIMED_WAITING
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("The t1_sleep is with state as {}", threadWithSleep.getState());
        log.info("Now I'm the main thread trying to interrupt the sleepy thread t1");
        threadWithSleep.interrupt();
    }

    private static void threadJoinMethod() {
        final Thread threadToBeJoined= getThreadToBeJoined();
        threadToBeJoined.start();
        log.info("Main thread starts waiting for the threadToBeJoined");
        try {
            // wait until the threadToBeJoined finished its job
//            threadToBeJoined.join();

            // set a timeout value along with the join()
            threadToBeJoined.join(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Now the SHARED_DATA is {}", SHARED_DATA);
    }

    private static void threadSetDaemonMethod() throws InterruptedException {
        final Thread threadWithSleep = getThreadWithSleepInside();
        // Once the thread is set to daemon, the JVM will close right after the main thread finished.
        threadWithSleep.setDaemon(true);
        log.info("The daemon thread is created");
        threadWithSleep.start();
        log.info("The daemon thread starts");
        TimeUnit.MILLISECONDS.sleep(500);
        log.info("The main thread finished");
    }
}

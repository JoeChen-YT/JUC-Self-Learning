package joe.experiment.juc.thread.basic;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JavaThreadCreation {
    private static final String MAIN_THREAD_LOG_MESSAGE = "Main thread is running";
    private static final String CHILD_THREAD_LOG_MESSAGE = "I'm in the child thread";

    public static void main(String[] args) {
        final Thread childThread = threadCreationViaRunnableInLambda();

        childThread.start();
        log.info(MAIN_THREAD_LOG_MESSAGE);
    }

    private static Thread threadCreation() {
        return new Thread("ChildThread") {
            @Override
            public void run() {
                log.info(CHILD_THREAD_LOG_MESSAGE);
            }
        };
    }

    /*
     Using runnable to create the thread object is better than the previous one, since it decouples the operation task
     which is the runnable object from the thread which is basically a running engine.
     */
    private static Thread threadCreationViaRunnable() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.info(CHILD_THREAD_LOG_MESSAGE);
            }
        };
        return new Thread(runnable, "ChildThread");
    }
    private static Thread threadCreationViaRunnableInLambda() {
        return new Thread(() -> {log.info(CHILD_THREAD_LOG_MESSAGE);},"ChildThread");
    }

}

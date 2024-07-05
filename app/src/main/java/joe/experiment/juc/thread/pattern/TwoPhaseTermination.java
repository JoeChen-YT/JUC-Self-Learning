package joe.experiment.juc.thread.pattern;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

@Log4j2
public class TwoPhaseTermination {
    public static void main(String[] args) throws InterruptedException {
        final ErrorLogMonitor errorLogMonitor = new ErrorLogMonitor();
        log.info("Main thread start the error monitor");
        errorLogMonitor.startMonitor();
        // Mimic the time spent on the main thread business logic
        TimeUnit.SECONDS.sleep(10);
        log.info("Main thread stop the error monitor");
        errorLogMonitor.stopMonitor();
    }
}

@Log4j2
class ErrorLogMonitor {
    private Thread monitor;

    public void startMonitor() {
        monitor = new Thread("monitorThread") {
            @Override
            public void run() {
                while(true) {
                    final Thread currentThread = Thread.currentThread();
                    log.info("The interrupted status for current thread is {}", currentThread.isInterrupted());
                    if (currentThread.isInterrupted()) {
                        break;
                    }
                    try {
                        log.info("No error log found!!!");
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        log.info("monitorThread is being interrupted! prepare to shut down!");
                        log.info("gracefully clean up all the resources!!!");
                        currentThread.interrupt();
                    }
                }
            }
        };
        monitor.start();
    }

    public void stopMonitor() {
        monitor.interrupt();
    }
}

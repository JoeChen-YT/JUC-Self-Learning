package joe.experiment.juc.thread.basic.creation;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Log4j2
public class JavaThreadCreationExtension {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Callable<Integer> callable = () -> {
            log.info("I'm inside the ChildThread");
            Thread.sleep(5000);
            log.info("Successfully retrieve the data");
            return 1995;
        };
        final FutureTask<Integer> futureTask = new FutureTask<>(callable);
        final Thread childThreadThread = new Thread(futureTask, "ChildThread");
        log.info("ChildThread has been prepared");
        childThreadThread.start();
        log.info("ChildThread has started");
        log.info("Get the data from ChildThread as {}", futureTask.get());
        log.info("MainThread finished");
    }
}

package joe.experiment.juc.threadpool;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a customized implementation thread pool which only contains very basic functionality:
 *  - It allows the user to choose the concurrent size of the thread and the size of the blocking queue:
 *    - If the task submission rate > the task consumption rate -> The thread that submit the task will wait until there is an available slot in the blocking queue.
 *    - If the task consumption rate > the task submission rate -> Those workers won't be destroyed since they're long-running worker for this version of implementation.
 */
public class CustomizedThreadPool {
    public static void main(String[] args) {
        final LongRunningThreadPool threadPool = new LongRunningThreadPool(2, 4);
        for(int i = 0; i < 10; i++) {
            int j = i;
            new Thread(() -> {
                threadPool.execute(() -> {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Instant.now().toString() + " | " + "Hello From task " + j);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }, "thread" + i).start();
        }
    }

}

class LongRunningThreadPool {
    private final ArrayList<Thread> coreWorkers;
    private final BlockingQueue blockingQueue;

    public LongRunningThreadPool(final int workerSize, final int queueSize) {
        this.blockingQueue = new BlockingQueue(queueSize);
        this.coreWorkers = new ArrayList<>();
        for (int i = 0; i < workerSize; i++) {
            final Thread worker = new LongRunningWorker();
            coreWorkers.add(worker);
            worker.start();
        }
    }

    public void execute(final Runnable task) {
        blockingQueue.put(task);
    }

    class LongRunningWorker extends Thread {

        public LongRunningWorker() {}

        public void run() {
            Runnable task;
            while ((task = blockingQueue.take()) != null) {
                System.out.println(Instant.now().toString() + " | " + Thread.currentThread().getName() + "is starting to run the " + task);
                task.run();
            }
        }
    }
}

class BlockingQueue {
    private final Deque<Runnable> taskQueue = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition producableCondition = lock.newCondition();
    private final Condition consumableCondition = lock.newCondition();
    private final int capacity;

    public BlockingQueue(final int capacity) {
        this.capacity = capacity;
    }

    public Runnable take() {
        lock.lock();
        try {
            while (taskQueue.size() == 0) {
               consumableCondition.await();
            }
            final Runnable task = taskQueue.removeFirst();
            System.out.println(Instant.now().toString() + " | " + task + " is getting out of the blocking queue");
            producableCondition.signal();
            return task;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void put(final Runnable task) {
        lock.lock();
        try {
            while (taskQueue.size() == capacity) {
                producableCondition.await();
            }
            taskQueue.addLast(task);
            System.out.println(Instant.now().toString() + " | " + task + " is adding to the blocking queue");
            consumableCondition.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}

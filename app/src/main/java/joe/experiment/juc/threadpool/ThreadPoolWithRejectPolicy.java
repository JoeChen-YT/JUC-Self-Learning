package joe.experiment.juc.threadpool;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolWithRejectPolicy {
    public static void main(String[] args) {
        final RejectPolicy longWaitPolicy = BlockingQueueSupportingRejectPolicy::put;
        final RejectPolicy throwExceptionPolicy = (blockingQueue, task) -> {throw new RuntimeException();};
        final RejectPolicy doNothingPolicy = (blockingQueue, task) -> {};
        final ThreadPoolSupportingRejectPolicy threadPool = new ThreadPoolSupportingRejectPolicy(2, 4, throwExceptionPolicy);
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

@FunctionalInterface
interface RejectPolicy {
    void reject(final BlockingQueueSupportingRejectPolicy blockingQueue, final Runnable task);
}

class ThreadPoolSupportingRejectPolicy {
    private final ArrayList<Thread> coreWorkers;
    private final BlockingQueueSupportingRejectPolicy blockingQueue;

    private final RejectPolicy rejectPolicy;

    public ThreadPoolSupportingRejectPolicy(final int workerSize, final int queueSize, final RejectPolicy rejectPolicy) {
        this.rejectPolicy = rejectPolicy;
        this.blockingQueue = new BlockingQueueSupportingRejectPolicy(queueSize);
        this.coreWorkers = new ArrayList<>();
        for (int i = 0; i < workerSize; i++) {
            final Thread worker = new LongRunningWorker();
            coreWorkers.add(worker);
            worker.start();
        }
    }

    public void execute(final Runnable task) {
        blockingQueue.tryPut(task, rejectPolicy);
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

class BlockingQueueSupportingRejectPolicy {
    private final Deque<Runnable> taskQueue = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition producableCondition = lock.newCondition();
    private final Condition consumableCondition = lock.newCondition();
    private final int capacity;

    public BlockingQueueSupportingRejectPolicy(final int capacity) {
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

    public void tryPut(final Runnable task, final RejectPolicy rejectPolicy) {
        lock.lock();
        try {
            if (taskQueue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {
                taskQueue.addLast(task);
                System.out.println(Instant.now().toString() + " | " + task + " is adding to the blocking queue");
                consumableCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

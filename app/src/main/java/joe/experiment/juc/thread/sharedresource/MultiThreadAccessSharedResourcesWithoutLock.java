package joe.experiment.juc.thread.sharedresource;

public class MultiThreadAccessSharedResourcesWithoutLock {
    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        classicMultiThreadRaceConditionProblem();
    }

    public static void classicMultiThreadRaceConditionProblem() throws InterruptedException {
        // Be sure to initialize the threads first, and the start them together!!!
        // If you initialize one and start it right away, and then initialize another one and start it,
        // the calculation of the first one might already finish before the second one started. In other words,
        // You might not be able to see the expected result you wish.
        final Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                count++;
            }
        });
        final Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                count--;

            }
        });

        thread1.start();
        thread2.start();

        // Be sure to wait for a while before the calculation is done
        Thread.sleep(1000);

        System.out.println(count);
    }
}

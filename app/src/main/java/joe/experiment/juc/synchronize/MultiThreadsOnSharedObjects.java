package joe.experiment.juc.synchronize;

public class MultiThreadsOnSharedObjects {
    private static int SHARED_INT = 0;

    public static void main(String[] args) throws InterruptedException {
        final Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                SHARED_INT++;
            }
        }, "Thread1");

        final Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                SHARED_INT++;
            }
        }, "Thread2");

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        System.out.println(SHARED_INT);
    }
}

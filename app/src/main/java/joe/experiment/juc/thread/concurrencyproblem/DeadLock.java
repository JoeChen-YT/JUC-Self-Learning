package joe.experiment.juc.thread.concurrencyproblem;

public class DeadLock {
    public static final Object resource1 = new Object();
    public static final Object resource2 = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (resource1) {
                System.out.println("Successfully acquire the resource1 lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (resource2) {
                    System.out.println("Successfully acquire the resource2 lock");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (resource2) {
                System.out.println("Successfully acquire the resource2 lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (resource1) {
                    System.out.println("Successfully acquire the resource1 lock");
                }
            }
        }).start();
    }
}

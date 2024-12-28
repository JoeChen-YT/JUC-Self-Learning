package joe.experiment.juc.thread.pattern;

import java.time.Instant;

public class GuardedSuspension {
    public static void main(String[] args) {
        final GuardedObject<String> guardedObject = new GuardedObject<>();
        // Testing unlimited wait for get() method.
//        new Thread(() -> {
//            System.out.println(Instant.now().toString() + " Waiting for the response");
//            final String response = guardedObject.get();
//            System.out.println(Instant.now().toString() + " Successfully get the response: " + response);
//        }).start();
//        new Thread(() -> {
//            System.out.println(Instant.now().toString() + " Trying to get the response");
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            guardedObject.complete("Hello Guarded Suspension Pattern");
//            System.out.println(Instant.now().toString() + " Successfully set the response");
//        }).start();
        // Testing time waiting for get(milliseconds) method
        new Thread(() -> {
            System.out.println(Instant.now().toString() + " Waiting for the response");
            final String response = guardedObject.get(2000);
            System.out.println(Instant.now().toString() + " Successfully get the response: " + response);
        }).start();
        new Thread(() -> {
            System.out.println(Instant.now().toString() + " Trying to get the response");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            guardedObject.complete("Hello Guarded Suspension Pattern");
            System.out.println(Instant.now().toString() + " Successfully set the response");
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Instant.now().toString() + " Mimic the false invocation");
            guardedObject.complete(null);
        }).start();
    }
}

class GuardedObject<T> {
    private T object;

    public T get() {
        synchronized (this) {
            while (object == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }
    }

    public T get(long milliseconds) {
        long beginningTime = System.currentTimeMillis();
        synchronized (this) {
            while(object == null) {
                long remainingTime = milliseconds - (System.currentTimeMillis() - beginningTime);
                if (remainingTime < 0) {
                    break;
                }
                try {
                    this.wait(remainingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return object;
        }
    }

    public void complete(final T object) {
        synchronized (this) {
            this.object = object;
            this.notifyAll();
        }
    }
}

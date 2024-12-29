package joe.experiment.juc.thread.pattern;

import java.util.LinkedList;

public class ProducerAndConsumer {
    public static void main(String[] args) throws InterruptedException {
        final MessageQueue messageQueue = new MessageQueue(5);

        // Create 8 threads to put message first
        for (int i = 0; i < 8; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    messageQueue.put(new Message(id, "Hello For Message " + id));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        // Sleep 2s
        Thread.sleep(2000);

        // Create 4 threads to consume the message
        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                try {
                    messageQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }
}

class MessageQueue {
    private final LinkedList<Message> messages;
    private final int capacity;

    public MessageQueue(final int capacity) {
        this.messages = new LinkedList<>();
        this.capacity = capacity;
    }

    public Message take() throws InterruptedException {
        synchronized (messages) {
            while (messages.size() == 0) {
                messages.wait();
            }
            final Message message = messages.removeFirst();
            System.out.println("Take message out of the queue with id as " + message.id() + ", and content as " + message.message());
            messages.notifyAll();
            return message;
        }
    }

    public void put(final Message message) throws InterruptedException {
        synchronized (messages) {
            while (messages.size() == capacity) {
                messages.wait();
            }
            messages.addLast(message);
            System.out.println("Put message into the queue with id as " + message.id() + ", and content as " + message.message());
            messages.notifyAll();
        }
    }
}

record Message(int id, String message) {}

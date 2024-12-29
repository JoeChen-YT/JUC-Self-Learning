package joe.experiment.juc.thread.pattern;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class GuardedSuspensionExample {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            new People().start();
        }

        Thread.sleep(2000);

        for (int id : MailBoxes.getIds()) {
            new Postman(id, "Hello from Postman： " + id).start();
        }
    }
}

class People extends Thread {
    @Override
    public void run() {
        final GuardedObject<String> mailbox = MailBoxes.generateMailbox();
        System.out.println("开始收信 id: " + mailbox.getId());
        String content = mailbox.get(5000);
        System.out.println("收到信 id: " + mailbox.getId() + " 内容: " + content);
    }
}

class Postman extends Thread {
    private int id;
    private String content;

    public Postman(final int id, final String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public void run() {
        final GuardedObject<String> mailbox = MailBoxes.getMailbox(id);
        System.out.println("开始送信 id: " + id + " 内容: " + content);
        mailbox.complete(content);
    }
}

class MailBoxes {
    private static int id = 0;
    private static Map<Integer, GuardedObject<String>> mailboxes = new Hashtable<>();

    public static GuardedObject<String> generateMailbox() {
        final int id = generateId();
        final GuardedObject<String> mailbox = new GuardedObject<>(id);
        mailboxes.put(id, mailbox);
        return mailbox;
    }

    public static GuardedObject<String> getMailbox(int id) {
        return mailboxes.remove(id);
    }

    public static Set<Integer> getIds() {
        return new HashSet<>(mailboxes.keySet());

    }

    private static synchronized int generateId() {
        return id++;
    }
}

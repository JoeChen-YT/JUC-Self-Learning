package joe.experiment.juc.thread.sharedresource;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TicketSellingProblem {
    public static void main(String[] args) throws InterruptedException {
        final TicketWindow ticketWindow = new TicketWindow(1000);
        final List<Integer> ticketCountList = new Vector<>();
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            final Thread thread = new Thread(() -> {
                final int ticketCount = randomCount();
                int soldTicketNumber = ticketWindow.sellNotThreadSafe(ticketCount);
//                int soldTicketNumber = ticketWindow.sellThreadSafe(ticketCount);
                System.out.println("Sold ticket with number as " + soldTicketNumber);
                ticketCountList.add(soldTicketNumber);
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("余票数为:" + ticketWindow.getRemainingTicket());
        System.out.println("卖出票数为:" + ticketCountList.stream().mapToInt(count -> count).sum());
    }

    private static int randomCount() {
        return (int) Math.ceil(Math.random() * 5) + 1;
    }
}

class TicketWindow {
    private int count;

    public TicketWindow(final int count) {
        this.count = count;
    }

    public int getRemainingTicket() {
        return count;
    }

    public int sellNotThreadSafe(final int count) {
        if (this.count >= count) {
            this.count -= count;
            return count;
        } else {
            return 0;
        }
    }

    public synchronized int sellThreadSafe(final int count) {
        if (this.count >= count) {
            this.count -= count;
            return count;
        } else {
            return 0;
        }
    }
}
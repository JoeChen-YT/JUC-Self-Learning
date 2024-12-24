package joe.experiment.juc.thread.sharedresource;

import org.checkerframework.checker.units.qual.A;

public class AccountTransferProblem {
    public static void main(String[] args) throws InterruptedException {
        final Account accountA = new Account(5000);
        final Account accountB = new Account(5000);

        final Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                accountA.transferThreadNotSafe(accountB, randomCount());
//                accountA.transferThreadSafe(accountB, randomCount());
            }
        });
        thread1.start();

        final Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                accountB.transferThreadNotSafe(accountA, randomCount());
//                accountB.transferThreadSafe(accountA, randomCount());
            }
        });
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("The total amount is: " + (accountA.getMoney() + accountB.getMoney()));
    }

    private static int randomCount() {
        return (int) Math.ceil(Math.random() * 5) + 1;
    }
}

class Account {
    private int money;

    public Account(final int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(final int money) {
        this.money = money;
    }

    public void transferThreadNotSafe(final Account account, final int amount) {
        if (this.money >= amount) {
            setMoney(this.money - amount);
            account.setMoney(this.money + amount);
        }
    }

    public void transferThreadSafe(final Account account, final int amount) {
        synchronized (Account.class) {
            if (this.money >= amount) {
                setMoney(this.money - amount);
                account.setMoney(account.money + amount);
            }
        }
    }


}

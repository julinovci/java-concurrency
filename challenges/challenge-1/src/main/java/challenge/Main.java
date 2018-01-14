package challenge;


public class Main {

    
    // Create and start two threads that use the same bank account instance
    // and with initial balance of 1000. One will deposit 300 into the bank
    // account and then withdraw 50. The other will deposit 203.75 and then
    // withdraw 100
    public static void main(String[] args) {
        final BankAccount account = new BankAccount("ABNA97594", 1000.00);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                account.deposit(300.00);
                account.withdraw(50.00);
                System.out.println("Thread 1: Transaction completed for " + account.getAccountNumber());
            }
        });
        
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                account.deposit(203.75);
                account.withdraw(100.00);
                System.out.println("Thread 2: Transaction completed for " + account.getAccountNumber());
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {}

        System.out.println("Final balance " + account.getBalance());
    }
}
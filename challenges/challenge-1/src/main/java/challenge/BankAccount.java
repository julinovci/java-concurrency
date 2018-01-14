package challenge;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {

    private double balance;
    private String accountNumber;
    private ReentrantLock lock;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.lock = new ReentrantLock();
    }

    // public synchronized void deposit(double amount) {
    public void deposit(double amount) {
        // Local variable is threadsafe. No need to synchronize.
        boolean status = false;
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    balance += amount;
                    status = true;
                } finally {
                    lock.unlock();
                }
            }
            else {
                System.out.println("Could not get the lock");
            }
        } catch (InterruptedException e) {
            // tryLock can throw uninterruted exception
        }

        System.out.println("Transaction status " + status);
    }

    // public synchronized void withdraw(double amount) {
    public void withdraw(double amount) {
        boolean status = false;
        try {
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    balance -= amount;
                    status = true;
                } finally {
                    lock.unlock();
                }
            }
            else {
                System.out.println("Could not get the lock");
            }
        } catch (InterruptedException e) {}

        System.out.println("Transaction status " + status);
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void printAccountNumber() {
        System.out.println("Account number = " + accountNumber);
    }
}
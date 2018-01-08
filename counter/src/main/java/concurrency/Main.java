package concurrency;

public class Main {

    public static void main(String[] args) {
        Countdown countdown = new Countdown();

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");
        
        t1.start();
        t2.start();
    }

}

class Countdown {
    
    private int i;

    // to avoid race conditions, do:
    // - public synchronized void doCountdown(), or
    // - use synchronized block
    // using synchronized block is better 'cause it keeps
    // the code that is synchronized to a minimum
    // don't want to have threads suspended
    public void doCountdown() {
        String colour;

        switch(Thread.currentThread().getName()) {
            case "Thread 1":
                colour = ThreadColour.ANSI_CYAN;
                break;
            case "Thread 2":
                colour = ThreadColour.ANSI_PURPLE;
                break;
            default:
                colour = ThreadColour.ANSI_GREEN;
        }

        // can't use local variable (e.g. colour) in synchronized block
        // every thread has its own copy of the local variable in the thread stack
        synchronized(this) {
            for (i = 10; i > 0; i--) {
                System.out.println(colour + Thread.currentThread().getName() + ": i = " + i);
            }
        }
    }
}

class CountdownThread extends Thread {
    private Countdown countdown;

    public CountdownThread(Countdown cd) {
        countdown = cd;
    }

    public void run() {
        countdown.doCountdown();
    }
}
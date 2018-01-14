package starvation;

import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // true means lock is fair, first come first served order
    // only fairness in aquiring the lock is guaranteed, not fairness in thread scheduling
    // When using fair lock with a lot of threads, performance will be impacted
    // Depending on the task, it does not matter if the thread is waiting for a long time (i.e. starving),
    // using synchronized block would be better than using fair locks. 
    public static ReentrantLock lock = new ReentrantLock(true);

    public static void main(String[] args) {

        Thread t1 = new Thread(new Worker(ThreadColour.ANSI_WHITE), "Priority 10");
        Thread t2 = new Thread(new Worker(ThreadColour.ANSI_YELLOW), "Priority 8");
        Thread t3 = new Thread(new Worker(ThreadColour.ANSI_CYAN), "Priority 6");
        Thread t4 = new Thread(new Worker(ThreadColour.ANSI_GREEN), "Priority 4");
        Thread t5 = new Thread(new Worker(ThreadColour.ANSI_RED), "Priority 2");

        // Priority is a suggestion. It is not binding.
        t1.setPriority(10);
        t2.setPriority(8);
        t3.setPriority(6);
        t4.setPriority(4);
        t5.setPriority(2);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }

    private static class Worker implements Runnable {
        private int runCount = 1;
        private String threadColour;

        public Worker(String threadColour) {
            this.threadColour = threadColour;
        }

        @Override
        public void run() {
            for (int i = 0; i < 60; i++) {
                lock.lock();
                try {
                    System.out.format(threadColour + "%s: runCount = %d\n", Thread.currentThread().getName(), runCount++);
                    // execute critical section of code
                } finally {
                    lock.unlock();
                } 
            }
        }
    }

}
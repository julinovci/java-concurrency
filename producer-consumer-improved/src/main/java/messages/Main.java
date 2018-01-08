package messages;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import concurrency.ThreadColour;

// Thread interference can be solved using synchonized block.
// It has its drawbacks:
// - threads that are blocked waiting to executre sync block are blocked, they cannot be interrupted
// - synchronized block must be in within the same method
// - can't test if the objects intrinsic lock is available
// - if the lock is not available, we cannot timeout after we have waited for the lock for awhile
// - if multiple threads are waiting to get a lock, it is not first come first serve basis
// Better approach is to use objects that implement java.util.concurrent.locks interface
// 
// Having .lock() and .unlock() calls scattered around the code could get messy, error-prone
// difficult to maintain. Better use try-finally block with locks. If exception is thrown,
// .unlock() will be always called if it is in finally block.
public class Main {

    public static void main(String[] args) {
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<>(6);
        ReentrantLock bufferLock = new ReentrantLock();
        
        // Using ExecutorService in this example is overkill, but it's vital for applications
        // that use a large number of threads. Using it allows JVM to optimize thread management.
        ExecutorService ex = Executors.newFixedThreadPool(5);
        MyProducer pr = new MyProducer(buffer, ThreadColour.ANSI_GREEN);
        MyConsumer c1 = new MyConsumer(buffer, ThreadColour.ANSI_BLACK); 
        MyConsumer c2 = new MyConsumer(buffer, ThreadColour.ANSI_BLUE);

        ex.execute(pr);
        ex.execute(c1);
        ex.execute(c2);

        Future<String> future = ex.submit(new Callable<String>() {
            public String call() throws Exception {
                System.out.println(ThreadColour.ANSI_RED + "Hello from callable class");
                return "This is the callable result";
            }
        });

        try {
            // This blocks until the result is available.
            System.out.println(future.get());
        } catch (ExecutionException e) {
            System.out.println("Something went wrong");
        } catch (InterruptedException e) {
            System.out.println("Got interrupted");
        }
        ex.shutdown();
    }
}

class MyProducer implements Runnable {
    private ArrayBlockingQueue<String> buffer;
    private String colour;

    public MyProducer(ArrayBlockingQueue<String> buffer, String colour) {
        this.buffer = buffer;
        this.colour = colour;
    }

    public void run() {
        Random rand = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num: nums) {
            try {
                System.out.println(colour + "Adding... " + num);
                buffer.put(num);

                Thread.sleep((rand.nextInt(1000)));
            } catch (InterruptedException e) {
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(colour + "Adding EOF and exiting.");
        try {
            buffer.put("EOF");

        } catch (InterruptedException e) {}
    }
}

class MyConsumer implements Runnable {
    private ArrayBlockingQueue<String> buffer;
    private String colour;

    public MyConsumer(ArrayBlockingQueue<String> buffer, String colour) {
        this.buffer = buffer;
        this.colour = colour;
    }

    public void run() {
        while (true) {
            synchronized(buffer) {
                try {
                    if (buffer.isEmpty()) {
                        continue;
                    }
                    if (buffer.peek().equals("EOF")) {
                         System.out.println(colour + "Exiting");
                        // if we remove "EOF", other consumer threads would loop forever
                        break;
                    }
                    else {
                        System.out.println(colour + "Removed " + buffer.take());
                    }
                } catch (InterruptedException e) {}
            }
        }
    }
}
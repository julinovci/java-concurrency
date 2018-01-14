package concurrency;

import java.util.LinkedList;
import concurrency.ThreadColour;

public class Main {

    public static void main(String[] args) {
        LinkedList<String> buffer = new LinkedList<>();
        Object lock = new Object();

        MyProducer pr = new MyProducer(buffer, ThreadColour.ANSI_GREEN, lock);
        MyConsumer c1 = new MyConsumer(buffer, ThreadColour.ANSI_RED, lock); 
        MyConsumer c2 = new MyConsumer(buffer, ThreadColour.ANSI_BLUE, lock);

        new Thread(pr).start();
        new Thread(c1).start();
        new Thread(c2).start();
    }
}

class MyProducer implements Runnable {
    public final int LIMIT = 10;
    private LinkedList<String> buffer;
    private String colour;
    private Object lock;

    public MyProducer(LinkedList<String> buffer, String colour, Object lock) {
        this.buffer = buffer;
        this.colour = colour;
        this.lock = lock;
    }

    public void run() {
        int value = 0;

        while (true) {
            synchronized(lock) {
                // Once the list is filled to its capacity limit
                // wait and let the consumers process the list items
                while (buffer.size() == LIMIT) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {}
                }
                System.out.println(colour + "Adding " + value);
                buffer.add(""+value++);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
                lock.notifyAll();
            }
        }
    }
}

class MyConsumer implements Runnable {
    public final int LIMIT = 10;
    private LinkedList<String> buffer;
    private String colour;
    private Object lock;

    public MyConsumer(LinkedList<String> buffer, String colour, Object lock) {
        this.buffer = buffer;
        this.colour = colour;
        this.lock = lock;
    }

    public void run() {
        while (true) {
            synchronized(lock) {
                while (buffer.size() == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {}
                }
                System.out.println(colour + "List size is " + buffer.size());
                String value = buffer.removeFirst();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {}
                System.out.println("value is " + value);
                lock.notifyAll();
            }
        }
    }
}
package concurrency;

import static concurrency.ThreadColour.ANSI_GREEN;
import static concurrency.ThreadColour.ANSI_CYAN;
import static concurrency.ThreadColour.ANSI_RED;

public class Main {

    public static void main(String[] args) {
        
        System.out.println(ANSI_GREEN + "Hello from the main thread 1");
        
        Thread anotherThread = new AnotherThread();
        anotherThread.start();

        new Thread () {
            public void run() {
                System.out.println(ANSI_CYAN + "Hello from anonymous thread");
            }
        }.start();;

        Thread myRunnableThread = new Thread(new Runnable(){
            @Override
            public void run() {
                System.out.println(ANSI_RED + "Hello from MyRunnable");
            
                try {
                    anotherThread.join(2000);
                    System.out.println(ANSI_RED + "anotherThread got terminated or timed out, so I am running again.");
                } catch (InterruptedException e) {
                    System.out.println(ANSI_RED + "Could not wait, I was interrupted");
                }
            }
        });

        myRunnableThread.start();

        // anotherThread.interrupt();
        

        System.out.println(ANSI_GREEN + "Hello from the main thread 2");
    }

}

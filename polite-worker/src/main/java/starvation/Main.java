package starvation;


public class Main {

    // Live lock example
    public static void main(String[] args) {
        final Worker w1 = new Worker("Worker 1", true);
        final Worker w2 = new Worker("Worker 2", true);
    
        final SharedResource sr = new SharedResource(w1);

        new Thread(new Runnable(){
        
            @Override
            public void run() {
                w1.work(sr, w2);
            }
        }).start();
    
        new Thread(new Runnable(){
        
            @Override
            public void run() {
                w2.work(sr, w1);
            }
        }).start();
    }
}
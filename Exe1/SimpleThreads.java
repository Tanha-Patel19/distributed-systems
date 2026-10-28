package Exe1;

/**
 * Task 1: Simple thread program.
 * Three threads print some lines each. Watch how the output interleaves.
 */
public class SimpleThreads {

    // A thread's job: implement Runnable
    static class Worker implements Runnable {
        private final String name;

        Worker(String name) {
            this.name = name;
        }

        public void run() {
            for (int i = 1; i <= 5; i++) {
                System.out.println(name + ": step " + i);
                // uncomment the next line to see more mixing:
                 try { Thread.sleep(10); } catch (InterruptedException e) { return; }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Worker("Thread A"));
        Thread t2 = new Thread(new Worker("Thread B"));
        Thread t3 = new Thread(new Worker("Thread C"));

        t1.start();   // start(), not run()!
        t2.start();
        t3.start();

        t1.join();    // wait until the thread has terminated
        t2.join();
        t3.join();

        System.out.println("main: all threads finished");
    }
}
/**
 * Task 1: Simple thread program.
 * Starts several threads, each printing to the terminal.
 * Run:  java SimpleThreads.java   (or javac + java SimpleThreads)
 */
package Exe1;
public class SimpleThreads {

    static class Worker implements Runnable {
        private final String name;

        Worker(String name) { this.name = name; }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                System.out.println(name + ": step " + i);
                // Uncomment to see a much more regular interleaving:
                 try { Thread.sleep(10); } catch (InterruptedException e) { return; }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Worker("Thread-" + (char) ('A' + i)));
        }
        for (Thread t : threads) t.start();   // start, do NOT call run()!
        for (Thread t : threads) t.join();    // wait until all are done
        System.out.println("main: all threads finished");
    }
}
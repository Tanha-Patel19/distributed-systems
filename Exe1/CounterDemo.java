/**
 * Task 2: Synchronization.
 * Shows thread interference on an unsynchronized Counter and
 * that a synchronized version fixes it.
 * Run:  java CounterDemo.java
 */
package Exe1;
import java.util.concurrent.CountDownLatch;

public class CounterDemo {

    static final int ITERATIONS = 10_000_000;

    /**
     * One thread increments N times, another decrements N times -> expected result 0.
     * A latch makes both threads start their loops at (almost) the same moment,
     * otherwise the first thread may finish before the second even starts.
     */
    static int run(Counter counter) throws InterruptedException {
        CountDownLatch go = new CountDownLatch(1);
        Thread inc = new Thread(() -> {
            await(go);
            for (int i = 0; i < ITERATIONS; i++) counter.increment();
        });
        Thread dec = new Thread(() -> {
            await(go);
            for (int i = 0; i < ITERATIONS; i++) counter.decrement();
        });
        inc.start();
        dec.start();
        go.countDown();   // release both threads
        inc.join();
        dec.join();
        return counter.value();
    }

    private static void await(CountDownLatch l) {
        try { l.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws InterruptedException {
        final int RUNS = 5;

        System.out.println("Expected result every time: 0\n");

        System.out.println("--- Unsynchronized Counter ---");
        for (int r = 1; r <= RUNS; r++) {
            System.out.println("Run " + r + ": " + run(new Counter()));
        }

        System.out.println("\n--- SynchronizedCounter ---");
        for (int r = 1; r <= RUNS; r++) {
            System.out.println("Run " + r + ": " + run(new SynchronizedCounter()));
        }
    }
}

// Unsynchronized version from the exercise sheet
class Counter {
    private int c = 0;

    public void increment() {
        int b = c;
        b = b + 1;
        c = b;
    }

    public void decrement() {
        int d = c;
        d = d - 1;
        c = d;
    }

    public int value() {
        return c;
    }
}

// Synchronized version: only one thread at a time may be inside any of these methods
class SynchronizedCounter extends Counter {
    @Override public synchronized void increment() { super.increment(); }
    @Override public synchronized void decrement() { super.decrement(); }
    @Override public synchronized int value()      { return super.value(); }
}
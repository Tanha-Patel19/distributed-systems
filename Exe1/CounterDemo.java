package Exe1;

/**
 * Task 2: Synchronization.
 * One thread increments, one decrements the same counter.
 * Both do the same number of steps, so the result should be 0.
 * Shows thread interference on an unsynchronized Counter and
 * that a synchronized version fixes it.
 */
public class CounterDemo {

    static final int ITERATIONS = 10000000;

    // Starts both threads on the given counter, waits for them, returns the result
    static int test(Counter counter) throws InterruptedException {
        Thread inc = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                counter.increment();
            }
        });
        Thread dec = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                counter.decrement();
            }
        });
        inc.start();
        dec.start();
        inc.join();
        dec.join();
        return counter.value();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Correct result would be 0 every time.");

        System.out.println("--- Counter (not synchronized) ---");
        for (int run = 1; run <= 5; run++) {
            System.out.println("Run " + run + ": " + test(new Counter()));
        }

        System.out.println("--- SynchronizedCounter ---");
        for (int run = 1; run <= 5; run++) {
            System.out.println("Run " + run + ": " + test(new SynchronizedCounter()));
        }
    }
}

// Counter exactly as given on the exercise sheet (not synchronized)
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

// Same counter, but synchronized : only one thread at a time inside a method
class SynchronizedCounter extends Counter {
    public synchronized void increment() {
        super.increment();
    }

    public synchronized void decrement() {
        super.decrement();
    }

    public synchronized int value() {
        return super.value();
    }
}
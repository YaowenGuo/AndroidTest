package atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestOptimisticLocking {
    public static void main(String[] args) {
        AtomicLong atomicInteger = new AtomicLong();
        for (int i = 0; i < 100000; i++) {
            Thread t = new Thread(new AtomicTest(atomicInteger));
            t.start();
            try {
                t.join(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println(atomicInteger.get());
    }
}


class AtomicTest implements Runnable {
    AtomicLong atomicInteger;


    public AtomicTest(AtomicLong atomicInteger) {
        this.atomicInteger = atomicInteger;
    }


    @Override
    public void run() {
        atomicInteger.addAndGet(1);
        atomicInteger.addAndGet(2);
        atomicInteger.addAndGet(3);
        atomicInteger.addAndGet(4);
    }

}
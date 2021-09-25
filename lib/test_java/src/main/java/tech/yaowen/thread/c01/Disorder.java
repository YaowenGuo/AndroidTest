package tech.yaowen.thread.c01;

import java.util.concurrent.CountDownLatch;

public class Disorder {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;


    public static void main(String[] args) throws InterruptedException {
        for (long i = 0; i < Long.MAX_VALUE; ++i) {
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            CountDownLatch launch = new CountDownLatch(2);
            Thread one = new Thread(() -> {
                a = 1;
                x = b;
                launch.countDown();
            });
            Thread two = new Thread(() -> {
                b = 1;
                y = a;
                launch.countDown();
            });
            one.start();
            two.start();
            launch.await();
            if (x == 0 && y == 0) {
                System.err.println("第" + i + "次 （" + x + ", " + y + ")");
                break;
            }
        }
    }
}

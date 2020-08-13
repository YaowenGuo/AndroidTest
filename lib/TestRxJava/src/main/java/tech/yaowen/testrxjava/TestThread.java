package tech.yaowen.testrxjava;

public class TestThread {

    static void test() {




        final Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    System.out.println("Wait ing");
                    synchronized (this) {
                        wait(1000);
                    }

                    System.out.println("Wait for 1000");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        try {

            Thread.sleep(500);
//            thread.wait();
            thread.notify();
            System.out.println("Main");
//            Thread.sleep(1000);

//            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        thread.notify();
    }

    public static void main(String[] argus) {
        test();
    }
}

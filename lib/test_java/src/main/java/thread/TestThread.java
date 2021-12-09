package thread;

public class TestThread {
    static Integer a = 0;
    public static void main(String[] args) {
        System.out.println("x");
        Thread thread = new Thread(TestThread::test);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void test() {
        a = 1;
    }


    public static void test1() {
        synchronized (TestThread.class) {
            a = 1;
        }
    }

    public static void test2() {
        synchronized (a) {
            a = 1;
        }
    }
}
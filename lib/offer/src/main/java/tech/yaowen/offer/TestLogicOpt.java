package tech.yaowen.offer;

public class TestLogicOpt<T> {
    T t;

    public void setT(T t) {
        this.t = t;
    }

    public T getT() {
        return t;
    }

    public void test(String[] argus) {

        TestLogicOpt<String> testLogicOpt = new TestLogicOpt();
        testLogicOpt.setT("Hello");
        System.out.println(testLogicOpt.getT());
    }

}

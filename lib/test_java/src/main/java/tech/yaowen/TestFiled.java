package tech.yaowen;

class A {
    public static String filed = "A";

    protected void setFiled(String value) {
        filed = value;
        callM();
    }

    void callM() {
        System.out.println("callM value: A");
    }

    public String getFiled() {
        return filed;
    }

    public static void main(String[] args) {
        A b = new B();
        System.out.println("filed value: " + b.getFiled());
        b.setFiled("C");
        System.out.println("filed value: " + b.getFiled());

        final A a = new A();
        a.filed = "hhh";

    }

}

class B extends A {
    private String filed = "B";

    void callM() {
        System.out.println("callM value: B");
    }

//    public void setFiled(String value) {
//        filed = value;
//        callM();
//    }

//    public String getFiled() {
//        return filed;
//    }

}

public class TestFiled {


}

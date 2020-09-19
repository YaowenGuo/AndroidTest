package tech.yaowen;

class A {
    private String filed = "A";

    public void setFiled(String value) {
        filed = value;
    }

    public String getFiled() {
        return filed;
    }
}

class B extends A {
    private String filed = "B";

}

public class TestFiled {

    public static void main(String[] args) {
        A b = new B();
        System.out.println("filed value: " + b.getFiled());
        b.setFiled("C");
        System.out.println("filed value: " + b.getFiled());
    }

}

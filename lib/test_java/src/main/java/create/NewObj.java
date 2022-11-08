package create;

public class NewObj {
    static Object obj = new Object();
    public static void main(String[] args) {
        System.out.println("obj: " + obj.getClass().getName());
    }
}

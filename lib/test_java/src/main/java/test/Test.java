package test;

import java.util.Arrays;

public class Test<T extends Arrays> {
    public static void main(String[] argus) throws ClassNotFoundException {
        // returns the Class object for this class
        Class myClass = Test.class;

        System.out.println("Class represented by myClass: "
                + myClass.toString());

        // Get the type parameters of myClass
        // using getTypeParameters() method
        System.out.println(
                "TypeParameters of myClass: "
                        + Arrays.toString(
                        myClass.getTypeParameters()));

    }
}

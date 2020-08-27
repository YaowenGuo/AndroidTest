package typeinfo;

import java.lang.reflect.Field;

import static net.mindview.util.Print.print;

class A {
    int a;
}

class B1 extends A {
    int b;
}

class C extends B1 {
    Object obj = new Object();
}

public class SuperClassTest {

    public static void printParents(Object o) {
        if (o == null) return;
        Class parent;
        if (o instanceof Class) {
            parent = ((Class) o).getSuperclass();
        } else {
            parent = o.getClass().getSuperclass();
        }
        if (parent != null) {
            print(parent.getName());
            printParents(parent);
        }
    }

    public static void printAllField(Object o) {
        if (o == null) return;
        Class type;
        if (o instanceof Class) {
            type = (Class) o;
        } else {
            type = o.getClass();
        }

        Field[] fields = type.getDeclaredFields();
        for (Field field: fields) {
            print(field.getName());
        }
        printAllField(type.getSuperclass());
    }

    public static void main(String[] args) {
//        C c = new C();
//        printAllField(c);

        char[] chars = {'q', 'b', 'c'};

        char a = 'a';
        Class type = chars.getClass();
        print("char array class: " + (chars instanceof Object));
    }
}

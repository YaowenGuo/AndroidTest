package tech.yaowen.test_reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TestReflection {

    public static void main(String[] argus) {
        try {
            Class myClass = Class.forName("tech.yaowen.test_reflection.Utils");
            myClass.getConstructors();
            Constructor constructor = myClass.getConstructors()[0];
            constructor.setAccessible(true);
            Object object = constructor.newInstance();
            System.out.println(object);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}

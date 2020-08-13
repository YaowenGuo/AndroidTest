package tech.yaowen.test_annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Binding {
    // 运行时反射，获取变量赋值。
    public static void reflectionAtRunning( Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            ButterKnife bind = field.getAnnotation(ButterKnife.class);
            if (bind != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, bind.value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    // 编译前反射，生成代码。
    public static void bind( Object obj) {
        try {
            Class bindClass = Class.forName(obj.getClass().getCanonicalName() + "Binding");
            Constructor constructor = bindClass.getDeclaredConstructor(obj.getClass());
            constructor.newInstance(obj);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
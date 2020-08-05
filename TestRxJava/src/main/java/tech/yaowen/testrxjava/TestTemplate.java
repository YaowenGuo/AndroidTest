package tech.yaowen.testrxjava;

import java.util.ArrayList;

class Fruit {
}

class Apple extends Fruit {

}

class Banana extends Fruit {

}

class Shop<T extends Fruit> {
    ArrayList<T> fruit = new ArrayList<>();

    public void add(T fruit) {

    }

    public T get() {
        if (fruit.size() > 0) {
            return fruit.get(fruit.size() - 1);
        } else {
            return null;
        }
    }
}

class Shop1<T extends Fruit> {
    ArrayList<T> fruit = new ArrayList<>();

    public void add(T fruit) {

    }

    public T get() {
        if (fruit.size() > 0) {
            return fruit.get(fruit.size() - 1);
        } else {
            return null;
        }
    }
}

public class TestTemplate {
    public static void main(String[] argus) {
        ArrayList<Apple> apples = new ArrayList<>();
        apples.add(new Apple());

        ArrayList<? super Apple> applyList = new ArrayList<Fruit>();


        ArrayList<? extends Fruit> fruitList = apples;

        Fruit a = fruitList.get(0);
    }

    public static <T extends Fruit> void wait(T fruit) {

    }

    public static void totalWeight(ArrayList<? extends Fruit> fruits) {
        for (Fruit fruit : fruits) {
        }
    }
 }


package tech.yaowen.offer;/*
package tech.yaowen.offer;

public class DictionaryOrderNumber {

    public int findNthDictionaryNumber(int max, int order) {
        if (max < 1 || order < 0 || order > max) {
            return -1;
        }

        char[] numArr = Integer.toString(max).toCharArray();
        return -1;
    }

    protected int increament(char[] charArr, int at, int num, int find) {
        for (int i = 0; i < 10; ++i) {
            charArr[at] = (char) ('0' + i);
//            if ()
            if (at < charArr.length) {
                increament(charArr, at + 1, num, find);
            }

        }
        return -1;
    }

    public static void main(String[] argus) {

    }
}
*/



import java.util.ArrayList;
import java.util.Scanner;



class Test<T> {
     T[] value;

     Test(T[] values) {

    }

    public static void test() {
        int a = 3;
        Integer b = 100;
        Integer c = 100;
        String text1 = "java";
        String text2 = new String("python");
        int[] arr1 = new int[]{4};
        change(a, b, c,  text1, text2, arr1);
        System.out.println(a);
        System.out.println(b == c);
        System.out.println(b.equals(c));
        System.out.println(text1);
        System.out.println(text2);
        System.out.println(arr1[0]);
    }

    public static void change(int a, Integer b, Integer c,  String s1, String s2, int[] arr1) {
        a = 9;
        s1 = "android";
        s2 = String.valueOf("go");
        arr1 = new int[1];
        arr1[0] = 9;
    }


/*    public static int findKthNumber(int n, int k) {
        if (n < 1 || k < 1 || k > n) {
            return -1;
        }

        int curr = 1;
        k = k - 1;
        while (k > 0) {
            long steps = 0, first = curr, last = curr + 1;
            while (first <= n) {
                steps += Math.min((long)n + 1, last) - first;
                first *= 10;
                last *= 10;
            }
            if (steps <= k) {
                curr += 1;
                k -= steps;
            } else {
                curr *= 10;
                k -= 1;
            }
        }
        return curr;
    }*/

    public static int findKthNumber(int n, int k) {
        if (k < 1 || k > n) {
            return -1;
        }

        int[] order = {1};
        return findNextLevel(0, 1, n, order, k);
    }

    public static int findNextLevel(int base, int inc, int max, int[] order, int find) {
        for (; inc < 10; ++inc) {
            int value = base + inc;

            if (value > max) {
                return -1; // 没找到
            } else {

                if (order[0] == find) {
                    return value;
                } else {
                    if (base * 10 <=  max ) {
                        ++order[0];
                        int result = findNextLevel(value * 10, 0, max, order, find);
                        if (result > 0) {
                            return result;
                        }
                    }


                }
            }
            ++order[0];
        }
        return -1;
    }


    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int n = sc.nextInt();
//        int k = sc.nextInt();
//        sc.nextByte();
//        int value = findKthNumber(n, k);
//        if (value > 0) {
//            System.out.println(value);
//        } else {
//            System.out.println();
//        }

        test();
    }
}
package tech.yaowen.offer

import java.util.*


object Main {
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

    fun findKthNumber(n: Int, k: Int): Int {
        return if (k < 1 || k > n) {
            -1
        } else {
            val order = intArrayOf(0)
            findNextLevel(0, 1, n, order, k)
        }
    }

    fun findNextLevel(base: Int, inc: Int, max: Int, order: IntArray, find: Int): Int {
        var base = base
        var inc = inc
        var order = order
        while (inc < 10) {
            val value = base + inc
            if (value > max) {
                return -1 // 没找到
            } else {
                ++order[0]
                if (order[0] == find) {
                    return value
                } else {
                    val result = findNextLevel(value * 10, 0, max, order, find)
                    if (result > 0) {
                        return result
                    }
                }
            }
            ++inc
        }
        return -1
    }


}

fun main(args: Array<String>) {
    val sc = Scanner(System.`in`)
    val n = sc.nextInt()
    val k = sc.nextInt()
    val value = Main.findKthNumber(n, k)
    if (value > 0) {
        println(value)
    } else {
        println()
    }
}
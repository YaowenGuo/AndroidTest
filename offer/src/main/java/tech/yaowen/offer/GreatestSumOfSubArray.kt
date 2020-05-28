package tech.yaowen.offer

import java.lang.Exception


fun greatestSumOfSubArray(values: IntArray, interval: IntArray): Long {
    if (values.isEmpty()) throw Exception("Input is empty!")

    var tempSum = 0L
    var greatestSum = Long.MIN_VALUE
    var start = 0
    var end = 0

    for (i in values.indices) {
        if (tempSum <= 0) {
            tempSum = values[i].toLong()
            start = i
            end = i
        } else {
            tempSum += values[i].toLong()
        }

        if (tempSum > greatestSum) {
            greatestSum = tempSum
            end = i
        }
    }

    interval[0] = start
    interval[1] = end
    return greatestSum
}


fun main() {
    val values = intArrayOf(1, -2, 3, 10, -4, 7, 2, -5)
    val interval = intArrayOf(0, 0)
    val greatestSubArraySum = greatestSumOfSubArray(values, interval)
    println(greatestSubArraySum)
    for (i in interval) {
        print("$i, ")
    }
    println()
}
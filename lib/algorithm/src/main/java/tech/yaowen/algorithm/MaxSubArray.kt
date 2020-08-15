package tech.yaowen.algorithm;

fun maxSubArray(array: IntArray): IntArray? {
    if (array.isEmpty()) {
        return null
    }

    var maxSum = Int.MIN_VALUE
    var havePositiveNum  = false
    for (i in array.indices) {
        if (array[i] > 0) {
            havePositiveNum = true
            break
        } else if (array[i] > maxSum) {
            maxSum = array[i]
        }
    }

    if (!havePositiveNum) {
        return intArrayOf(maxSum)
    }

    var tempSum = array[0]
    maxSum = array[0]

    var maxStart = 0
    var maxEnd = 0
    var tempStart = 0
    for (i in 1 until array.size) {
        if (tempSum < 0) {
            tempStart = i
            tempSum = 0
        }
        tempSum += array[i]

        if (tempSum > maxSum) {
            if (tempStart != maxStart) {
                maxStart = tempStart
            }
            maxEnd = i
        }
    }

    return  IntArray(maxEnd - maxStart + 1) {
        array[maxStart + it]
    }
}

fun main()  {
    val array = intArrayOf(2, -3, 9, 7, -4, 10)

    val subArr = maxSubArray(array)

    if (subArr != null) {
        for (value in subArr) {
            print("$value, ")
        }
        println()
    }
}
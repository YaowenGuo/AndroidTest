package tech.yaowen.algorithm


fun inversePares(values: IntArray): Int {
    if (values.size < 2) return 0
    val copyValues = values.clone()

    return inversePareNum(values, copyValues, 0, values.size - 1)
}

fun inversePareNum(inArray: IntArray, toArray: IntArray, start: Int, end: Int): Int {
    return if (start == end) {
        toArray[start] = inArray[start]
        0
    } else {
        val length = (end - start) / 2

        val leftPares = inversePareNum(toArray, inArray, start, start + length)
        val rightPares = inversePareNum(toArray, inArray, start + length + 1, end)

        val reverseNum = inverseTares(toArray, inArray, start, start + length, end)

        leftPares + rightPares + reverseNum
    }
}

fun inverseTares(inArray: IntArray, toArray: IntArray, l: Int, divider: Int, h: Int): Int {
    var start1 = l
    var start2 = divider + 1
    var merge = l

    var inversePares = 0
    while (start1 <= divider && start2 <= h) {
        if (inArray[start1] > inArray[start2]) {
            inversePares += h - start2 + 1
            toArray[merge++] = inArray[start2++]
        } else {
            toArray[merge++] = inArray[start1++]
        }
    }

    while (start1 <= divider) toArray[merge++] = inArray[start1++]

    while (start2 <= h) toArray[merge++] = inArray[start2++]
    return inversePares
}


fun main() {
    val values = intArrayOf(7, 5, 6, 4)
    val inversePares = inversePares(values)

    for (value in values) {
        print("$value, ")
    }

    println()

    println(inversePares)
}
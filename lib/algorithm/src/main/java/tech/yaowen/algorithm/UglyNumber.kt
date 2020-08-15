package tech.yaowen.algorithm


fun getUglyNumber(size: Int): Int {
    if (size <= 0) return 0

    val intArray = IntArray(size)
    intArray[0] = 1

    var multiply2Index = 0
    var multiply3Index = 0
    var multiply5Index = 0
    var nextUglyIndex = 1
    while (nextUglyIndex < intArray.size) {
        intArray[nextUglyIndex] = min(multiply2Index * 2, multiply3Index * 3, multiply5Index * 5)

        while (intArray[multiply2Index] * 2 <= intArray[nextUglyIndex]) ++multiply2Index

        while (intArray[multiply3Index] * 3 <= intArray[nextUglyIndex]) ++multiply3Index

        while (intArray[multiply5Index] * 5 <= intArray[nextUglyIndex]) ++multiply5Index
        ++nextUglyIndex
    }
    return intArray.last()
}

fun min(a: Int, b: Int, c: Int): Int {
    var min = if (a < b)  a else b
    min = if (min < c) min else c
    return min
}
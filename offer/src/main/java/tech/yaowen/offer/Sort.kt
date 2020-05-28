package tech.yaowen.offer


fun quickSort(intArray: IntArray, low: Int, high: Int) {
    if (low in 0 until high && high < intArray.size) {
        val divider = partition(intArray, low, high)
        quickSort(intArray, low, divider - 1)
        quickSort(intArray, divider + 1, high)
    }
}

fun partition(values: IntArray, start: Int, end: Int): Int {
    val divide = values[start]
    var low = start
    var high = end
    while (low < high) {
        while (low < high && values[high] >= divide) ++high
        values[low] = values[high]
        while (low < high && values[low] <= divide) ++low
        values[high] = values[low]
    }
    values[low] = divide
    return low
}


package tech.yaowen.algorithm

import java.util.*

fun quickSortRecursive(intArray: IntArray, low: Int, high: Int) {
    if (low in 0 until high && high < intArray.size) {
        val divider = partition(intArray, low, high)
        quickSortRecursive(intArray, low, divider - 1)
        quickSortRecursive(intArray, divider + 1, high)
    }
}

fun quickSort(array: IntArray) {
    if (array.isEmpty()) return

    val stack = LinkedList<IntArray>()
    stack.push(intArrayOf(0, array.size - 1))
    while (!stack.isEmpty()) {
        val range = stack.pop()
        val divider = partition(array, range[0], range[1])
        if (divider + 1 < range[1]) {
            stack.add(intArrayOf(divider + 1, range[1]))
        }
        if (divider - 1 > range[0]) {
            stack.add(intArrayOf(range[0], divider - 1))
        }
    }
}


fun partition(values: IntArray, start: Int, end: Int): Int {
    val divide = values[start]
    var low = start
    var high = end
    while (low < high) {
        while (low < high && values[high] >= divide) --high
        values[low] = values[high]
        while (low < high && values[low] <= divide) ++low
        values[high] = values[low]
    }
    values[low] = divide
    return low
}

fun mergeSort(inArray: IntArray) {
    if (inArray.isEmpty()) return
    val temp = IntArray(inArray.size) { index -> inArray[index] }
    mergeSort(inArray, temp, 0, inArray.size - 1)
}

fun mergeSort(inArray: IntArray, mergeArray: IntArray, l: Int, h: Int) {
    if (l == h) {
        mergeArray[l] = inArray[l]
    } else {
        val divider = l + (h - l) / 2
        mergeSort(mergeArray, inArray, l, divider)
        mergeSort(mergeArray, inArray, divider + 1, h)
        merge(mergeArray, inArray, l, divider, h)
    }
}

fun merge(inArray: IntArray, toArray: IntArray, l: Int, divider: Int, h: Int) {
    var start1 = l
    var start2 = divider + 1
    var merge = l

    while (start1 <= divider && start2 <= h) {
        if (inArray[start1] > inArray[start2]) {
            toArray[merge++] = inArray[start2++]
        } else {
            toArray[merge++] = inArray[start1++]
        }
    }

    while (start1 <= divider) toArray[merge++] = inArray[start1++]

    while (start2 <= h) toArray[merge++] = inArray[start2++]
}


fun bubbleSort(array: IntArray) {
    if (array.isEmpty()) return
    var i = 0
    while (i in 0 .. array.size - 2) {
        var swapped = false
        var lastSwapIndex = array.size - 1
        for (j in 0 until  lastSwapIndex) {
            if (array[j] > array[j+1]) {
                swapped = true
                val temp = array[j];
                array[j] = array[j+1]
                array[j+1] = temp
                lastSwapIndex = j
            }
        }
        if (!swapped) {
            break
        } else {
            i = lastSwapIndex
        }
    }
}

fun insertionSort(array: IntArray) {
    var temp = 0
    for (i in 1 until array.size) {
        temp = array[i]
        var j = i - 1
        while (j >= 0 && array[j] > temp) {
            array[j + 1] = array[j]
            --j
        }
        array[j + 1] = temp
    }
}


fun selectionSort(array: IntArray) {
    for (i in 0 until array.size - 1) {
        var subMin = i
        for (j in i until array.size) {
            if (array[j] < array[subMin]) {
                subMin = j
            }
        }
        if (i != subMin) {
            val temp = array[i]
            array[i] = array[subMin]
            array[subMin] = temp
        }
    }
}


fun main() {

//    val value1 = Scanner(System.`in`).nextInt()
    val testData = intArrayOf(2, 5, 1, 18, 3, 9, 0)

    selectionSort(testData)

    for (date in testData) {
        print("$date, ")
    }
    println()
}




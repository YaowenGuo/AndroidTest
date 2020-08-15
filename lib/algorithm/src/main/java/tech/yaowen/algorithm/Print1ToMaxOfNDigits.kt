package tech.yaowen.algorithm

import java.lang.Exception


@ExperimentalUnsignedTypes
fun print1ToMaxOfNDigits(n: UInt) {
    if (n <= 0u) throw Exception("Illegal Input")

    val numberStr = CharArray(n.toInt()) { '0' }

    while (!increment(numberStr)) {
        printNum(numberStr)
    }
}

fun printNum(numberStr: CharArray) {
    var isBeginning0 = true
    for (i in numberStr.indices) {
        if (isBeginning0 && numberStr[i] != '0') isBeginning0 = false

        if (!isBeginning0) {
            print(numberStr[i])
        }
    }
    print('\n')
}

fun increment(numberStr: CharArray): Boolean {
    var isOverflow = false
    val takeOver = 1
    var sum = 0
    for (i in (numberStr.size - 1) downTo 0) {
        sum = numberStr[i] - '0' + takeOver
        if (sum >= 10) {
            if (i == 0) {
                isOverflow = true
            } else {
                sum -= 10
                numberStr[i] = '0' + sum
            }
        } else {
            numberStr[i] = '0' + sum
            break
        }
    }

    return isOverflow
}

@ExperimentalUnsignedTypes
fun print1ToMaxOfNDigits1(n: UInt) {
    if (n <= 0u) throw Exception("Illegal Input")

    val numberStr = CharArray(n.toInt()) { '0' }
    printToMaxOfNDigitsRecursively(numberStr, 0u)
}

@ExperimentalUnsignedTypes
fun printToMaxOfNDigitsRecursively(numberStr: CharArray, index: UInt) {
    for (i in 0 .. 9) {
        numberStr[index.toInt()] = '0' + i
        if (index == (numberStr.size - 1).toUInt()) {
            printNum(numberStr)
        } else {
            printToMaxOfNDigitsRecursively(numberStr, index + 1u)
        }
    }
}


fun main() {
    print1ToMaxOfNDigits1(2u)
}


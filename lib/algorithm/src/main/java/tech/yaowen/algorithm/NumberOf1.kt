package tech.yaowen.algorithm


// 右移位算法，防止负数移位高位一直置 1
@ExperimentalUnsignedTypes
fun numberOf1(number: Int): UInt {
    var num = number
    var numberOf1 = 0u
    while (num != 0) {
//        println((num.toUInt()).toString(2))
        if ((num and 1) != 0)
            numberOf1 ++

        num = num ushr 1
    }
    return numberOf1
}


/**
 * 左移位计算，需要使用单独的标志位变量。移位单独的标志位。
 */
@ExperimentalUnsignedTypes
fun numberOf1SHL(num: Int):UInt {
    var numberOf1 = 0u
    var flag = 1
    while (flag != 0) {
        if ((num and flag) != 0) numberOf1 ++

        flag = flag shl 1
    }
    return numberOf1
}


@ExperimentalUnsignedTypes
fun numberOf1Minus(num: Int): UInt {
    var numberOf1 = 0u
    var number = num
    while (number != 0) {
        numberOf1++
        number = number and (number - 1)
    }
    return numberOf1
}

@ExperimentalUnsignedTypes
fun main() {
//    println((0xFFFFFFFF.toInt()).toString(16))
    println(numberOf1SHL(0x444400))
    println(numberOf1SHL(0xFFFFFFFF.toInt()))
    println(numberOf1SHL(1))
}
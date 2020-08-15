package tech.yaowen.algorithm


@ExperimentalUnsignedTypes
fun fibonacci(n: UInt): UInt {
    val startValue = uintArrayOf(0u, 1u)
    if (n < 2u) {
        return startValue[n.toInt()]
    }
    var preValue1 = 0u
    var preValue2 = 1u
    var value = 0u
    for (i in 2..n.toInt()) {
        value = preValue1 + preValue2
        preValue1 = preValue2
        preValue2 = value
    }
    return value
}


fun main() {
    for (i in 0..10) {
        println(fibonacci(i.toUInt()))
    }
}
package tech.yaowen.offer


@ExperimentalUnsignedTypes
fun numberOf1ByDecimal(max: UInt): UInt {
    var decimalBase = 1u
    var numOf1 = 0u
    var multi10Times = 0u
    while (decimalBase <= max) {
        val remainder = max % decimalBase
        val highestNum = max / decimalBase % 10u


        if (highestNum > 1u) { // 最高位大于 1
            numOf1 += decimalBase
        } else if (highestNum == 1u) {
            numOf1 += (remainder + 1u) // 最高位等于 1
        }

        if (decimalBase >= 10u) {

            numOf1 += highestNum * multi10Times * (decimalBase / 10u)
        }

        decimalBase *= 10u
        ++multi10Times
    }
    return numOf1
}

@ExperimentalUnsignedTypes
fun numOf1ByDecimal(max: UInt): UInt {

    var decimalBase = 1u
    var numOf1 = 0u
    var multi10Times = 0u

    val str = max.toString().toCharArray()
    var remainder = max
    for (index in str.indices) {
        remainder %= 10u
        if (str[index] > '1') {
            numOf1 += power(10, str.size - (index + 1)).toInt().toUInt()
        } else if (str[index] == '1') {
            numOf1 += remainder + 1u
        }

        if (index <= str.size - 2) {
            numOf1 += ((str[index] - '0') * (str.size - index - 1) * power(10, str.size - (index + 2))).toInt().toUInt()
        }
    }
    return numOf1
}


fun main() {
    println(numOf1ByDecimal(5u))

    println(numOf1ByDecimal(10u))

    println(numOf1ByDecimal(55u))

    println(numOf1ByDecimal(99u))

    println(numOf1ByDecimal(0u))

    println(numOf1ByDecimal(1u))

    println(numOf1ByDecimal(10000u))
    println(numOf1ByDecimal(21235u))


}
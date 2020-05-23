package tech.yaowen.offer

import kotlin.Exception


fun power(base: Int, exponent: Int): Double {
    if ((base - 0.0) < 0.000001 && exponent < 0) {
        throw Exception("Illegal Input")
    }

    var exponentAbs = if (exponent < 0) {
        (-exponent).toUInt()
    } else {
        exponent.toUInt()
    }

    return if (exponent < 0 ) {
        1 / powerOfUnsignedExponent(base, exponentAbs)
    } else {
        powerOfUnsignedExponent(base, exponentAbs)
    }
}

@ExperimentalUnsignedTypes
fun powerOfUnsignedExponent(base: Int, exponent: UInt): Double {
    if (base == 0) return 0.0
    if (exponent == 0u) return 1.0
    if (exponent == 1u) return base.toDouble()

    var result = powerOfUnsignedExponent(base, exponent shr 1)
    result *= result

    if ((exponent and 1u) != 0u) {
        result *= base
    }
    return result
}




package tech.yaowen.offer


@ExperimentalUnsignedTypes
fun concatenateMinNum(numArray: UIntArray): String {
    if (numArray.isEmpty()) return ""

    val numStrArray = Array<CharArray>(numArray.size) {
        numArray[it].toString().toCharArray()
    }

    val c1 = Comparator<CharArray> { str1, str2 ->
        val str1Start = str1 + str2
        val str2Start = str2 + str1
        for (i in str1Start.indices) {
            if (str1Start[i] != str2Start[i]) {
                return@Comparator str1Start[i] - str2Start[i]
            }

        }
        return@Comparator 0

    }
    numStrArray.sortWith(c1)

    val stringBuffer = StringBuffer()
    for (charArr in numStrArray) {
        stringBuffer.append(charArr)
    }
    return stringBuffer.toString()
}


fun main() {
    println(concatenateMinNum(uintArrayOf(3u, 32u, 321u)))
}
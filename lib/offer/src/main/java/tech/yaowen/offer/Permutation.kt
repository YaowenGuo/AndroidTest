package tech.yaowen.offer



fun permutation(charArray: CharArray, begin: Int = 0) {

    if (charArray.isEmpty() || begin < 0 || begin > charArray.size) return
    if (begin == charArray.size) {
        println(charArray)
    } else {
        for (i in begin until charArray.size) {
            val temp = charArray[begin]
            charArray[begin] = charArray[i]
            charArray[i] = temp
            permutation(charArray, begin + 1)

            charArray[i] = charArray[begin]
            charArray[begin] = temp

        }
    }
}


fun permutationList(charArray: CharArray) {
    if (charArray.isEmpty()) return
    var fromIndex: Int
    var endIndex: Int
    var changeIndex: Int
    charArray.sort()
    do {
        // 输出一种全排列
        println(charArray)
        endIndex = charArray.size - 1
        fromIndex = endIndex
        // 向前查找第一个变小的元素
        while (fromIndex > 0 && charArray[fromIndex] < charArray[fromIndex - 1]) --fromIndex
        changeIndex = fromIndex
        if (fromIndex == 0) break
        // 向后查找最后一个大于words[fromIndex-1]的元素
        while (changeIndex + 1 < charArray.size && charArray[changeIndex + 1] > charArray[fromIndex - 1]) ++changeIndex
        charArray.swap(fromIndex - 1, changeIndex) // 交换两个值
        charArray.invert(fromIndex, endIndex) // 对后面的所有值进行反向处理
    } while (true)
}

fun CharArray.swap(indexA: Int, indexB: Int): Boolean {
    if (indexA < 0 || indexB < 0 || indexA >= this.size || indexB >= this.size
        || indexA == indexB) return false

    val temp = this[indexA]
    this[indexA] = this[indexB]
    this[indexB] = temp
    return true
}

fun CharArray.invert(indexA: Int, indexB: Int): Boolean {
    if (indexA < 0 || indexB < 0 || indexA >= this.size || indexB >= this.size
        || indexA == indexB) return false

    var stat = indexA
    var end = indexB
    while (stat < end) {
        this.swap(stat, end)
        ++stat
        ++end
    }
    return true
}


fun main() {
    val charArray = charArrayOf('a', 'b', 'c')
//    permutation(charArray)
//    print(charArray.slice(2 .. 3))
    permutationList(charArray)
}
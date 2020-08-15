package tech.yaowen.algorithm

import java.util.*


fun <T> isStackPopOrder(
    pushOrder: Array<T>?,
    popOrder: Array<T>?,
    equal: (T, T) -> Boolean
): Boolean {
    if (pushOrder == null || popOrder == null || pushOrder.isEmpty() || popOrder.isEmpty()) {
        return false
    }

    val stack = Stack<T>()
    var nextPop = 0
    var nextPosh = 0
    while (nextPosh < pushOrder.size || nextPop < popOrder.size) {
        if (((stack.size == 0 && nextPop < pushOrder.size))
            || (stack.size > 0 && !equal(stack.last(), popOrder[nextPop]) && nextPosh < pushOrder.size)) {
            stack.push(pushOrder[nextPosh])
            nextPosh++
        } else if (equal(stack.last(), popOrder[nextPop])){
            stack.pop()
            nextPop++
        } else {
            break
        }

    }

    return nextPop == popOrder.size
}


fun main() {
    val pushOrder = Array(5) { it + 1}
    val popOrder = intArrayOf(4, 5, 3, 2, 1).toTypedArray()
    val popOrder2 = intArrayOf(4, 3, 5, 1, 2).toTypedArray()

    println(isStackPopOrder(pushOrder, popOrder) { a: Int, b: Int -> a == b})

    println(isStackPopOrder(pushOrder, popOrder2) { a: Int, b: Int -> a == b})

    println(isStackPopOrder(pushOrder, Array<Int>(0){ it }) { a: Int, b: Int -> a == b})

    println(isStackPopOrder(null, popOrder) { a: Int, b: Int -> a == b})



}
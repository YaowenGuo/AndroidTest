package tech.yaowen.algorithm

fun main () {
    print("0X1230".replace("^0x".toRegex(), "").toIntOrNull())
}
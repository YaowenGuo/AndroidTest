package com.example.test_retrofit

fun main() {
    val num: Any = 20
    check(num)
}

fun <T> check(num: T) {
    when(num) {
        is Int -> println(num)
        is Float -> {
            println("float$num")
        }
    }
}
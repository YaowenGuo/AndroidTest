package com.example.rust

class Main {
    companion object {
        val con = System.loadLibrary("rust")

    }

    external fun hello(to: String): String
}
// 直接运行找不到，原先 grovery 的编译就有问题。直接运行是在电脑系统目录下找，找不到。
fun main() {
    Main().hello("word!")
}
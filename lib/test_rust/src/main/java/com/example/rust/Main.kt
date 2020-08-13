package com.example.rust

class Main {
    companion object {
        val con = System.loadLibrary("rust")

    }

    external fun hello(to: String): String
}

fun main() {
    Main().hello("word!")
}
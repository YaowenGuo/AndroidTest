package tech.yaowen.testio

import okio.*
import java.io.BufferedWriter
import java.io.File


fun main() {

//    FileOutputStream("./TestIO.txt").use {
//            outputStream -> outputStream.write("hello".toByteArray())
//    }

/*    FileInputStream("TestIO.txt").use {
        val reader = InputStreamReader(it)
        val buffered = BufferedReader(reader)
        println(buffered.readLine())
    }*/

    val file = File("TestIO.txt")
    val source = file.source()
        .buffer()
    source.use {
        println(source.readUtf8())
    }

    val buffer = Buffer()
//    val writer = buffer.outputStream().bufferedWriter()
//    writer.write("hhhhhhhhh")
//    writer.flush()
    buffer.writeUtf8("hhhhhhhhh")

    println(buffer.readUtf8())



}

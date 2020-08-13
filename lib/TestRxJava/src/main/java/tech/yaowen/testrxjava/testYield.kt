package tech.yaowen.testrxjava

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.TimeUnit


fun main() {
    val job = CoroutineScope(Dispatchers.Default)
    val channel = Channel<Int>()
    job.launch {
//        delay(1000)
        for (i in 0 .. Int.MAX_VALUE) {

            if (i % 1000000 == 0) {
                println("Thread in cor1: ${Thread.currentThread()}")
            }

            if (i == Int.MAX_VALUE) {
                println("Max int malue = $i")
            }
        }

        println("Thread in main: ${Thread.currentThread()}")

//        while(!channel.isClosedForSend) {
//            val value = channel.receive()
//            println("receive: $value")
//        }
    }


//    job.launch {
//        println("Thread in cor2: ${Thread.currentThread()}")
//        for (x in 0..2) {
//            println("send $x")
//            channel.send(x)
//        }
//        channel.close()
//    }
}
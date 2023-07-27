package tech.yaowen.testrxjava

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
  println("thread: ${Thread.currentThread().name}")
  val rest1 = async(Dispatchers.IO) { doRequest1() }
  println("rest1")
  val rest2 = async(Dispatchers.IO) { doRequest2() }
  println("rest2")
  println("xxxx ${rest1.await()}, ${rest2.await()}")
}

suspend fun doRequest1(): String {
    println("thread: ${Thread.currentThread().name}")
    delay(2000)
    return "resp1"
}

suspend fun doRequest2(): String {
    println("thread: ${Thread.currentThread().name}")
    delay(2000)
    return "resp2"
}
// fun main() = runBlocking {
//    val result = CoroutineScope(Dispatchers.Unconfined).async { "Hello" }
//    println("result: ${result.await()}")
// }



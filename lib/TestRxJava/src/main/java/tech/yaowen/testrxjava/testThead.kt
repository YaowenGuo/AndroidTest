package tech.yaowen.testrxjava

import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantReadWriteLock

val readWriterLock = ReentrantReadWriteLock()
val readLock = readWriterLock.readLock()
val writeLock = readWriterLock.writeLock()

var x = 0

fun testRead() {
    readLock.lock()
    try {
        println(x)
    } finally {
        readLock.unlock()
    }
}

fun testWrite() {
    writeLock.lock()
    try {
        x++
    } finally {
        writeLock.unlock()
    }
}


fun main() {
    testJoin()
}


fun testInterrupt() {
    val th = object : Thread() {
        override fun run() {
            if (isInterrupted) {
                println(isInterrupted)
            }

            if (Thread.interrupted()) {
                println(Thread.interrupted())
            }

            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                // 正在睡的时候，执行了 `interrupt` 将会直接被激活，然后抛出 `InterruptedException`
            }


        }
    }

    th.start()
    th.interrupt()
}

fun testOverrideRunAndRunnable() {
    val thread1 = object : Thread() {
        override fun run() {
            println("Override run")
        }
    }

    val runnable = Runnable { println("Runnable") }

    val thread2 = object : Thread(runnable) {
        var target = runnable
        override fun run() {
            println("Override run")
            target.run()
        }
    }

    thread2.start()
}

fun testJoin() {
    val thread2 = Thread(Runnable {
        for (i in 0..10) {
            println("Thread2")
        }
    })

    val runnable1 = Runnable {
        for (i in 0..10) {
            if (i == 5) thread2.join()
            println("Thread1")
        }
    }

    val thread1 = Thread(runnable1)


    val thread3 = Thread(Runnable {
        for (i in 0..10) {
            println("Thread3")
        }
    })

    thread1.start()
    thread2.start()
    thread3.start()
}
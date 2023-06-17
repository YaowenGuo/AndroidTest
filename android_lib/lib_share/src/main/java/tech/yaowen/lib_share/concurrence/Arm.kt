package tech.yaowen.lib_share.concurrence

object Arm {
    init {
        System.loadLibrary("arm")
    }

    external fun testCAS()

    external fun testHWASan()
}
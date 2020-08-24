package tech.yaowen.lifecycle.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.Closeable

class MainViewModel : ViewModel() {
    init {
        setTagIfAbsent("sts", object: Closeable {
            override fun close() {
                Log.e("sts", "cleared")
            }
        })
    }
}

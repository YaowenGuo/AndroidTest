package tech.yaowen.content

import android.content.Context
import android.os.*
import android.provider.MediaStore
import android.provider.UserDictionary
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        val handler: Handler = Handler()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.e("MainActivity", "onRestore")
    }

    fun testResolver() {
        // Defines a mutable list to contain the selection arguments
        val selectionArgs: MutableList<String> = mutableListOf()
        selectionArgs += "userInput"


        val queryUri = MediaStore.Files.getContentUri("external")
        val rest = UserDictionary.Words.CONTENT_URI




        val thread = object : Thread() {
            init {
                val looper = Looper.prepare()


            }

        }



        val va = ThreadLocal<Boolean>()


    }

    override fun onPause() {
        Log.e("MainActivity", "onPause")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.e("MainActivity", "onSave")
        Log.e("MainActivity", "onStop2")
        super.onSaveInstanceState(outState, outPersistentState)
    }


    override fun onStop() {
        Log.e("MainActivity", "onStop")
        super.onStop()
    }
}

package tech.yaowen.test.jni

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class JNIActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {

      val testBKFiled = 0
    super.onCreate(savedInstanceState)
    setContent {
      Column(modifier = Modifier.fillMaxWidth().background(Color.Gray)) { Text("JNI test") }
    }
    val jni = TestEntry()
//    jni.printValue("Hello 耀文")
//    jni.stringFromJNI()
  }


    fun testBK() {

    }
}

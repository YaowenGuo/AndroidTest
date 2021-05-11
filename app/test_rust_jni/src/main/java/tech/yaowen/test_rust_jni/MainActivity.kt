package tech.yaowen.test_rust_jni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import tech.yaowen.androidrust.Hello
import tech.yaowen.test_rust_jni.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =  ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.text.text = Hello().stringFromJNI("Rust go go go !!!!")
    }
}

package tech.yaowen.customview.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity
import androidx.viewbinding.ViewBinding
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.ActivityMainBinding
import tech.yaowen.customview.databinding.ActivityViewLifeTestBinding

class ViewLifeTestActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewLifeTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewLifeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}


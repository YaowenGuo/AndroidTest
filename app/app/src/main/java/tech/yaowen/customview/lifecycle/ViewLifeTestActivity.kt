package tech.yaowen.customview.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity
import androidx.viewbinding.ViewBinding
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.ActivityMainBinding
import tech.yaowen.customview.databinding.ActivityViewLifeTestBinding

class ViewLifeTestActivity : AppCompatActivity() {
    val binding: ActivityViewLifeTestBinding by bindView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_life_test)
    }
}


fun <T: ViewBinding> ComponentActivity.bindView(): Lazy<T> {
    ActivityViewLifeTestBinding.inflate()
    return
}
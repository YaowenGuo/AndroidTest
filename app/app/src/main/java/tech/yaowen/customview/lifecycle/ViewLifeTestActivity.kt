package tech.yaowen.customview.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.yaowen.customview.R

class ViewLifeTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_life_test)
    }
}
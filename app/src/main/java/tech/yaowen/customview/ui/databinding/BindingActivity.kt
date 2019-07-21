package tech.yaowen.customview.ui.databinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.yaowen.customview.R

class BindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.binding_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BindingFragment.newInstance())
                .commitNow()
        }


    }

}

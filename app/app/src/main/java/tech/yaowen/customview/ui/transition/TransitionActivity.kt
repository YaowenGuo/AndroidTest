package tech.yaowen.customview.ui.transition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import tech.yaowen.customview.R
import tech.yaowen.customview.databinding.TransitionActivityBinding


class TransitionActivity : AppCompatActivity() {
    lateinit var binding: TransitionActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.transition_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TransitionFragment())
                .commitNow()
        }
    }

}

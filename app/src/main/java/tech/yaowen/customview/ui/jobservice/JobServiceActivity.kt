package tech.yaowen.customview.ui.jobservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.yaowen.customview.R


class JobServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, JobFragment.newInstance())
                .commitNow()
        }
    }

}

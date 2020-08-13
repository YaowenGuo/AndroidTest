package tech.yaowen.customview.ui.jobservice

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity


fun <T : AppCompatActivity> T.drawBadge() {
    val decorView = window.decorView as ViewGroup
//    val contentParent = decorView.findViewById(android.R.id.content) as FrameLayout
    val badge = View(this)
    badge.setBackgroundColor(Color.YELLOW)
    decorView.addView(badge, 200, 100)
}
package tech.yaowen.customview

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hencoder.a33_lib_annotations.BindView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import tech.yaowen.customview.databinding.ActivityMainBinding
import tech.yaowen.customview.dialog.TransparentBgDialog
import tech.yaowen.customview.ui.TouchActivity

//import tech.yaowen.test_annotation.Binding
//import tech.yaowen.test_annotation.ButterKnife

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var rocketAnimation: AnimationDrawable

    @JvmField
    @BindView(R.id.textView)
    public var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
//        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val intent = Intent(this@MainActivity, TouchActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
//        main.findViewById<>()
        nav_view.setNavigationItemSelectedListener(this)



//        imageAnim.animate()
//            .translationX(100f)
//            .setStartDelay(1000)
//            .setDuration(1000)
//            .start()


        val objAnim = ObjectAnimator.ofFloat(imageAnim, "x", 100.dpToPx())

        objAnim.startDelay = 1000
        objAnim.reverse()
        objAnim.start()

//        val animSet = AnimatorSet()
//        imageAnim.apply {
//            setBackgroundResource(R.drawable.i)
//            rocketAnimation = background as AnimxxationDrawable
//        }
//
//        imageAnim.setOnClickListener { rocketAnimation.start() }

//        val dialog = TransparentBgDialog()
//        dialog.show(supportFragmentManager, "sd")

        val layoutAnim = LayoutTransition()
//        layoutAnim.setAnimator()

        visibleBtn.setOnClickListener {
            if (imageAnim.isVisible) {
                imageAnim.visibility = View.GONE
            } else {
                imageAnim.visibility = View.VISIBLE
            }
        }

        val holder1 = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val holder2 = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val holder3 = PropertyValuesHolder.ofFloat("alpha", 1f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(visibleBtn, holder1, holder2, holder3)
        animator.start()


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

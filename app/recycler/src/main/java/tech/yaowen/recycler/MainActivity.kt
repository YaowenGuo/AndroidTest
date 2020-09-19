package tech.yaowen.recycler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.hhl.gridpagersnaphelper.GridPagerSnapHelper
import com.hhl.recyclerviewindicator.OnPageChangeListener

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configSecondRecyclerView(2, 3)
    }

    private fun configSecondRecyclerView(row: Int, column: Int) {
        val secondRV: RecyclerView = findViewById(R.id.recycler)
        secondRV.setHasFixedSize(true)

        //setLayoutManager
        val gridLayoutManager = GridPagerLayoutManager(this, row, 3, false)
        secondRV.layoutManager = gridLayoutManager

        //attachToRecyclerView
//        val gridPagerSnapHelper = GridPagerSnapHelper()
//        gridPagerSnapHelper.setRow(row).setColumn(column)
//        gridPagerSnapHelper.attachToRecyclerView(secondRV)
        val screenWidth: Int = ScreenUtils.getScreenWidth()
        val itemWidth = screenWidth / column

        //setAdapter
        val adapter = RecyclerViewAdapter(itemWidth)
        secondRV.adapter = adapter

        //indicator
//        indicator.setRecyclerView(secondRV)
        //Note: pageColumn must be config
//        indicator.setPageColumn(column)
//        indicator.setOnPageChangeListener(object : OnPageChangeListener {
//            override fun onPageSelected(position: Int) {}
//            override fun onPageScrollStateChanged(state: Int) {}
//        })
    }
}

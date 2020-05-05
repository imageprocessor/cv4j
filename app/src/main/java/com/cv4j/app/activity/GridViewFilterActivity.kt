package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cv4j.app.R
import com.cv4j.app.adapter.GridViewFilterAdapter
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.ui.DividerGridItemDecoration
import kotlinx.android.synthetic.main.activity_gridview_filter.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.GridViewFilterActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:21
 * @version: V1.0 <描述当前版本功能>
 */

class GridViewFilterActivity : BaseActivity() {

    var title: String? = null

    private val list: MutableList<String> = ArrayList()

    companion object {
        private val myPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

        init {
            myPool.setMaxRecycledViews(0, 10)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gridview_filter)
        initViews()
        initData()
    }

    private fun initViews() {
        toolbar.title = "< $title"
    }

    private fun initData() {
        val res: Resources = getResources()
        val filterNames = res.getStringArray(R.array.filterNames)
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_mm)
        for (filter in filterNames) {
            list.add(filter)
        }
        val manager = GridLayoutManager(this@GridViewFilterActivity, 3)
        manager.setRecycleChildrenOnDetach(true)
        recyclerview.layoutManager = manager
        recyclerview.adapter = GridViewFilterAdapter(list, bitmap)
        recyclerview.addItemDecoration(DividerGridItemDecoration(this@GridViewFilterActivity))
        recyclerview.setRecycledViewPool(myPool)
    }
}
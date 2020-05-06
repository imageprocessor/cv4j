package com.cv4j.app.fragment

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cv4j.app.R
import com.cv4j.app.adapter.SpitalConvAdapter
import com.cv4j.app.app.BaseFragment
import com.cv4j.app.ui.DividerGridItemDecoration
import com.safframework.tony.common.utils.Preconditions
import kotlinx.android.synthetic.main.fragment_spital_conv.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.SpitalConvFragment
 * @author: Tony Shen
 * @date: 2020-05-04 13:09
 * @version: V1.0 <描述当前版本功能>
 */

class SpitalConvFragment : BaseFragment() {

    private val list: MutableList<String> = ArrayList()

    companion object {
        private val myPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

        init {
            myPool.setMaxRecycledViews(0, 10)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_spital_conv, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        val res: Resources = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_spital_conv)
        val filterNames = res.getStringArray(R.array.spatialConvNames)
        if (Preconditions.isNotBlank(filterNames)) {
            for (filter in filterNames) {
                list.add(filter)
            }
        }
        val manager = GridLayoutManager(mContext, 3)
        manager.recycleChildrenOnDetach = true
        recyclerview.layoutManager = manager
        recyclerview.adapter = SpitalConvAdapter(list, bitmap)
        recyclerview.addItemDecoration(DividerGridItemDecoration(mContext))
        recyclerview.setRecycledViewPool(myPool)
    }
}
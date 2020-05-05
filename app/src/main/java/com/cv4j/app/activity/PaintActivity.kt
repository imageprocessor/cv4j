package com.cv4j.app.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cv4j.app.R
import com.cv4j.app.adapter.PaintAdapter
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.fragment.PaintFragment
import kotlinx.android.synthetic.main.activity_oil_paint.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.PaintActivity
 * @author: Tony Shen
 * @date: 2020-05-05 21:13
 * @version: V1.0 <描述当前版本功能>
 */

class PaintActivity : BaseActivity() {

    var title: String? = null
    private val mList: MutableList<Fragment> = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oil_paint)

        intent.extras?.let {
            title = it.getString("Title","")
        }?:{
            title = ""
        }()

        toolbar.setOnClickListener {
            finish()
        }

        initData()
    }

    private fun initData() {
        toolbar.title = "< $title"
        mList.add(PaintFragment.newInstance(0))
        mList.add(PaintFragment.newInstance(1))
        mList.add(PaintFragment.newInstance(2))
        viewpager.setAdapter(PaintAdapter(this.getSupportFragmentManager(), mList))
        tablayout.setupWithViewPager(viewpager)
    }
}
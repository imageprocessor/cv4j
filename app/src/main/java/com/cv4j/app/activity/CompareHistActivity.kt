package com.cv4j.app.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cv4j.app.R
import com.cv4j.app.adapter.CompareHistAdapter
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.fragment.CompareHist1Fragment
import com.cv4j.app.fragment.CompareHist2Fragment
import com.cv4j.app.fragment.CompareHist3Fragment
import kotlinx.android.synthetic.main.activity_compare_hist.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.CompareHistActivity
 * @author: Tony Shen
 * @date: 2020-05-05 11:40
 * @version: V1.0 <描述当前版本功能>
 */

class CompareHistActivity : BaseActivity() {

    var title: String? = null
    private val mList: MutableList<Fragment> = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_hist)

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
        mList.add(CompareHist1Fragment())
        mList.add(CompareHist2Fragment())
        mList.add(CompareHist3Fragment())
        viewpager.adapter = CompareHistAdapter(this.supportFragmentManager, mList)
        tablayout.setupWithViewPager(viewpager)
    }
}
package com.cv4j.app.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cv4j.app.R
import com.cv4j.app.adapter.ProjectHistAdapter
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.fragment.BackProjectFragment
import com.cv4j.app.fragment.GaussianBackFragment
import kotlinx.android.synthetic.main.activity_flip.*
import kotlinx.android.synthetic.main.activity_flip.toolbar
import kotlinx.android.synthetic.main.activity_project_hist.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.ProjectHistActivity
 * @author: Tony Shen
 * @date: 2020-05-05 21:00
 * @version: V1.0 <描述当前版本功能>
 */

class ProjectHistActivity : BaseActivity() {

    var title: String? = null

    private val mList: MutableList<Fragment> = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_hist)

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
        mList.add(BackProjectFragment())
        mList.add(GaussianBackFragment())
        viewpager.setAdapter(ProjectHistAdapter(this.getSupportFragmentManager(), mList))
        tablayout.setupWithViewPager(viewpager)
    }
}
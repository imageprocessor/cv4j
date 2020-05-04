package com.cv4j.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.activity.CompareHistActivity
import com.cv4j.app.activity.HistogramDemoActivity
import com.cv4j.app.activity.HistogramEqualizationActivity
import com.cv4j.app.activity.ProjectHistActivity
import com.cv4j.app.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_hist.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.HistFragment
 * @author: Tony Shen
 * @date: 2020-05-04 11:43
 * @version: V1.0 <描述当前版本功能>
 */

class HistFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_hist, container, false)

        text1.setOnClickListener {
            val i = Intent(mContext, HistogramEqualizationActivity::class.java)
            i.putExtra("Title", text1.text.toString())
            startActivity(i)
        }

        text2.setOnClickListener {
            val i = Intent(mContext, HistogramDemoActivity::class.java)
            i.putExtra("Title", text2.text.toString())
            startActivity(i)
        }

        text3.setOnClickListener {
            val i = Intent(mContext, CompareHistActivity::class.java)
            i.putExtra("Title", text3.text.toString())
            startActivity(i)
        }

        text4.setOnClickListener {
            val i = Intent(mContext, ProjectHistActivity::class.java)
            i.putExtra("Title", text4.text.toString())
            startActivity(i)
        }

        return v
    }
}
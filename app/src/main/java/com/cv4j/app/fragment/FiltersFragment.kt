package com.cv4j.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.activity.*
import com.cv4j.app.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_filters.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.FiltersFragment
 * @author: Tony Shen
 * @date: 2020-05-04 11:27
 * @version: V1.0 <描述当前版本功能>
 */
class FiltersFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_filters, container, false)

        text1.setOnClickListener {
            val i = Intent(mContext, SelectFilterActivity::class.java)
            i.putExtra("Title", text1.text.toString())
            startActivity(i)
        }

        text2.setOnClickListener {
            val i = Intent(mContext, CompositeFilersActivity::class.java)
            i.putExtra("Title", text2.text.toString())
            startActivity(i)
        }

        text3.setOnClickListener {
            val i = Intent(mContext, UseFilterWithRxActivity::class.java)
            i.putExtra("Title", text3.text.toString())
            startActivity(i)
        }

        text4.setOnClickListener {
            val i = Intent(mContext, DslActivity::class.java)
            i.putExtra("Title", text4.text.toString())
            startActivity(i)
        }

        text5.setOnClickListener {
            val i = Intent(mContext, GridViewFilterActivity::class.java)
            i.putExtra("Title", text5.text.toString())
            startActivity(i)
        }

        text6.setOnClickListener {
            val i = Intent(mContext, ColorFilterActivity::class.java)
            i.putExtra("Title", text6.text.toString())
            startActivity(i)
        }

        text7.setOnClickListener {
            val i = Intent(mContext, GaussianBlurActivity::class.java)
            i.putExtra("Title", text7.text.toString())
            startActivity(i)
        }

        text8.setOnClickListener {
            val i = Intent(mContext, BeautySkinActivity::class.java)
            i.putExtra("Title", text8.text.toString())
            startActivity(i)
        }

        text9.setOnClickListener {
            val i = Intent(mContext, PaintActivity::class.java)
            i.putExtra("Title", text9.text.toString())
            startActivity(i)
        }

        return v
    }
}
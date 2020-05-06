package com.cv4j.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.activity.pixels.*
import com.cv4j.app.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_pixel_operator.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.PixelOperatorFragment
 * @author: Tony Shen
 * @date: 2020-05-04 12:57
 * @version: V1.0 <描述当前版本功能>
 */

class PixelOperatorFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_pixel_operator, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text1.setOnClickListener {
            val i = Intent(mContext, ArithmeticAndLogicOperationActivity::class.java)
            i.putExtra("Title", text1.text.toString())
            startActivity(i)
        }

        text2.setOnClickListener {
            val i = Intent(mContext, SubImageActivity::class.java)
            i.putExtra("Title", text2.text.toString())
            startActivity(i)
        }

        text3.setOnClickListener {
            val i = Intent(mContext, PrincipalColorExtractorActivity::class.java)
            i.putExtra("Title", text3.text.toString())
            startActivity(i)
        }

        text4.setOnClickListener {
            val i = Intent(mContext, ResizeActivity::class.java)
            i.putExtra("Title", text4.text.toString())
            startActivity(i)
        }

        text5.setOnClickListener {
            val i = Intent(mContext, FlipActivity::class.java)
            i.putExtra("Title", text5.text.toString())
            startActivity(i)
        }

        text6.setOnClickListener {
            val i = Intent(mContext, RotateActivity::class.java)
            i.putExtra("Title", text6.text.toString())
            startActivity(i)
        }
    }
}
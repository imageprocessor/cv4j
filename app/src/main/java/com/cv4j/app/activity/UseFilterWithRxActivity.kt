package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.filters.NatureFilter
import com.cv4j.rxjava.RxImageData
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.activity_use_filter_with_rx.*
import thereisnospon.codeview.CodeViewTheme

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.UseFilterWithRxActivity
 * @author: Tony Shen
 * @date: 2020-05-05 20:27
 * @version: V1.0 <描述当前版本功能>
 */

class UseFilterWithRxActivity : BaseActivity() {

    var title: String? = null

    lateinit var rxImageData: RxImageData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_filter_with_rx)

        intent.extras?.let {
            title = it.getString("Title","")
        }?:{
            title = ""
        }()

        toolbar.setOnClickListener {
            finish()
        }

        initViews()
        initData()
    }

    private fun initViews() {
        toolbar.title = "< $title"
    }

    private fun initData() {
        val res: Resources = getResources()
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_io)
        rxImageData = bitmap(bitmap)
        rxImageData.addFilter(NatureFilter())
                .isUseCache(false)
                .into(image)
        codeview.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        val code = StringBuilder()
        code.append("RxImageData.bitmap(bitmap)")
                .append("\r\n")
                .append("    .addFilter(new NatureFilter())")
                .append("\r\n")
                .append("    .into(image)")
        codeview.showCode(code.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        rxImageData.recycle()
    }
}
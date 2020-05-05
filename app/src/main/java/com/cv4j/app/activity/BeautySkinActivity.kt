package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.filters.face.BeautySkinFilter
import com.cv4j.rxjava.RxImageData
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.activity_beauty_skin.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.BeautySkinActivity
 * @author: Tony Shen
 * @date: 2020-05-05 10:57
 * @version: V1.0 <描述当前版本功能>
 */

class BeautySkinActivity : BaseActivity() {

    var title: String? = null

    private lateinit var rxImageData: RxImageData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beauty_skin)

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
        val res: Resources = resources
        var bitmap = BitmapFactory.decodeResource(res, R.drawable.test_beauty_skin1)
        origin_image1.setImageBitmap(bitmap)
        rxImageData = bitmap(bitmap)
        rxImageData.addFilter(BeautySkinFilter()).into(image1)
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_beauty_skin2)
        origin_image2.setImageBitmap(bitmap)
        rxImageData = bitmap(bitmap)
        rxImageData.addFilter(BeautySkinFilter()).into(image2)
    }

    override fun onDestroy() {
        super.onDestroy()
        rxImageData.recycle()
    }
}
package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.filters.CompositeFilters
import com.cv4j.core.filters.NatureFilter
import com.cv4j.core.filters.SpotlightFilter
import kotlinx.android.synthetic.main.activity_composite_filters.*
import thereisnospon.codeview.CodeViewTheme

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.CompositeFilersActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:05
 * @version: V1.0 <描述当前版本功能>
 */

class CompositeFilersActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composite_filters)

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

        val res: Resources = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters)
        val ci1 = CV4JImage(bitmap)
        image1.setImageBitmap(NatureFilter().filter(ci1.processor).image.toBitmap())
        val ci2 = CV4JImage(bitmap)
        image2.setImageBitmap(SpotlightFilter().filter(ci2.processor).image.toBitmap())
        val compositeFilters = CompositeFilters()
        val newBitmap = compositeFilters
                .addFilter(NatureFilter())
                .addFilter(SpotlightFilter())
                .filter(CV4JImage(bitmap).processor)
                .image
                .toBitmap()
        image3.setImageBitmap(newBitmap)
        codeview.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        val code = StringBuilder()
        code.append("CompositeFilters compositeFilters = new CompositeFilters();")
                .append("\r\n")
                .append("Bitmap newBitmap = compositeFilters")
                .append("\r\n")
                .append(".addFilter(new NatureFilter())").append("\r\n")
                .append(".addFilter(new SpotlightFilter())").append("\r\n")
                .append(".filter(new CV4JImage(bitmap).getProcessor())").append("\r\n")
                .append(".getImage()").append("\r\n")
                .append(".toBitmap();").append("\r\n").append("\r\n")
                .append("image3.setImageBitmap(newBitmap);")
        codeview.showCode(code.toString())
    }
}
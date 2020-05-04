package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.pixels.Resize
import kotlinx.android.synthetic.main.activity_resize.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.ResizeActivity
 * @author: Tony Shen
 * @date: 2020-05-04 13:49
 * @version: V1.0 <描述当前版本功能>
 */

class ResizeActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resize)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1)
        image.setImageBitmap(bitmap)
        var cv4jImage = CV4JImage(bitmap)
        var imageProcessor = cv4jImage.processor
        var resize = Resize(0.75f)
        imageProcessor = resize.resize(imageProcessor, Resize.NEAREST_INTEPOLATE)
        if (imageProcessor != null) {
            val resultCV4JImage = CV4JImage(imageProcessor.width, imageProcessor.height, imageProcessor.pixels)
            result_image.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
        cv4jImage = CV4JImage(bitmap)
        var imageProcessor2 = cv4jImage.processor
        resize = Resize(2f)
        imageProcessor2 = resize.resize(imageProcessor, Resize.BILINE_INTEPOLATE)
        if (imageProcessor2 != null) {
            val resultCV4JImage = CV4JImage(imageProcessor2.width, imageProcessor2.height, imageProcessor2.pixels)
            result_image2.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
    }
}
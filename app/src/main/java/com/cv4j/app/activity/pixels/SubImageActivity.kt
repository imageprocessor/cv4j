package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ImageProcessor
import com.cv4j.core.datamodel.Rect
import com.cv4j.core.pixels.Operator
import com.cv4j.exception.CV4JException
import kotlinx.android.synthetic.main.activity_sub_image.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.SubImageActivity
 * @author: Tony Shen
 * @date: 2020-05-04 21:49
 * @version: V1.0 <描述当前版本功能>
 */

class SubImageActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_image)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.pixel_test_3)
        image.setImageBitmap(bitmap)
        val cv4jImage = CV4JImage(bitmap)
        val imageProcessor = cv4jImage.processor
        val rect = Rect()
        rect.x = 300
        rect.y = 200
        rect.width = 300
        rect.height = 450
        var resultImageProcessor: ImageProcessor? = null
        try {
            resultImageProcessor = Operator.subImage(imageProcessor, rect)
        } catch (e: CV4JException) {
        }
        if (resultImageProcessor != null) {
            val resultCV4JImage = CV4JImage(resultImageProcessor.width, resultImageProcessor.height, resultImageProcessor.pixels)
            result_image.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
    }
}
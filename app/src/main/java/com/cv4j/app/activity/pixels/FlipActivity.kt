package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.pixels.Flip
import kotlinx.android.synthetic.main.activity_flip.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.FlipActivity
 * @author: Tony Shen
 * @date: 2020-05-04 13:22
 * @version: V1.0 <描述当前版本功能>
 */
class FlipActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip)

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

        val res: Resources = getResources()
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1)
        image.setImageBitmap(bitmap)
        var cv4jImage = CV4JImage(bitmap)
        val imageProcessor = cv4jImage.processor
        Flip.flip(imageProcessor, Flip.FLIP_HORIZONTAL)
        if (imageProcessor != null) {
            val resultCV4JImage = CV4JImage(imageProcessor.width, imageProcessor.height, imageProcessor.pixels)
            result_image1.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
        cv4jImage = CV4JImage(bitmap)
        val imageProcessor2 = cv4jImage.processor
        Flip.flip(imageProcessor2, Flip.FLIP_VERTICAL)
        if (imageProcessor2 != null) {
            val resultCV4JImage = CV4JImage(imageProcessor2.width, imageProcessor2.height, imageProcessor2.pixels)
            result_image2.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
    }
}
package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.Scalar
import com.cv4j.core.pixels.NormRotate
import kotlinx.android.synthetic.main.activity_flip.*
import kotlinx.android.synthetic.main.activity_rotate.*
import kotlinx.android.synthetic.main.activity_rotate.image
import kotlinx.android.synthetic.main.activity_rotate.toolbar
import kotlinx.android.synthetic.main.fragment_back_project.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.RotateActivity
 * @author: Tony Shen
 * @date: 2020-05-04 21:41
 * @version: V1.0 <描述当前版本功能>
 */

class RotateActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotate)

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
        val cv4jImage = CV4JImage(bitmap)
        var imageProcessor = cv4jImage.processor
        val normRotate = NormRotate()
        imageProcessor = normRotate.rotate(imageProcessor, 120f, Scalar.rgb(255, 0, 0))
        if (imageProcessor != null) {
            val resultCV4JImage = CV4JImage(imageProcessor.width, imageProcessor.height, imageProcessor.pixels)
            result_image.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
    }
}
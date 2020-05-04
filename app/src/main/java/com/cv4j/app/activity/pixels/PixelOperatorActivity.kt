package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ImageProcessor
import com.cv4j.core.pixels.Operator
import kotlinx.android.synthetic.main.activity_flip.*
import kotlinx.android.synthetic.main.activity_pixel_operator.*
import kotlinx.android.synthetic.main.activity_pixel_operator.toolbar

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.PixelOperatorActivity
 * @author: Tony Shen
 * @date: 2020-05-04 13:36
 * @version: V1.0 <描述当前版本功能>
 */

class PixelOperatorActivity : BaseActivity() {

    var title: String? = null

    var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixel_operator)

        intent.extras?.let {
            title = it.getString("Title","")
            type = it.getInt("Type",0)
        }?:{
            title = ""
            type = 0
        }()

        toolbar.setOnClickListener {
            finish()
        }
        initData()
    }

    private fun initData() {
        toolbar.title = "< $title"
        val res: Resources = resources
        val bitmap1 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1)
        image1.setImageBitmap(bitmap1)
        val bitmap2 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_2)
        image2.setImageBitmap(bitmap2)
        val cv4jImage1 = CV4JImage(bitmap1)
        val imageProcessor1 = cv4jImage1.processor
        val cv4jImage2 = CV4JImage(bitmap2)
        val imageProcessor2 = cv4jImage2.processor
        var imageProcessor: ImageProcessor? = null
        imageProcessor = when (type) {
            ADD -> Operator.add(imageProcessor1, imageProcessor2)
            SUBSTRACT -> Operator.substract(imageProcessor1, imageProcessor2)
            MULTIPLE -> Operator.multiple(imageProcessor1, imageProcessor2)
            DIVISION -> Operator.division(imageProcessor1, imageProcessor2)
            BITWISE_AND -> Operator.bitwise_and(imageProcessor1, imageProcessor2)
            BITWISE_OR -> Operator.bitwise_or(imageProcessor1, imageProcessor2)
            BITWISE_NOT -> Operator.bitwise_not(imageProcessor1)
            BITWISE_XOR -> Operator.bitwise_xor(imageProcessor1, imageProcessor2)
            ADD_WEIGHT -> Operator.addWeight(imageProcessor1, 2.0f, imageProcessor2, 1.0f, 4)
            else -> Operator.add(imageProcessor1, imageProcessor2)
        }
        if (imageProcessor != null) {
            val resultCV4JImage = CV4JImage(imageProcessor.width, imageProcessor.height, imageProcessor.pixels)
            result_image.setImageBitmap(resultCV4JImage.processor.image.toBitmap())
        }
    }

    companion object {
        const val ADD = 1
        const val SUBSTRACT = 2
        const val MULTIPLE = 3
        const val DIVISION = 4
        const val BITWISE_AND = 5
        const val BITWISE_OR = 6
        const val BITWISE_NOT = 7
        const val BITWISE_XOR = 8
        const val ADD_WEIGHT = 9
    }
}
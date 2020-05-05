package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.binary.ConnectedAreaLabel
import com.cv4j.core.binary.MorphOpen
import com.cv4j.core.binary.Threshold
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.Rect
import com.cv4j.core.datamodel.Size
import com.cv4j.image.util.Tools
import com.safframework.tony.common.utils.Preconditions
import kotlinx.android.synthetic.main.activity_morpholog.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.MorphologyActivity
 * @author: Tony Shen
 * @date: 2020-05-05 21:19
 * @version: V1.0 <描述当前版本功能>
 */

class MorphologyActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_morpholog)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_binary1)
        image0.setImageBitmap(bitmap)
        val cv4JImage = CV4JImage(bitmap)
        val threshold = Threshold()
        threshold.process(cv4JImage.convert2Gray().processor as ByteProcessor, Threshold.THRESH_TRIANGLE, Threshold.METHOD_THRESH_BINARY_INV, 255)
        image1.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val morphOpen = MorphOpen()
        cv4JImage.resetBitmap()
        morphOpen.process(cv4JImage.processor as ByteProcessor, Size(5))
        image2.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val connectedAreaLabel = ConnectedAreaLabel()
        val mask = IntArray(cv4JImage.processor.width * cv4JImage.processor.height)
        val rectangles: List<Rect> = ArrayList()
        connectedAreaLabel.process(cv4JImage.processor as ByteProcessor, mask, rectangles, true)
        cv4JImage.resetBitmap()
        val newBitmap = cv4JImage.processor.image.toBitmap()
        if (Preconditions.isNotBlank(rectangles)) {
            Tools.drawRects(newBitmap, rectangles)
        }
        image3.setImageBitmap(newBitmap)
    }
}
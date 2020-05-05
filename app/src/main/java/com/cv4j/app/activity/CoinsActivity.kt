package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.SparseIntArray
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.binary.ConnectedAreaLabel
import com.cv4j.core.binary.Erode
import com.cv4j.core.binary.Threshold
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.Size
import kotlinx.android.synthetic.main.activity_coins.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.CoinsActivity
 * @author: Tony Shen
 * @date: 2020-05-05 11:26
 * @version: V1.0 <描述当前版本功能>
 */

class CoinsActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coins)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_coins)
        image0.setImageBitmap(bitmap)
        val cv4JImage = CV4JImage(bitmap)
        val threshold = Threshold()
        threshold.process(cv4JImage.convert2Gray().processor as ByteProcessor, Threshold.THRESH_OTSU, Threshold.METHOD_THRESH_BINARY_INV, 255)
        image1.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val erode = Erode()
        cv4JImage.resetBitmap()
        erode.process(cv4JImage.processor as ByteProcessor, Size(3), 10)
        image2.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val connectedAreaLabel = ConnectedAreaLabel()
        val mask = IntArray(cv4JImage.processor.width * cv4JImage.processor.height)
        val num = connectedAreaLabel.process(cv4JImage.processor as ByteProcessor, mask, null, false) // 获取连通组件的个数
        val colors = SparseIntArray()
        val random = Random()
        val height = cv4JImage.processor.height
        val width = cv4JImage.processor.width
        val size = height * width
        for (i in 0 until size) {
            val c = mask[i]
            if (c >= 0) {
                colors.put(c, Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)))
            }
        }
        cv4JImage.resetBitmap()
        val newBitmap = cv4JImage.processor.image.toBitmap()
        for (row in 0 until height) {
            for (col in 0 until width) {
                val c = mask[row * width + col]
                if (c >= 0) {
                    newBitmap.setPixel(col, row, colors[c])
                }
            }
        }
        image3.setImageBitmap(newBitmap)
        if (num > 0) numTextView.text = String.format("总计识别出%d个硬币", num)
    }
}
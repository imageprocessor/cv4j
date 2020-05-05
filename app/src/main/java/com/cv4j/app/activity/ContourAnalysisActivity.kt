package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.util.SparseIntArray
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.binary.ConnectedAreaLabel
import com.cv4j.core.binary.ContourAnalysis
import com.cv4j.core.binary.Threshold
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.MeasureData
import com.safframework.log.L.i
import kotlinx.android.synthetic.main.activity_contour_analysis.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.ContourAnalysisActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:08
 * @version: V1.0 <描述当前版本功能>
 */

class ContourAnalysisActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contour_analysis)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_ca)
        image0.setImageBitmap(bitmap)
        val cv4JImage = CV4JImage(bitmap)
        val threshold = Threshold()
        threshold.process(cv4JImage.convert2Gray().processor as ByteProcessor, Threshold.THRESH_OTSU, Threshold.METHOD_THRESH_BINARY, 255)
        image1.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val connectedAreaLabel = ConnectedAreaLabel()
        connectedAreaLabel.setFilterNoise(true)
        val mask = IntArray(cv4JImage.processor.width * cv4JImage.processor.height)
        connectedAreaLabel.process(cv4JImage.processor as ByteProcessor, mask, null, false)
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
        image2.setImageBitmap(newBitmap)

        // 轮廓分析
        val thirdBitmap = Bitmap.createBitmap(newBitmap)
        val ca = ContourAnalysis()
        val measureDatas: List<MeasureData> = ArrayList()
        ca.process(cv4JImage.convert2Gray().processor as ByteProcessor, mask, measureDatas)
        val canvas = Canvas(thirdBitmap)
        val paint = Paint()
        paint.color = Color.WHITE
        for (data in measureDatas) {
            canvas.drawText(data.toString(), data.cp.x.toFloat(), data.cp.y.toFloat(), paint)
            i(data.toString())
        }
        image3.setImageBitmap(thirdBitmap)
    }
}
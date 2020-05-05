package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.binary.HoughLinesP
import com.cv4j.core.binary.Threshold
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.Line
import kotlinx.android.synthetic.main.activity_line_detection.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.LineDetectionActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:32
 * @version: V1.0 <描述当前版本功能>
 */

class LineDetectionActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_detection)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_lines)
        image0.setImageBitmap(bitmap)
        val cv4JImage = CV4JImage(bitmap)
        val threshold = Threshold()
        threshold.process(cv4JImage.convert2Gray().processor as ByteProcessor, Threshold.THRESH_OTSU, Threshold.METHOD_THRESH_BINARY, 255)
        image1.setImageBitmap(cv4JImage.processor.image.toBitmap())
        val houghLinesP = HoughLinesP()
        val lines: List<Line> = ArrayList<Line>()
        houghLinesP.process(cv4JImage.processor as ByteProcessor, 12, 10, 50, lines)
        val bm2 = Bitmap.createBitmap(cv4JImage.processor.image.toBitmap())
        val canvas = Canvas(bm2)
        val paint = Paint()
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 4f
        paint.color = Color.RED
        for (line in lines) {
            canvas.drawLine(line.x1.toFloat(), line.y1.toFloat(), line.x2.toFloat(), line.y2.toFloat(), paint)
        }
        image2.setImageBitmap(bm2)
    }
}
package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ImageProcessor
import com.cv4j.core.hist.CalcHistogram
import com.cv4j.core.hist.EqualHist
import kotlinx.android.synthetic.main.activity_histogram_equalization.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.HistogramEqualizationActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:28
 * @version: V1.0 <描述当前版本功能>
 */

class HistogramEqualizationActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histogram_equalization)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist)
        image0.setImageBitmap(bitmap)
        val cv4jImage0 = CV4JImage(bitmap)
        var imageProcessor = cv4jImage0.convert2Gray().processor
        var paint = Paint()
        calc_image0.setImageBitmap(drawHist(imageProcessor, paint))
        val cv4jImage = CV4JImage(bitmap)
        imageProcessor = cv4jImage.convert2Gray().processor
        if (imageProcessor is ByteProcessor) {
            val equalHist = EqualHist()
            equalHist.equalize(imageProcessor)
            image1.setImageBitmap(cv4jImage.processor.image.toBitmap())
            paint = Paint()
            calc_image1.setImageBitmap(drawHist(imageProcessor, paint))
        }
    }

    private fun drawHist(imageProcessor: ImageProcessor, paint: Paint): Bitmap {
        val calcHistogram = CalcHistogram()
        val bins = 127
        val hist = Array(imageProcessor.channels) { IntArray(bins) }
        calcHistogram.calcRGBHist(imageProcessor, bins, hist, true)
        val bm = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawRect(0f, 0f, 512f, 512f, paint)
        val step = 512.0f / 127
        var xoffset: Int
        var yoffset: Int
        val channels = imageProcessor.channels
        val colors = intArrayOf(Color.argb(127, 255, 0, 0), Color.argb(127, 0, 255, 0), Color.argb(127, 0, 0, 255))
        for (i in 0 until channels) {
            paint.color = colors[i]
            for (j in 0 until bins) {
                xoffset = (j * step).toInt()
                yoffset = hist[i][j] * 512 / 255
                canvas.drawRect(xoffset.toFloat(), 512 - yoffset.toFloat(), xoffset + step, 512f, paint)
            }
        }
        return bm
    }
}
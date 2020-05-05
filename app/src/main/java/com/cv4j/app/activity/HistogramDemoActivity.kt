package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ImageProcessor
import com.cv4j.core.hist.CalcHistogram
import kotlinx.android.synthetic.main.activity_histogram_demo.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.HistogramDemoActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:25
 * @version: V1.0 <描述当前版本功能>
 */

class HistogramDemoActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histogram_demo)

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
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2)
        image0.setImageBitmap(bitmap)
        val cv4jImage = CV4JImage(bitmap)
        image1.setImageBitmap(drawHist(cv4jImage.processor, Paint()))
    }

    private fun drawHist(imageProcessor: ImageProcessor, paint: Paint): Bitmap {
        val calcHistogram = CalcHistogram()
        val bins = 127
        val hist = Array(imageProcessor.channels) { IntArray(bins) }
        calcHistogram.calcRGBHist(imageProcessor, bins, hist, true)
        val bm = Bitmap.createBitmap(imageProcessor.width, imageProcessor.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawRect(0f, 0f, imageProcessor.width.toFloat(), imageProcessor.height.toFloat(), paint)
        val step = imageProcessor.width / 127.toFloat()
        var xoffset: Int
        var yoffset: Int
        val channels = imageProcessor.channels
        val h = imageProcessor.height
        val colors = intArrayOf(Color.argb(77, 255, 0, 0), Color.argb(77, 0, 255, 0), Color.argb(77, 0, 0, 255))
        for (i in 0 until channels) {
            paint.color = colors[i]
            for (j in 0 until bins) {
                xoffset = (j * step).toInt()
                yoffset = hist[i][j] * h / 255
                canvas.drawRect(xoffset.toFloat(), h - yoffset.toFloat(), xoffset + step, h.toFloat(), paint)
            }
        }
        return bm
    }
}
package com.cv4j.app.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.core.datamodel.ByteProcessor
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ColorProcessor
import com.cv4j.core.hist.BackProjectHist
import com.cv4j.core.hist.CalcHistogram
import kotlinx.android.synthetic.main.fragment_back_project.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.BackProjectFragment
 * @author: Tony Shen
 * @date: 2020-05-04 10:55
 * @version: V1.0 <描述当前版本功能>
 */
class BackProjectFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_back_project, container, false)
        initData()
        return v
    }

    private fun initData() {
        val res = resources
        val bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_project_target)
        target_image.setImageBitmap(bitmap1)
        val bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_project_sample)
        sample_image.setImageBitmap(bitmap2)
        val cv4jImage = CV4JImage(bitmap1)
        val colorProcessor = cv4jImage.processor as ColorProcessor
        val w = colorProcessor.width
        val h = colorProcessor.height

        // 反向投影结果
        val resultCV4JImage = CV4JImage(w, h)
        val byteProcessor = resultCV4JImage.processor as ByteProcessor

        // sample
        val sample = CV4JImage(bitmap2)
        val sampleProcessor = sample.processor as ColorProcessor
        val bins = 16
        val backProjectHist = BackProjectHist()
        backProjectHist.backProjection(colorProcessor, byteProcessor, CalcHistogram.calculateNormHist(sampleProcessor, bins), bins)
        result!!.setImageBitmap(byteProcessor.image.toBitmap())
    }
}
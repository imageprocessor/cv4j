package com.cv4j.app.fragment

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.hist.CalcHistogram
import com.cv4j.core.hist.CompareHist
import kotlinx.android.synthetic.main.fragment_compare_hist_3.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.CompareHist3Fragment
 * @author: Tony Shen
 * @date: 2020-05-04 11:24
 * @version: V1.0 <描述当前版本功能>
 */

class CompareHist3Fragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_compare_hist_3, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        val res: Resources = resources
        val bitmap1 = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist1)
        val bitmap2 = BitmapFactory.decodeResource(res, R.drawable.test_compare_hist2)
        image0.setImageBitmap(bitmap1)
        image1.setImageBitmap(bitmap2)
        val cv4jImage1 = CV4JImage(bitmap1)
        val imageProcessor1 = cv4jImage1.processor
        val cv4jImage2 = CV4JImage(bitmap2)
        val imageProcessor2 = cv4jImage2.processor
        var source: Array<IntArray>? = null
        var target: Array<IntArray>? = null
        val calcHistogram = CalcHistogram()
        val bins = 256
        source = Array(imageProcessor1.channels) { IntArray(bins) }
        calcHistogram.calcRGBHist(imageProcessor1, bins, source, true)
        target = Array(imageProcessor2.channels) { IntArray(bins) }
        calcHistogram.calcRGBHist(imageProcessor2, bins, target, true)
        val compareHist = CompareHist()
        val sb = StringBuilder()
        var sum1 = 0.0
        var sum2 = 0.0
        var sum3 = 0.0
        for (i in 0..2) {
            sum1 += compareHist.bhattacharyya(source[i], target[i])
            sum2 += compareHist.covariance(source[i], target[i])
            sum3 += compareHist.ncc(source[i], target[i])
        }
        sb.append("巴氏距离:").append(sum1 / 3).append("\r\n")
                .append("协方差:").append(sum2 / 3).append("\r\n")
                .append("相关性因子:").append(sum3 / 3)
        result.text = sb.toString()
    }
}
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
import kotlinx.android.synthetic.main.fragment_compare_hist_2.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.CompareHist2Fragment
 * @author: Tony Shen
 * @date: 2020-05-04 11:22
 * @version: V1.0 <描述当前版本功能>
 */
class CompareHist2Fragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_compare_hist_2, container, false)
        initData()
        return v
    }

    private fun initData() {
        val res: Resources = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_hist2)
        image0.setImageBitmap(bitmap)
        image1.setImageBitmap(bitmap)
        val cv4jImage = CV4JImage(bitmap)
        val imageProcessor = cv4jImage.processor
        var source: Array<IntArray>? = null
        val calcHistogram = CalcHistogram()
        val bins = 180
        source = Array(imageProcessor.channels) { IntArray(bins) }
        calcHistogram.calcHSVHist(imageProcessor, bins, source, true)
        val compareHist = CompareHist()
        val sb = StringBuilder()
        sb.append("巴氏距离:").append(compareHist.bhattacharyya(source[0], source[0])).append("\r\n")
                .append("协方差:").append(compareHist.covariance(source[0], source[0])).append("\r\n")
                .append("相关性因子:").append(compareHist.ncc(source[0], source[0]))
        result.text = sb.toString()
    }
}
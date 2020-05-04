package com.cv4j.app.fragment

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.core.filters.OilPaintFilter
import com.cv4j.core.filters.StrokeAreaFilter
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.fragment_paint.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.PaintFragment
 * @author: Tony Shen
 * @date: 2020-05-04 12:54
 * @version: V1.0 <描述当前版本功能>
 */

class PaintFragment : BaseFragment() {

    private var mType = 0

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mType = bundle.getInt(PAINT_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_paint, container, false)
        initData()
        return v
    }

    private fun initData() {
        val res: Resources = getResources()
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_oil_paint)
        when (mType) {
            OIL_PAINT_TYPE -> bitmap(bitmap).addFilter(OilPaintFilter()).into(image)
            PENCIL_PAINT_TYPE -> bitmap(bitmap).addFilter(StrokeAreaFilter()).into(image)
            else -> image.setImageBitmap(bitmap)
        }
    }

    companion object {
        private const val PAINT_TYPE = "type"
        private const val OIL_PAINT_TYPE = 1
        private const val PENCIL_PAINT_TYPE = 2

        fun newInstance(type: Int): PaintFragment {
            val args = Bundle()
            args.putInt(PAINT_TYPE, type)
            val fragment = PaintFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
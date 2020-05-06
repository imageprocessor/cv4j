package com.cv4j.app.fragment

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.image.util.QRCodeScanner
import kotlinx.android.synthetic.main.fragment_qr.*

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.QRFragment
 * @author: Tony Shen
 * @date: 2020-05-04 13:05
 * @version: V1.0 <描述当前版本功能>
 */

class QRFragment : BaseFragment() {

    private var mIndex = 0
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = arguments
        if (bundle != null) {
            mIndex = bundle.getInt(QR_INDEX)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_qr, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detect_btn.setOnClickListener {
            val cv4JImage = CV4JImage(bitmap)
            val qrCodeScanner = QRCodeScanner()
            val rect = qrCodeScanner.findQRCodeBounding(cv4JImage.processor, 1, 6)
            val bm = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(bm)
            val paint = Paint()
            paint.color = Color.RED
            paint.strokeWidth = 10.0.toFloat()
            paint.style = Paint.Style.STROKE
            val androidRect = Rect(rect.x - 20, rect.y - 20, rect.br().x + 20, rect.br().y + 20)
            canvas.drawRect(androidRect, paint)
            image.setImageBitmap(bm)
        }

        initData()
    }

    private fun initData() {
        bitmap = when (mIndex) {
            0 -> BitmapFactory.decodeResource(getResources(), R.drawable.qr_1)
            1 -> BitmapFactory.decodeResource(getResources(), R.drawable.qr_applepay)
            2 -> BitmapFactory.decodeResource(getResources(), R.drawable.qr_jiazhigang)
            3 -> BitmapFactory.decodeResource(getResources(), R.drawable.qr_tony)
            else -> BitmapFactory.decodeResource(getResources(), R.drawable.qr_1)
        }
        image.setImageBitmap(bitmap)
    }

    companion object {
        private const val QR_INDEX = "index"

        fun newInstance(index: Int): QRFragment {
            val args = Bundle()
            args.putInt(QR_INDEX, index)
            val fragment = QRFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
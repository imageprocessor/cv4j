package com.cv4j.app.fragment

import android.app.ProgressDialog
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cv4j.app.R
import com.cv4j.app.app.BaseFragment
import com.cv4j.rxjava.RxImageData
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.fragment_io.*
import thereisnospon.codeview.CodeViewTheme

/**
 *
 * @FileName:
 *          com.cv4j.app.fragment.IOFragment
 * @author: Tony Shen
 * @date: 2020-05-04 12:49
 * @version: V1.0 <描述当前版本功能>
 */

class IOFragment : BaseFragment() {

    private var rxImageData: RxImageData? = null
    private var dialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater.inflate(R.layout.fragment_io, container, false)

        dialog = ProgressDialog.show(mContext, "Loading", "Please wait...", true)
        initData()
        return v
    }

    private fun initData() {
        val res: Resources = mContext.resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.test_io)
        image1.setImageBitmap(bitmap)
        rxImageData = bitmap(bitmap)
        rxImageData!!.dialog(dialog).into(image2)
        codeview1.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        var code = StringBuilder()
        code.append("CV4JImage cv4JImage = new CV4JImage(bitmap);")
                .append("\r\n")
                .append("image2.setImageBitmap(cv4JImage.toBitmap());")
        codeview1.showCode(code.toString())
        codeview2.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor()
        code = StringBuilder()
        code.append("RxImageData.bitmap(bitmap).into(image2);")
        codeview2.showCode(code.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        rxImageData!!.recycle()
    }
}
package com.cv4j.app.activity

import android.app.ProgressDialog
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.filters.GaussianBlurFilter
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.activity_gaussian_blur.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.GaussianBlurActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:16
 * @version: V1.0 <描述当前版本功能>
 */

class GaussianBlurActivity : BaseActivity() {
    var title: String? = null

    private var res: Resources? = null
    private var bitmap: Bitmap? = null
    private lateinit var progDailog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gaussian_blur)

        intent.extras?.let {
            title = it.getString("Title","")
        }?:{
            title = ""
        }()

        toolbar.setOnClickListener {
            finish()
        }

        initData()
        useRenderScript()
        // 由于blur()中将bitmap回收啦，所以要重新赋值
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters)
        useCV4j()
    }

    private fun initData() {
        toolbar.title = "< $title"
        res = resources
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters)
        progDailog = ProgressDialog.show(this, "Loading", "Please wait...", true)
        progDailog.setCancelable(false)
    }

    private fun useRenderScript() {
        image1.setImageBitmap(blur(bitmap))
    }

    private fun useCV4j() {
        val filter = GaussianBlurFilter()
        filter.setSigma(10.0)
        bitmap(bitmap)
                .dialog(progDailog)
                .addFilter(filter)
                .into(image2)
    }

    /**
     * 使用RenderScript实现高斯模糊的算法
     * @param bitmap
     * @return
     */
    fun blur(bitmap: Bitmap?): Bitmap {
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        val outBitmap = Bitmap.createBitmap(bitmap!!.width, bitmap.height, Bitmap.Config.ARGB_8888)
        //Instantiate a new Renderscript
        val rs = RenderScript.create(getApplicationContext())
        //Create an Intrinsic Blur Script using the Renderscript
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        val allIn = Allocation.createFromBitmap(rs, bitmap)
        val allOut = Allocation.createFromBitmap(rs, outBitmap)
        //Set the radius of the blur: 0 < radius <= 25
        blurScript.setRadius(20.0f)
        //Perform the Renderscript
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap)
        //recycle the original bitmap
        bitmap.recycle()
        //After finishing everything, we destroy the Renderscript.
        rs.destroy()
        return outBitmap
    }
}
package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.filters.ColorFilter
import com.cv4j.rxjava.RxImageData
import com.cv4j.rxjava.RxImageData.Companion.bitmap
import kotlinx.android.synthetic.main.activity_color_filter.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.ColorFilterActivity
 * @author: Tony Shen
 * @date: 2020-05-05 11:36
 * @version: V1.0 <描述当前版本功能>
 */

class ColorFilterActivity : BaseActivity() {

    var title: String? = null
    private var bitmap: Bitmap? = null
    private val colorStyles: MutableMap<Int, String> = HashMap<Int, String>()
    private lateinit var rxImageData: RxImageData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_filter)

        intent.extras?.let {
            title = it.getString("Title","")
        }?:{
            title = ""
        }()

        toolbar.setOnClickListener {
            finish()
        }

        initViews()
        initData()
    }

    private fun initViews() {
        toolbar.title = "< $title"
    }

    private fun initData() {
        val res: Resources = resources
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_color_filter)
        rxImageData = bitmap(bitmap)
        rxImageData.addFilter(ColorFilter()).isUseCache(false).into(image)
        colorStyles[ColorFilter.AUTUMN_STYLE] = " 秋天风格 "
        colorStyles[ColorFilter.BONE_STYLE] = " 硬朗风格 "
        colorStyles[ColorFilter.COOL_STYLE] = " 凉爽风格 "
        colorStyles[ColorFilter.HOT_STYLE] = " 热带风格 "
        colorStyles[ColorFilter.HSV_STYLE] = " 色彩空间变换风格 "
        colorStyles[ColorFilter.JET_STYLE] = " 高亮风格 "
        colorStyles[ColorFilter.OCEAN_STYLE] = " 海洋风格 "
        colorStyles[ColorFilter.PINK_STYLE] = " 粉色风格 "
        colorStyles[ColorFilter.RAINBOW_STYLE] = " 彩虹风格 "
        colorStyles[ColorFilter.SPRING_STYLE] = " 春天风格 "
        colorStyles[ColorFilter.SUMMER_STYLE] = " 夏天风格 "
        colorStyles[ColorFilter.WINTER_STYLE] = " 冬天风格 "
        val len = colorStyles.size
        for (i in 0 until len) {
            val linearLp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val myLinear = LinearLayout(this)
            linearLp.setMargins(5, 0, 5, 20)
            myLinear.orientation = LinearLayout.HORIZONTAL
            myLinear.tag = i
            linear!!.addView(myLinear, linearLp)
            val textViewLp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val textView = TextView(this)
            textView.text = colorStyles[i].toString() + ""
            textView.gravity = Gravity.CENTER
            myLinear.addView(textView, textViewLp)
            myLinear.setOnClickListener { v ->
                Toast.makeText(this@ColorFilterActivity, colorStyles[v.tag as Int].toString() + "", Toast.LENGTH_SHORT).show()
                val colorFilter = ColorFilter()
                colorFilter.setStyle(v.tag as Int)
                rxImageData.recycle()
                rxImageData = bitmap(bitmap)
                rxImageData.addFilter(colorFilter).isUseCache(false).into(image)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rxImageData.recycle()
    }
}
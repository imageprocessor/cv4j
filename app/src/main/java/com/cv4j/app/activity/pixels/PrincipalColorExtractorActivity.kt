package com.cv4j.app.activity.pixels

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ColorProcessor
import com.cv4j.core.pixels.PrincipalColorExtractor
import kotlinx.android.synthetic.main.activity_principal_color_extractor.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.PrincipalColorExtractorActivity
 * @author: Tony Shen
 * @date: 2020-05-04 21:57
 * @version: V1.0 <描述当前版本功能>
 */

class PrincipalColorExtractorActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_color_extractor)

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
        image.setImageBitmap(bitmap)
        val cv4jImage = CV4JImage(bitmap)
        val imageProcessor = cv4jImage.processor
        val extractor = PrincipalColorExtractor()
        val scalars = extractor.extract(imageProcessor as ColorProcessor)
        val scalar0 = scalars[0]
        result0.setBackgroundColor(Color.rgb(scalar0.red, scalar0.green, scalar0.blue))
        val scalar1 = scalars[1]
        result1.setBackgroundColor(Color.rgb(scalar1.red, scalar1.green, scalar1.blue))
        val scalar2 = scalars[2]
        result2.setBackgroundColor(Color.rgb(scalar2.red, scalar2.green, scalar2.blue))
        val scalar3 = scalars[3]
        result3.setBackgroundColor(Color.rgb(scalar3.red, scalar3.green, scalar3.blue))
        val scalar4 = scalars[4]
        result4.setBackgroundColor(Color.rgb(scalar4.red, scalar4.green, scalar4.blue))
    }
}
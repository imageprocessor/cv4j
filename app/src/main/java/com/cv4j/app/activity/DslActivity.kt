package com.cv4j.app.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.filters.NatureFilter
import com.cv4j.rxjava.cv4j
import kotlinx.android.synthetic.main.activity_dsl.*
import thereisnospon.codeview.CodeViewTheme

class DslActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl)

        initData()
    }

    private fun initData() {

        title = intent.extras?.getString("Title")
        toolbar?.title = "< " + title

        cv4j {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_io)

            filter = NatureFilter()

            imageView = image
        }

        codeview?.setTheme(CodeViewTheme.ANDROIDSTUDIO)?.fillColor()

        val code = StringBuilder()
        code.append("cv4j {")
                .append("\r\n")
                .append("    bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_io)")
                .append("\r\n")
                .append("    filter = NatureFilter()")
                .append("\r\n")
                .append("    imageView = image")
                .append("\r\n")
                .append("}")

        codeview?.showCode(code.toString())

        toolbar?.setOnClickListener {
            finish()
        }
    }
}

package com.cv4j.app.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import com.cv4j.app.R
import com.cv4j.core.filters.NatureFilter
import com.cv4j.rxjava.cv4j
import thereisnospon.codeview.CodeView
import thereisnospon.codeview.CodeViewTheme

class DslActivity : AppCompatActivity() {

    var image: ImageView? = null

    var codeView: CodeView? = null

    var toolbar: Toolbar? = null

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dsl)

        initViews()
        initData()
    }

    private fun initViews() {

        image = findViewById(R.id.image) as ImageView?

        toolbar = findViewById(R.id.toolbar) as Toolbar?

        codeView = findViewById(R.id.codeview) as CodeView?
    }

    private fun initData() {

        title = intent.extras.getString("Title")
        toolbar?.title = "< " + title

        cv4j {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_io)

            filter = NatureFilter()

            imageView = image
        }

        codeView?.setTheme(CodeViewTheme.ANDROIDSTUDIO)?.fillColor()

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

        codeView?.showCode(code.toString())

        toolbar?.setOnClickListener {
            finish()
        }
    }
}

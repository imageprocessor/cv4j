package com.cv4j.app.activity.pixels

import android.content.Intent
import android.os.Bundle
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import kotlinx.android.synthetic.main.activity_arithmetic_and_logic_operator.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.pixels.ArithmeticAndLogicOperationActivity
 * @author: Tony Shen
 * @date: 2020-05-04 22:18
 * @version: V1.0 <描述当前版本功能>
 */

class ArithmeticAndLogicOperationActivity : BaseActivity() {

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arithmetic_and_logic_operator)

        intent.extras?.let {
            title = it.getString("Title","")
        }?:{
            title = ""
        }()

        toolbar.setOnClickListener {
            finish()
        }

        text1.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text1.text.toString())
            i.putExtra("Type", PixelOperatorActivity.ADD)
            startActivity(i)
        }

        text2.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text2.text.toString())
            i.putExtra("Type", PixelOperatorActivity.SUBSTRACT)
            startActivity(i)
        }

        text3.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text3.text.toString())
            i.putExtra("Type", PixelOperatorActivity.MULTIPLE)
            startActivity(i)
        }

        text4.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text4.text.toString())
            i.putExtra("Type", PixelOperatorActivity.DIVISION)
            startActivity(i)
        }

        text5.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text5.text.toString())
            i.putExtra("Type", PixelOperatorActivity.BITWISE_AND)
            startActivity(i)
        }

        text6.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text6.text.toString())
            i.putExtra("Type", PixelOperatorActivity.BITWISE_OR)
            startActivity(i)
        }

        text7.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text7.text.toString())
            i.putExtra("Type", PixelOperatorActivity.BITWISE_NOT)
            startActivity(i)
        }

        text8.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text8.text.toString())
            i.putExtra("Type", PixelOperatorActivity.BITWISE_XOR)
            startActivity(i)
        }

        text9.setOnClickListener {
            val i = Intent(this, PixelOperatorActivity::class.java)
            i.putExtra("Title", text9!!.text.toString())
            i.putExtra("Type", PixelOperatorActivity.ADD_WEIGHT)
            startActivity(i)
        }

        initData()
    }

    private fun initData() {
        toolbar.title = "< $title"
    }
}
package com.cv4j.app.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cv4j.app.R
import com.cv4j.app.adapter.QRAdapter
import com.cv4j.app.app.BaseActivity
import com.cv4j.app.fragment.QRFragment
import kotlinx.android.synthetic.main.activity_detect_qr.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.DetectQRActivity
 * @author: Tony Shen
 * @date: 2020-05-05 16:13
 * @version: V1.0 <描述当前版本功能>
 */

class DetectQRActivity : BaseActivity() {

    var title: String? = null

    private val mList: MutableList<Fragment> = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect_qr)

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
        for (i in 0..3) {
            mList.add(QRFragment.newInstance(i))
        }
        viewpager.adapter = QRAdapter(this.supportFragmentManager, mList)
        tablayout.setupWithViewPager(viewpager)
    }
}
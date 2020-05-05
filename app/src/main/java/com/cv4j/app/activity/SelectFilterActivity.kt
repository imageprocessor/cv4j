package com.cv4j.app.activity

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.cv4j.app.R
import com.cv4j.app.app.BaseActivity
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.filters.CommonFilter
import kotlinx.android.synthetic.main.activity_select_filter.*
import java.util.*

/**
 *
 * @FileName:
 *          com.cv4j.app.activity.SelectFilterActivity
 * @author: Tony Shen
 * @date: 2020-05-05 20:30
 * @version: V1.0 <描述当前版本功能>
 */

class SelectFilterActivity : BaseActivity() {

    var title: String? = null

    private var bitmap: Bitmap? = null
    private val list: MutableList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_filter)

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
        val filterNames = res.getStringArray(R.array.filterNames)
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters)
        image.setImageBitmap(bitmap)
        for (filter in filterNames) {
            list.add(filter)
        }
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                if (position == 0) {
                    image.setImageBitmap(bitmap)
                    return
                }

                // 清除滤镜
                image!!.clearColorFilter()
                val filterName = adapter.getItem(position) as String?
                changeFilter(filterName)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun getFilter(filterName: String?): Any? {
        var `object`: Any? = null
        try {
            `object` = Class.forName("com.cv4j.core.filters." + filterName + "Filter").newInstance()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return `object`
    }

    fun changeFilter(filterName: String?) {
        val colorImage = CV4JImage(bitmap)
        val filter = getFilter(filterName) as CommonFilter?
        if (filter != null) {
            image.setImageBitmap(filter.filter(colorImage.processor).image.toBitmap())
        }
    }
}
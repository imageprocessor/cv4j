package com.cv4j.app.activity

import android.R
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.filters.CommonFilter
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
    @InjectView(R.id.image)
    var image: ImageView? = null

    @InjectView(R.id.spinner)
    var spinner: Spinner? = null

    @InjectView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @InjectExtra(key = "Title")
    var title: String? = null
    private var bitmap: Bitmap? = null
    private val list: MutableList<String> = ArrayList()
    private var adapter: ArrayAdapter<*>? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_filter)
        initViews()
        initData()
    }

    private fun initViews() {
        toolbar.setTitle("< $title")
    }

    private fun initData() {
        val res: Resources = getResources()
        val filterNames = res.getStringArray(R.array.filterNames)
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters)
        image!!.setImageBitmap(bitmap)
        for (filter in filterNames) {
            list.add(filter)
        }
        adapter = ArrayAdapter<Any?>(this, R.layout.simple_list_item_1, list)
        adapter!!.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        spinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
                if (position == 0) {
                    image!!.setImageBitmap(bitmap)
                    return
                }

                // 清除滤镜
                image!!.clearColorFilter()
                val filterName = adapter!!.getItem(position) as String?
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

    @Trace
    fun changeFilter(filterName: String?) {
        val colorImage = CV4JImage(bitmap)
        val filter = getFilter(filterName) as CommonFilter?
        if (filter != null) {
            image!!.setImageBitmap(filter.filter(colorImage.processor).image.toBitmap())
        }
    }

    @OnClick(id = R.id.toolbar)
    fun clickToolbar() {
        finish()
    }
}
package com.cv4j.rxjava

import android.graphics.Bitmap
import android.widget.ImageView
import com.cv4j.core.filters.CommonFilter

/**
 * Created by Tony Shen on 2017/6/5.
 */
class Wrapper {

    var bitmap:Bitmap? = null

    var useCache:Boolean = true

    var imageView: ImageView? = null

    var filter: CommonFilter? = null
}

fun cv4j(init: Wrapper.() -> Unit) {
    val wrap = Wrapper()

    wrap.init()

    render(wrap)
}

private fun render(wrap: Wrapper) {

    RxImageData.bitmap(wrap.bitmap).addFilter(wrap.filter).isUseCache(wrap.useCache).into(wrap.imageView)
}

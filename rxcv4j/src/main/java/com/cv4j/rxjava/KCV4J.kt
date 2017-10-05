/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.rxjava

import android.app.Dialog
import android.graphics.Bitmap
import android.widget.ImageView
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.filters.CommonFilter

/**
 * only for Kotlin code,this class provides the DSL style for cv4j
 */
class Wrapper {

    var bitmap:Bitmap? = null

    var cv4jImage: CV4JImage? = null

    var bytes:ByteArray? = null

    var useCache:Boolean = true

    var imageView: ImageView? = null

    var filter: CommonFilter? = null

    var dialog: Dialog? = null
}

fun cv4j(init: Wrapper.() -> Unit) {
    val wrap = Wrapper()

    wrap.init()

    render(wrap)
}

private fun render(wrap: Wrapper) {

    if (wrap.bitmap!=null) {

        RxImageData.bitmap(wrap.bitmap).dialog(wrap.dialog).addFilter(wrap.filter).isUseCache(wrap.useCache).into(wrap.imageView)

    } else if (wrap.cv4jImage!=null) {

        RxImageData.image(wrap.cv4jImage).dialog(wrap.dialog).addFilter(wrap.filter).isUseCache(wrap.useCache).into(wrap.imageView)

    } else if (wrap.bytes!=null) {

        RxImageData.bytes(wrap.bytes).dialog(wrap.dialog).addFilter(wrap.filter).isUseCache(wrap.useCache).into(wrap.imageView)

    }

}

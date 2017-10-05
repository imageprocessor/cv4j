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
import android.util.Log
import android.widget.ImageView
import com.cv4j.core.datamodel.CV4JImage
import com.cv4j.core.datamodel.ImageProcessor
import com.cv4j.core.filters.CommonFilter
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.NotNull
import java.util.*

class RxImageData private constructor(internal var image: CV4JImage?) {
    internal var flowable: Flowable<WrappedCV4JImage>
    internal var memCache: MemCache
    internal var useCache = true

    internal var imageView: ImageView? = null
    internal var filters: MutableList<CommonFilter>
    internal var wrappedCV4JImage: WrappedCV4JImage
    internal var mDialog: Dialog? = null

    private constructor(bytes: ByteArray?) : this(CV4JImage(bytes)) {}

    private constructor(bitmap: Bitmap?) : this(CV4JImage(bitmap)) {}

    init {
        filters = ArrayList<CommonFilter>()
        memCache = MemCache.instance

        wrappedCV4JImage = WrappedCV4JImage(image, filters)
        flowable = Flowable.just(wrappedCV4JImage)
    }

    fun dialog(dialog: Dialog?): RxImageData {

        if (dialog == null) {

            return this
        }

        this.mDialog = dialog
        this.mDialog?.show()
        return this
    }

    /**
     * 使用滤镜，支持链式调用多个滤镜
     * @param filter
     * *
     * @return
     */
    fun addFilter(filter: CommonFilter?): RxImageData {

        if (filter == null) {

            Log.e("RxImageData", "filter is null")
            return this
        }

        filters.add(filter)
        return this
    }

    /**
     * 判断是否使用缓存，默认情况下是使用缓存。
     * 该方法需要在into()方法之前使用。
     * @param useCache
     * *
     * @return
     */
    fun isUseCache(useCache: Boolean): RxImageData {

        this.useCache = useCache
        return this
    }

    /**
     * RxImageData.bitmap(bitmap).addFilter(new ColorFilter()).into(view);
     * @param imageview
     */
    fun into(imageview: ImageView?) {

        if (imageview == null) {

            Log.e("RxImageData", "imageview is null")
            return
        }

        this.imageView = imageview
        render()
    }

    /**
     * 渲染imageview
     */
    private fun render() {

        if (imageView == null) {
            return
        }

        if (filters.size == 0) {
            this.flowable.compose(RxImageData.toMain()).subscribe({ (cV4JImage) ->
                imageView?.setImageBitmap(cV4JImage?.toBitmap())
            }, {
                t ->
                t.printStackTrace()
            }, {
                if (mDialog != null) {
                    mDialog?.dismiss()
                    mDialog = null
                }
            })
        } else if (filters.size == 1) {
            this.flowable
                    .map({ (image1, filters1) ->
                        if (useCache) {

                            val sb = StringBuilder()
                            if (imageView?.context!= null) {
                                sb.append(imageView!!.context.javaClass.simpleName)
                            }

                            sb.append(filters1[0].javaClass.simpleName).append(imageView?.id)

                            // 目前key采用activity name + filter name + imageView id
                            val key = Utils.md5(sb.toString())

                            if (memCache.get(key) == null) {

                                val imageProcessor = filters1[0].filter(image?.processor)
                                memCache.put(key, imageProcessor.image.toBitmap())

                                imageProcessor
                            } else {

                                image!!.processor.image.setBitmap(memCache.get(key))
                                image!!.processor
                            }
                        } else {

                            filters1[0].filter(image?.processor)
                        }
                    })
                    .compose(RxImageData.toMain())
                    .subscribe({ processor ->
                        imageView?.setImageBitmap(processor.image.toBitmap())
                    }, {
                        t -> t.printStackTrace()
                    }, {
                        if (mDialog != null) {
                            mDialog?.dismiss()
                            mDialog = null
                        }
                    })

        } else {

            this.flowable.map({ (image1, filters1) -> filters1 })
                    .map { filter(image!!.processor) }
                    .compose(RxImageData.toMain())
                    .subscribe({ processor ->
                        imageView?.setImageBitmap(processor.image.toBitmap())
                    }, {
                        t -> t.printStackTrace()
                    }, {
                        if (mDialog != null) {
                            mDialog?.dismiss()
                            mDialog = null
                        }
                    })
        }
    }


    private fun filter(imageData: ImageProcessor): ImageProcessor {

        if (filters.size > 0) {
            return filter(imageData, filters.size)
        }

        return imageData
    }

    private fun filter(imageData: ImageProcessor, size: Int): ImageProcessor {
        var imageData = imageData

        if (size == 1) {
            val filter = filters[0]
            return filter.filter(imageData)
        }

        val filter = filters[size - 1]
        imageData = filter.filter(imageData)

        return filter(imageData, size - 1)
    }

    /**
     * 释放资源
     */
    fun recycle() {

        image?.recycle()
    }

    companion object {

        @JvmStatic
        fun bytes(@NotNull bytes: ByteArray?): RxImageData {

            return RxImageData(bytes)
        }

        @JvmStatic
        fun bitmap(@NotNull bitmap: Bitmap?): RxImageData {

            return RxImageData(bitmap)
        }

        @JvmStatic
        fun image(@NotNull image: CV4JImage?): RxImageData {

            return RxImageData(image)
        }

        private fun <T> toMain(): FlowableTransformer<T, T> {

            return FlowableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}
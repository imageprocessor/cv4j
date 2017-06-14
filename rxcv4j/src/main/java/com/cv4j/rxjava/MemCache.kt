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

import android.graphics.Bitmap
import android.os.Build
import android.support.v4.util.LruCache
import com.safframework.tony.common.utils.Preconditions

class MemCache private constructor() {

    init {
        val cacheSize = Runtime.getRuntime().maxMemory().toInt() / 8
        mLruCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                val bitmapSize = getBitmapSize(value)
                return if (bitmapSize == 0) 1 else bitmapSize
            }

            override fun entryRemoved(evicted: Boolean, key: String?,
                                      oldValue: Bitmap?, newValue: Bitmap?) {

                if (evicted) {
                    if (oldValue != null && !oldValue.isRecycled) {
                        oldValue.recycle()
                    }
                }
            }
        }
    }

    private object SingletonHolder {
         val INSTANCE = MemCache()
    }

    /**
     * @param value a bitmap
     * *
     * @return size of bitmap
     */
    fun getBitmapSize(value: Bitmap?): Int {

        if (value == null) {
            return 0
        }

        if (Build.VERSION.SDK_INT >= 19) {
            return value.allocationByteCount
        } else {
            return value.height * value.rowBytes
        }
    }

    /**
     * 清空lrucache缓存
     */
    fun clear() {
        mLruCache!!.evictAll()
    }

    /**
     * 根据key删除MemoryCache的图片缓存
     * @param key
     */
    fun remove(key: String) {
        if (Preconditions.isNotBlank(key)) {
            mLruCache!!.remove(key)
        }
    }

    @Synchronized fun put(key: String?, bitmap: Bitmap) {
        mLruCache!!.put(key, bitmap)
    }

    operator fun get(key: String?): Bitmap? {
        return if (mLruCache == null) null else mLruCache!!.get(key)
    }

    companion object {

        var mLruCache: LruCache<String, Bitmap>? = null// 硬引用缓存

        val instance: MemCache
            get() = SingletonHolder.INSTANCE
    }
}
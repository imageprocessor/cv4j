package com.cv4j.rxjava

import android.graphics.Bitmap
import android.os.Build
import android.support.v4.util.LruCache
import com.safframwork.tony.common.utils.Preconditions

/**
 * Created by Tony Shen on 2017/5/19.
 */
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
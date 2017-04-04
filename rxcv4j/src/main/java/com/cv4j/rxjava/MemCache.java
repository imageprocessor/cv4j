package com.cv4j.rxjava;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.safframwork.tony.common.utils.Preconditions;

/**
 * Created by Tony Shen on 2017/3/31.
 */

public class MemCache {

    private static final String TAG = "MemCache";

    private static LruCache<String, Bitmap> mLruCache; // 硬引用缓存

    private MemCache() {
        int cacheSize = (int) Runtime.getRuntime().maxMemory() / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                final int bitmapSize = getBitmapSize(value);
                return bitmapSize == 0 ? 1 : bitmapSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                if(evicted){
                    if(oldValue != null && !oldValue.isRecycled()){
                        oldValue = null;
                    }
                }
            }
        };
    }

    private static class SingletonHolder {
        private static final MemCache INSTANCE = new MemCache();
    }

    public static final MemCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * @param value a bitmap
     * @return size of bitmap
     */
    @TargetApi(19)
    private int getBitmapSize(Bitmap value) {

        if (value == null) {
            return 0;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            return value.getAllocationByteCount();
        }

        return value.getHeight() * value.getRowBytes();
    }

    /**
     * 清空lrucache缓存
     */
    public void clear() {
        mLruCache.evictAll();
    }

    /**
     * 根据key删除MemoryCache的图片缓存
     * @param key
     */
    public void remove(String key) {
        if (Preconditions.isNotBlank(key)) {
            mLruCache.remove(key);
        }
    }

    public synchronized void put(String key,Bitmap bitmap) {
        mLruCache.put(key, bitmap);
    }

    public Bitmap get(String key) {
        return mLruCache == null ? null : mLruCache.get(key);
    }
}

package com.lh.imgloader.cache

import android.content.Context
import android.graphics.Bitmap
import com.lh.imgloader.request.BitmapRequest

/**
 * 综合缓存,内存和sd卡双缓存
 */
class DoubleCache(context: Context) : BitmapCache {

    private var mDiskCache: DiskCache = DiskCache.getDiskCache(context)

    private var mMemoryCache = MemoryCache()

    override fun get(key: BitmapRequest): Bitmap? {
        var value: Bitmap? = mMemoryCache[key]
        if (value == null) {
            value = mDiskCache[key]
            saveBitmapIntoMemory(key, value)
        }
        return value
    }

    private fun saveBitmapIntoMemory(key: BitmapRequest, bitmap: Bitmap?) {
        // 如果Value从disk中读取,那么存入内存缓存
        if (bitmap != null) mMemoryCache.put(key, bitmap)
    }

    override fun put(key: BitmapRequest, value: Bitmap) {
        mDiskCache.put(key, value)
        mMemoryCache.put(key, value)
    }

    override fun remove(key: BitmapRequest) {
        mDiskCache.remove(key)
        mMemoryCache.remove(key)
    }

}

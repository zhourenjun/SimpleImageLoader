
package com.lh.imgloader.cache

import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.lh.imgloader.request.BitmapRequest
/**
 * 图片的内存缓存,key为图片的uri,值为图片本身
 */
class MemoryCache : BitmapCache {

    private val mMemeryCache: LruCache<String, Bitmap>

    init {
        // 计算可使用的最大内存
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // 取4分之一的可用内存作为缓存
        val cacheSize = maxMemory / 4
        mMemeryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return bitmap!!.rowBytes * bitmap.height / 1024
            }
        }
    }

    override fun get(key: BitmapRequest): Bitmap? {
        return mMemeryCache.get(key.imageUri)
    }

    override fun put(key: BitmapRequest, value: Bitmap) {
        mMemeryCache.put(key.imageUri, value)
    }

    override fun remove(key: BitmapRequest) {
        mMemeryCache.remove(key.imageUri)
    }
}

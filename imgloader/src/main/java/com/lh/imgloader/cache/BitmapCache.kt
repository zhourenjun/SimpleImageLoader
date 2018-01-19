
package com.lh.imgloader.cache

import android.graphics.Bitmap
import com.lh.imgloader.request.BitmapRequest


/**
 * 请求缓存接口
 * 图片缓存抽象类,具体的子类有  不使用缓存{@see NoCache}
 *                          内存缓存{@see MemoryCache}
 *                          sd卡缓存{@seeDiskCache}
 *                          内存和sd卡双缓存{@see DoubleCache}
 */

interface BitmapCache {

    operator fun get(key: BitmapRequest): Bitmap?
    //缓存以图片的uri(BitmapRequest中有该请求对应的ImageView、图片uri、显示Config等属性)为key,
    // Bitmap为value来关联存储
    fun put(key: BitmapRequest, value: Bitmap)

    fun remove(key: BitmapRequest)
}

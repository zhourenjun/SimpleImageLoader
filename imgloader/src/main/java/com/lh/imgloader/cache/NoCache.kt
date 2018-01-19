
package com.lh.imgloader.cache

import android.graphics.Bitmap
import com.lh.imgloader.request.BitmapRequest

/**
 * 没有缓存
 */
class NoCache : BitmapCache {

    override fun get(key: BitmapRequest): Bitmap? {

        return null
    }

    override fun put(key: BitmapRequest, value: Bitmap) {

    }

    override fun remove(key: BitmapRequest) {

    }

}

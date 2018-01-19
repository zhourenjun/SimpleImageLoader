package com.lh.imgloader.loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.net.Uri
import com.lh.imgloader.request.BitmapRequest
import com.lh.imgloader.util.BitmapDecoder
import java.io.File

class LocalLoader : AbsLoader() {

    override fun onLoadImage(result: BitmapRequest): Bitmap? {
        val imagePath = Uri.parse(result.imageUri).path
        val imgFile = File(imagePath)
        if (!imgFile.exists()) {
            return null
        }

        // 从sd卡中加载的图片仅缓存到内存中,不做本地缓存
        result.justCacheInMem = true

        // 加载图片
        val decoder = object : BitmapDecoder() {
            override fun decodeBitmapWithOption(options: Options?): Bitmap {
                return BitmapFactory.decodeFile(imagePath, options)
            }
        }

        return decoder.decodeBitmap(result.imageViewWidth, result.imageViewHeight)
    }
}

package com.lh.imgloader.loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lh.imgloader.cache.disklrucache.IOUtil
import com.lh.imgloader.request.BitmapRequest
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class UrlLoader : AbsLoader() {

    override fun onLoadImage(result: BitmapRequest): Bitmap? {
        val imageUrl = result.imageUri
        val fos: FileOutputStream? = null
        var ins: InputStream? = null
        var bitmap: Bitmap? = null
        var conn: HttpURLConnection? = null
        try {
            val url = URL(imageUrl)
            conn = url.openConnection() as HttpURLConnection
            ins = BufferedInputStream(conn.inputStream)
            bitmap = BitmapFactory.decodeStream(ins, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            IOUtil.closeQuietly(ins)
            IOUtil.closeQuietly(fos)
            if (conn != null) {
                // 关闭流
                conn.disconnect()
            }
        }
        return bitmap
    }
}

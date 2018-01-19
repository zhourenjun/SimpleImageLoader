package com.lh.imgloader.cache

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Environment
import android.util.Log
import com.lh.imgloader.cache.disklrucache.DiskLruCache
import com.lh.imgloader.cache.disklrucache.IOUtil
import com.lh.imgloader.request.BitmapRequest
import com.lh.imgloader.util.BitmapDecoder
import com.lh.imgloader.util.Md5Helper
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class DiskCache private constructor(context: Context) : BitmapCache {

    // Disk LRU Cache
    private var mDiskLruCache: DiskLruCache? = null

    init {
        initDiskCache(context)
    }
    //初始化sdcard缓存
    private fun initDiskCache(context: Context) {
        try {
            val cacheDir = getDiskCacheDir(context, IMAGE_DISK_CACHE)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 50L * MB)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //获取sd缓存的目录,如果挂载了sd卡则使用sd卡缓存,否则使用应用的缓存目录
    private fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            Log.d("", "### context : " + context + ", dir = " + context.externalCacheDir)
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        }
        return File(cachePath + File.separator + uniqueName)
    }


    private fun getAppVersion(context: Context): Int {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            return info.versionCode
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    @Synchronized
    override fun get(key: BitmapRequest): Bitmap? {
        // 图片解析器
        val decoder = object : BitmapDecoder() {
            override fun decodeBitmapWithOption(options: Options?): Bitmap? {
                val inputStream = getInputStream(key.imageUriMd5)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                IOUtil.closeQuietly(inputStream)
                return bitmap
            }
        }
        return decoder.decodeBitmap(key.imageViewWidth, key.imageViewHeight)
    }

    private fun getInputStream(md5: String): InputStream? {
        val snapshot: DiskLruCache.Snapshot?
        try {
            snapshot = mDiskLruCache!![md5]
            if (snapshot != null) {
                return snapshot.getInputStream(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // sd卡缓存只缓存从网络下下载下来的图片,本地图片则不缓存
    override fun put(key: BitmapRequest, value: Bitmap) {
        if (key.justCacheInMem) {
            Log.e(IMAGE_DISK_CACHE, "### 仅缓存在内存中")
            return
        }
        try {
            // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
           val editor = mDiskLruCache!!.edit(key = key.imageUriMd5)
            if (editor != null) {
                val outputStream = editor.newOutputStream(0)
                if (writeBitmapToDisk(value, outputStream)) {
                    // 写入disk缓存
                    editor.commit()
                } else {
                    editor.abort()
                }
                IOUtil.closeQuietly(outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun writeBitmapToDisk(bitmap: Bitmap, outputStream: OutputStream): Boolean {
        val bos = BufferedOutputStream(outputStream, 8 * 1024)
        bitmap.compress(CompressFormat.JPEG, 100, bos)
        var result = true
        try {
            bos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            result = false
        } finally {
            IOUtil.closeQuietly(bos)
        }
        return result
    }

    override fun remove(key: BitmapRequest) {
        try {
            mDiskLruCache!!.remove(Md5Helper.toMD5(key.imageUriMd5))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        //1MB
        private val MB = 1024 * 1024
        // cache dir
        private val IMAGE_DISK_CACHE = "bitmap"

        private var mDiskCache: DiskCache? = null

        fun getDiskCache(context: Context): DiskCache {
            if (mDiskCache == null) {
                synchronized(DiskCache::class.java) {
                    if (mDiskCache == null) {
                        mDiskCache = DiskCache(context)
                    }
                }
            }
            return mDiskCache!!
        }
    }
}

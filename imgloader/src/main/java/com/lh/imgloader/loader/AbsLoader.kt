package com.lh.imgloader.loader

import android.graphics.Bitmap
import android.util.Log
import com.lh.imgloader.config.DisplayConfig
import com.lh.imgloader.core.SimpleImageLoader
import com.lh.imgloader.request.BitmapRequest

/**
 * 判断缓存中是否含有该图片,如果有则将图片直接投递到UI线程,并且更新UI;
 * 如果没有缓存,则从对应的地方获取到图片,并且将图片缓存起来,然后再将结果投递给UI线程,更新UI
 */
abstract class AbsLoader : Loader {

    override fun loadImage(result: BitmapRequest) {
        //	1、从缓存中获取
        var resultBitmap = mCache!!.get(result)
        Log.e("", "### 是否有缓存 : " + resultBitmap + ", uri = " + result.imageUri)
        if (resultBitmap == null) {
            showLoading(result)
            //	2、没有缓存,调用onLoaderImage加载图片
            resultBitmap = onLoadImage(result)
            //	3、缓存图片
            cacheBitmap(result, resultBitmap)
        } else {
            result.justCacheInMem = true
        }
           //	4、将结果投递到UI线程
        deliveryToUIThread(result, resultBitmap)
    }

    //加载图片的hook方法,留给子类处理
    protected abstract fun onLoadImage(result: BitmapRequest): Bitmap?


    private fun cacheBitmap(request: BitmapRequest, bitmap: Bitmap?) {
        // 缓存新的图片
        if (bitmap != null && mCache != null) {
            synchronized(mCache) {
                mCache.put(request, bitmap)
            }
        }
    }

    /**
     * 显示加载中的视图,注意这里也要判断imageview的tag与image uri的相等性,否则逆序加载时出现问题
     */
    private fun showLoading(request: BitmapRequest) {
        val imageView = request.imageView
        if (request.isImageViewTagValid && hasLoadingPlaceholder(request.displayConfig)) {
            imageView.post{ imageView.setImageResource(request.displayConfig!!.loadingResId) }
        }
    }

    /**
     * 将结果投递到UI,更新ImageView
     */
    private fun deliveryToUIThread(request: BitmapRequest, bitmap: Bitmap?) {
        request.imageView.post { updateImageView(request, bitmap) }
    }

    /**
     * 更新ImageView
     */
    private fun updateImageView(request: BitmapRequest, result: Bitmap?) {
        val imageView = request.imageView
        val uri = request.imageUri
        if (result != null && imageView.tag == uri) {
            imageView.setImageBitmap(result)
        }

        // 加载失败
        if (result == null && hasFaildPlaceholder(request.displayConfig)) {
            imageView.setImageResource(request.displayConfig!!.failedResId)
        }

        // 回调接口
        if (request.imageListener != null) {
            request.imageListener!!.onComplete(imageView, result!!, uri)
        }
    }

    private fun hasLoadingPlaceholder(displayConfig: DisplayConfig?): Boolean {
        return displayConfig != null && displayConfig.loadingResId > 0
    }

    private fun hasFaildPlaceholder(displayConfig: DisplayConfig?): Boolean {
        return displayConfig != null && displayConfig.failedResId > 0
    }

    companion object {

        /**
         * 图片缓存  不管Loader有多少个,缓存对象都应该是共享的
         */
        private val mCache = SimpleImageLoader.instance.config?.bitmapCache
    }

}

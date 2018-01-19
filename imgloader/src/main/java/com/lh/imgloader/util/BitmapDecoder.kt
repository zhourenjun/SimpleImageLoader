package com.lh.imgloader.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.util.Log

/**
 * 封装先加载图片bound，计算出inSmallSize之后再加载图片的逻辑操作
 */
abstract class BitmapDecoder {


    abstract fun decodeBitmapWithOption(options: Options?): Bitmap?

    /**
     * @param width 图片的目标宽度
     * @param height 图片的目标高度
     */
    fun decodeBitmap(width: Int, height: Int): Bitmap? {
        // 如果请求原图,则直接加载原图
        if (width <= 0 || height <= 0) {
            return decodeBitmapWithOption(null)
        }
        // 1、获取只加载Bitmap宽高等数据的Option, 即设置options.inJustDecodeBounds = true;
        val options = BitmapFactory.Options()
        // 设置为true,表示解析Bitmap对象，该对象不占内存  只解析图片尺寸等信息
        options.inJustDecodeBounds = true
        // 2、通过options加载bitmap，此时返回的bitmap为空,数据将存储在options中
        decodeBitmapWithOption(options)
        // 3、计算缩放比例, 并且将options.inJustDecodeBounds设置为false;
        configBitmapOptions(options, width, height)
        // 4、通过options设置的缩放比例加载图片
        return decodeBitmapWithOption(options)
    }

    /**
     * 加载原图
     */
    fun decodeOriginBitmap(): Bitmap? {
        return decodeBitmapWithOption(null)
    }


    @Suppress("DEPRECATION")
    private fun configBitmapOptions(options: Options, width: Int, height: Int) {
        // 设置缩放比例
        options.inSampleSize = computeInSmallSize(options, width, height)

        Log.d("", "$## inSampleSize = ${options.inSampleSize}, width = $width, height= $height")
        // 图片质量
        options.inPreferredConfig = Config.RGB_565
        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false
        options.inPurgeable = true
        options.inInputShareable = true
    }

    private fun computeInSmallSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // 图像的原始高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // 计算高度和宽度与要求的高度和/或宽度的比率
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // 选择最小的比例作为inSampleSize值，这将保证最终图像两个维度大于或等于所请求的高度和宽度。
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            // 这提供了一些额外的逻辑，以防图像有一个奇怪的/长宽比。例如，全景可能具有比高度大得多的宽度。
            // 在这些情况下，总像素可能仍然最终太大以至于不能很好地适应内存，
            // 所以我们应该更加积极地对图像取样（=较大的/ inSampleSize）。
            val totalPixels = (width * height).toFloat()
            // 任何超过请求像素2倍的东西，我们将进一步取样
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }
}

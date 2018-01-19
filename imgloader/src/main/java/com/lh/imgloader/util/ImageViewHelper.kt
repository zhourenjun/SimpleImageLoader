package com.lh.imgloader.util

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView


object ImageViewHelper {

    private val DEFAULT_WIDTH = 200
    private val DEFAULT_HEIGHT = 200

    /**
     * Width is defined by target [view][ImageView] parameters,
     * configuration parameters or device display dimensions.
     * Size computing algorithm:
     * 1) Get the actual drawn getWidth() of the View. If view haven't
     * drawn yet then go to step #2.
     * 2) Get layout_width. If it hasn't exact value then go to step #3.
     * 3) Get maxWidth.
     */
    fun getImageViewWidth(imageView: ImageView?): Int {
        if (imageView != null) {
            val params = imageView.layoutParams
            var width = 0
            if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = imageView.width // Get actual image width
            }
            if (width <= 0 && params != null) {
                width = params.width // Get layout width parameter
            }
            if (width <= 0) {
                width = getImageViewFieldValue(imageView, "mMaxWidth")
            }
            return width
        }
        return DEFAULT_WIDTH
    }

    /**
     * Height is defined by target [view][ImageView] parameters,
     * configuration parameters or device display dimensions.
     * Size computing algorithm:
     * 1) Get the actual drawn getHeight() of the View. If view haven't
     * drawn yet then go to step #2.
     * 2) Get layout_height. If it hasn't exact value then go to step #3.
     * 3) Get maxHeight.
     */
    fun getImageViewHeight(imageView: ImageView?): Int {
        if (imageView != null) {
            val params = imageView.layoutParams
            var height = 0
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.height // Get actual image height
            }
            if (height <= 0 && params != null) {
                // Get layout height parameter
                height = params.height
            }
            if (height <= 0) {
                height = getImageViewFieldValue(imageView, "mMaxHeight")
            }
            return height
        }
        return DEFAULT_HEIGHT
    }

    private fun getImageViewFieldValue(`object`: Any, fieldName: String): Int {
        var value = 0
        try {
            val field = ImageView::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            val fieldValue = field.get(`object`) as Int
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue
            }
        } catch (e: Exception) {
            Log.e("", e.message)
        }

        return value
    }
}

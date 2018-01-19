package com.lh.imgloader.request

import android.widget.ImageView
import com.lh.imgloader.config.DisplayConfig
import com.lh.imgloader.core.SimpleImageLoader
import com.lh.imgloader.policy.LoadPolicy
import com.lh.imgloader.util.ImageViewHelper
import com.lh.imgloader.util.Md5Helper
import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * 网络请求类.
 * 注意GET和DELETE不能传递参数,因为其请求的性质所致,用户可以将参数构建到url后传递进来到Request中.
 */
class BitmapRequest(imageView: ImageView, uri: String, var displayConfig: DisplayConfig?,
                    var imageListener: SimpleImageLoader.ImageListener?) : Comparable<BitmapRequest> {


    private var mImageViewRef: Reference<ImageView> = WeakReference(imageView)
    var imageUri = uri
    var imageUriMd5 = Md5Helper.toMD5(imageUri)
    //请求序列号  请求队列会根据它的序列号进行排序
    var serialNum = 0
    // 是否取消该请求
    var isCancel = false


    var justCacheInMem = false

    //加载策略
    private var mLoadPolicy = SimpleImageLoader.instance.config?.loadPolicy

    /**
     * 图片加载完成后判断ImageView的tag和uri是否相等,如果相等则将图片显示到ImageView上,
     * 否则不更新ImageView
     */
    val isImageViewTagValid: Boolean
        get() = if (mImageViewRef.get() != null) mImageViewRef.get()?.tag == imageUri else false

    val imageView: ImageView
        get() = mImageViewRef.get()!!

    val imageViewWidth: Int
        get() = ImageViewHelper.getImageViewWidth(mImageViewRef.get())

    val imageViewHeight: Int
        get() = ImageViewHelper.getImageViewHeight(mImageViewRef.get())

    init {
        imageView.tag = uri //防止图片错位显示
    }


    fun setLoadPolicy(policy: LoadPolicy?) {
        if (policy != null) {
            mLoadPolicy = policy
        }
    }

    override fun compareTo(other: BitmapRequest): Int {
        return mLoadPolicy!!.compare(this, other)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + imageUri.hashCode()
        result = prime * result + mImageViewRef.get()!!.hashCode()
        result = prime * result + serialNum
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (other == null)
            return false

        if (javaClass != other.javaClass)
            return false

        val B = other as BitmapRequest

        if (imageUri != B.imageUri) {

            return false
        }

        if (mImageViewRef.get() != B.mImageViewRef.get()) {
            return false
        }

        return serialNum == B.serialNum
    }

}

package com.lh.imgloader.config

import com.lh.imgloader.cache.BitmapCache
import com.lh.imgloader.cache.MemoryCache
import com.lh.imgloader.policy.LoadPolicy
import com.lh.imgloader.policy.SerialPolicy

/**
 * ImageLoader配置类
 */
class ImageLoaderConfig {

    //图片缓存配置对象
    var bitmapCache: BitmapCache = MemoryCache()

    //加载图片时的loading和加载失败的图片配置对象
    var displayConfig = DisplayConfig()

    // 加载策略
    var loadPolicy: LoadPolicy = SerialPolicy()

    var threadCount = Runtime.getRuntime().availableProcessors() + 1

    fun setThreadCount(count: Int): ImageLoaderConfig {
        threadCount = Math.max(1, count)
        return this
    }

    fun setCache(cache: BitmapCache): ImageLoaderConfig {
        bitmapCache = cache
        return this
    }
    //加载中的图片
    fun setLoadingPlaceholder(resId: Int): ImageLoaderConfig {
        displayConfig.loadingResId = resId
        return this
    }
    //加载失败的图片
    fun setNotFoundPlaceholder(resId: Int): ImageLoaderConfig {
        displayConfig.failedResId = resId
        return this
    }

    fun setLoadPolicy(policy: LoadPolicy?): ImageLoaderConfig {
        if (policy != null) {
            loadPolicy = policy
        }
        return this
    }
}

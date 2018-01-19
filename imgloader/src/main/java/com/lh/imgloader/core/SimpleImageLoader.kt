package com.lh.imgloader.core

import android.graphics.Bitmap
import android.widget.ImageView
import com.lh.imgloader.cache.BitmapCache
import com.lh.imgloader.cache.MemoryCache
import com.lh.imgloader.config.DisplayConfig
import com.lh.imgloader.config.ImageLoaderConfig
import com.lh.imgloader.request.BitmapRequest

/**
 * 图片加载类,支持url和本地图片的uri形式加载.根据图片路径格式来判断是网络图片还是本地图片,
 * 如果是网络图片则交给SimpleNet框架来加载，如果是本地图片那么则交给mExecutorService从sd卡中加载
 * .加载之后直接更新UI，无需用户干预.如果用户设置了缓存策略,那么会将加载到的图片缓存起来.
 * 用户也可以设置加载策略，例如顺序加载{@see SerialPolicy}和逆向加载{@see ReversePolicy}.
 *
 * SimpleImageLoader-> ImageLoaderConfig-> RequestQueue-> RequestDispatcher-> loader-> cache
 * 用户调用displayImage请求加载图片,SimpleImageLoader将这个加载图片请求封装成一个Request,
 * 然后加入到队列中。几个色眯眯的调度子线程不断地从队列中获取请求,然后根据uri的格式获取到对应的Loader来加载图片。
 * 在加载图片之前首先会查看缓存中是否含有目标图片(具体细节在后续的博客再细说),如果有缓存则使用缓存,
 * 否则加载目标图片。获取到图片之后,我们会将图片投递给ImageView进行更新,如果该ImageView的tag与图片的uri是一样的,
 * 那么则更新ImageView,否则不处理。使用ImageView的tag与图片的uri进行对比是为了防止图片错位显示的问题,
 * 这在ImageLoader中是很重要的一步。如果目标图片没有缓存,第一次从uri中加载后会加入缓存中,
 * 当然从sdcard中加载的图片我们只会缓存到内存中,而不会再缓存一份到sd卡的另一个目录中。
 */
class SimpleImageLoader private constructor() {

    //网络请求队列
    private var mImageQueue: RequestQueue? = null
    // 缓存
    @Volatile private var mCache: BitmapCache? = MemoryCache()
    //图片加载配置对象
    var config: ImageLoaderConfig? = null
        private set

    //初始化ImageLoader,启动请求队列 调用init方法后整个ImageLoader就正式启动了
    fun init(config: ImageLoaderConfig) {
        this.config = config
        mCache = this.config!!.bitmapCache
        checkConfig()
        mImageQueue = RequestQueue(this.config!!.threadCount)
        mImageQueue!!.start()
    }

    private fun checkConfig() {
        if (config == null) {
            throw RuntimeException("The config of SimpleImageLoader is Null, please call the init(ImageLoaderConfig config) method to initialize")
        }

        if (mCache == null) {
            mCache = MemoryCache()
        }
    }

    fun displayImage(imageView: ImageView, uri: String, listener: ImageListener) {
        displayImage(imageView, uri, null, listener)
    }

    @JvmOverloads
    fun displayImage(imageView: ImageView, uri: String, config: DisplayConfig? = null, listener: ImageListener? = null) {

        val request = BitmapRequest(imageView, uri, config, listener)
        // 加载的配置对象,如果没有设置则使用ImageLoader的配置
        request.displayConfig = if (request.displayConfig != null)
            request.displayConfig
        else
            this.config!!.displayConfig
        // 添加到队列中
        mImageQueue!!.addRequest(request)
    }

    fun stop() {
        mImageQueue!!.stop()
    }

    // 图片加载Listener
    interface ImageListener {
        fun onComplete(imageView: ImageView, bitmap: Bitmap, uri: String)
    }

    companion object {

        private var sInstance: SimpleImageLoader? = null

        val instance: SimpleImageLoader
            get() {
                if (sInstance == null) {
                    synchronized(SimpleImageLoader::class.java) {
                        if (sInstance == null) {
                            sInstance = SimpleImageLoader()
                        }
                    }
                }
                return sInstance!!
            }
    }
}

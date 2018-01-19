package com.lh.imgloader.core

import android.util.Log
import com.lh.imgloader.loader.LoaderManager
import com.lh.imgloader.request.BitmapRequest
import java.util.concurrent.BlockingQueue


/**
 * 网络请求Dispatcher,继承自Thread,从网络请求队列中循环读取请求并且执行
 */
class RequestDispatcher(private val mRequestQueue: BlockingQueue<BitmapRequest>) : Thread() {

    override fun run() {
        try {
            while (!this.isInterrupted) {  //不断地从队列中获取请求
                val request = mRequestQueue.take() //删除头并返回删除的头
                if (request.isCancel) continue
                //	解析图片schema
                val schema = parseSchema(request.imageUri)
                //	根据schema获取对应的Loader
                val imageLoader = LoaderManager.instance.getLoader(schema)
                //	加载图片
                imageLoader.loadImage(request)
            }
        } catch (e: InterruptedException) {
            Log.i("", "### 请求分发器退出")
        }
    }

    /**
     * 如果你要实现自己的Loader来加载特定的格式,那么它的uri格式必须以schema://开头,
     * 否则解析会错误,例如可以为drawable://image,然后你注册一个schema为"drawable"的Loader
     * 到LoaderManager中,SimpleImageLoader在加载图片时就会使用你注册的Loader来加载图片,
     * 这样就可以应对用户的多种多样的需求
     */
    //这里是解析图片uri的格式,uri格式为:	schema://+图片路径
    private fun parseSchema(uri: String?): String {
        if (uri!!.contains("://")) {
            return uri.split("://".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            Log.e(name, "### wrong scheme, image uri is : " + uri)
        }
        return ""
    }
}

package com.lh.imgloader.policy

import com.lh.imgloader.request.BitmapRequest

/**
 * 逆序加载策略,即从最后加入队列的请求进行加载
 */
class ReversePolicy : LoadPolicy {

    override fun compare(request1: BitmapRequest, request2: BitmapRequest): Int {
        // 注意Bitmap请求要先执行最晚加入队列的请求,ImageLoader的策略
        return request2.serialNum - request1.serialNum
    }
}

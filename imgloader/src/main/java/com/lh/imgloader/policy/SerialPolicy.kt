package com.lh.imgloader.policy

import com.lh.imgloader.request.BitmapRequest

/**
 * 顺序加载策略
 */
class SerialPolicy : LoadPolicy {

    override fun compare(request1: BitmapRequest, request2: BitmapRequest): Int {
        // 如果优先级相等,那么按照添加到队列的序列号顺序来执行
        return request1.serialNum - request2.serialNum
    }

}

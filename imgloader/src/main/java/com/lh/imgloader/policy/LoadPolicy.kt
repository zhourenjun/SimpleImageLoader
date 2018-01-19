
package com.lh.imgloader.policy

import com.lh.imgloader.request.BitmapRequest

/**
 * 加载策略接口
 */
interface LoadPolicy {
    fun compare(request1: BitmapRequest, request2: BitmapRequest): Int
}

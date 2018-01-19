package com.lh.imgloader.loader

import com.lh.imgloader.request.BitmapRequest

/**
 * 面向对象的几大原则最终化为几个简单的关键字:		抽象、单一职责、最小化
 */
interface Loader {
    fun loadImage(result: BitmapRequest)
}


package com.lh.imgloader.core

import android.util.Log
import com.lh.imgloader.request.BitmapRequest
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 请求队列, 使用优先队列,使得请求可以按照优先级进行处理. [ Thread Safe ]
 */
class RequestQueue @JvmOverloads constructor(coreNums: Int = DEFAULT_CORE_NUMS) {

    //请求队列 [ Thread-safe ]
    private val allRequests: BlockingQueue<BitmapRequest> = PriorityBlockingQueue<BitmapRequest>()
    //请求的序列化生成器
    private val mSerialNumGenerator = AtomicInteger(0)
    // CPU核心数 + 1个分发线程数
    private var mDispatcherNums = DEFAULT_CORE_NUMS
    // NetworkExecutor,执行网络请求的线程
    private var mDispatchers = ArrayList<RequestDispatcher>(mDispatcherNums)

    init {
        mDispatcherNums = coreNums
    }

    /**
     * 启动RequestDispatcher
     */
    private fun startDispatchers() {
        for (i in 0 until mDispatcherNums) {
            Log.e("", "### 启动线程 " + i)
            mDispatchers.add(RequestDispatcher(allRequests))
            mDispatchers[i].start()
        }
    }

    fun start() {
        stop()
        startDispatchers()
    }

    /**
     * 停止RequestDispatcher
     */
    fun stop() {
        if ( mDispatchers.isNotEmpty()) {
            for (i in mDispatchers.indices) {
                mDispatchers[i].interrupt()
            }
        }
    }

    /**
     * 不能重复添加请求
     */
    fun addRequest(request: BitmapRequest) {
        if (!allRequests.contains(request)) {
            request.serialNum = this.generateSerialNumber()
            allRequests.add(request)
        } else {
            Log.d("", "### 请求队列中已经含有")
        }
    }

    fun clear() {
        allRequests.clear()
    }

    //为每个请求生成一个系列号
    private fun generateSerialNumber()= mSerialNumGenerator.incrementAndGet()

    companion object {
        // 默认的核心数
        var DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1
    }
}


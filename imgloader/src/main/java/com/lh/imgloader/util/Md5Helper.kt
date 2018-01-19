package com.lh.imgloader.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * MD5辅助类,对字符串取MD5
 */
object Md5Helper {
    //使用MD5算法对传入的key进行加密并返回
    private var mDigest: MessageDigest? = null

    init {
        try {
            mDigest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    // 对key进行MD5加密，如果无MD5加密算法，则直接使用key对应的hash值。
    fun toMD5(key: String): String {
        val cacheKey: String
        //获取MD5算法失败时，直接使用key对应的hash值
        if (mDigest == null) {
            return key.hashCode().toString()
        }
        mDigest!!.update(key.toByteArray())
        cacheKey = bytesToHexString(mDigest!!.digest())
        return cacheKey
    }


    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (aByte in bytes) {
            val hex = Integer.toHexString(0xFF and aByte.toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}


package com.lh.imgloader.loader

import android.graphics.Bitmap
import android.util.Log
import com.lh.imgloader.request.BitmapRequest

class NullLoader : AbsLoader() {

    override fun onLoadImage(result: BitmapRequest): Bitmap? {
        Log.e(NullLoader::class.java.simpleName, "### wrong schema, your image uri is : " + result.imageUri)
        return null
    }
}

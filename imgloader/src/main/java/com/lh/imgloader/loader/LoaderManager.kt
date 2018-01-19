package com.lh.imgloader.loader

import java.util.HashMap

class LoaderManager private constructor() {


    private val mLoaderMap = HashMap<String, Loader>()

    private val mNullLoader = NullLoader()  //Null	Object设计模式
    //默认将几个Loader注入到LoaderManager中
    init {
        register(HTTP, UrlLoader())
        register(HTTPS, UrlLoader())
        register(FILE, LocalLoader())
    }


    @Synchronized
    private fun register(schema: String, loader: Loader) {
        mLoaderMap.put(schema, loader)
    }

    fun getLoader(schema: String) = if (mLoaderMap.containsKey(schema)) mLoaderMap[schema]!! else mNullLoader

    companion object {

        val HTTP = "http"
        val HTTPS = "https"
        val FILE = "file"

        private var INSTANCE: LoaderManager? = null

        val instance: LoaderManager
            get() {
                if (INSTANCE == null) {
                    synchronized(LoaderManager::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = LoaderManager()
                        }
                    }
                }
                return INSTANCE!!
            }
    }
}

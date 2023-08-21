package com.zipper.fetch.cookie

import android.app.Application
import android.content.Context

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/21
 */
class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    init {
        appContext = this
    }
}

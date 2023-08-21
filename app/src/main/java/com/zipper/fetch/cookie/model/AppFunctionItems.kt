package com.zipper.fetch.cookie.model

import com.zipper.fetch.cookie.ui.AppDestination
import com.zipper.fetch.cookie.ui.AppScreen

sealed class AppFunctionItems(val name: String, val appScreen: AppScreen) {

    object MiniMaoTaiItem: AppFunctionItems("小程序茅台", AppScreen.MiniHome)
}

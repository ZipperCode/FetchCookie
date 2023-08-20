package com.zipper.fetch.cookie.model

import com.zipper.fetch.cookie.ui.AppDestination

sealed class AppFunctionItems(val name: String, val routePath: String) {

    object MiniMaoTaiItem: AppFunctionItems("小程序茅台", AppDestination.MTScreenRoute)
}

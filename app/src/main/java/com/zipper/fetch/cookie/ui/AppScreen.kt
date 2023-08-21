package com.zipper.fetch.cookie.ui

sealed class AppScreen(val route: String) {

    object App: AppScreen("MainApp")
    object MiniHome: AppScreen("MiniHome")
    object MiniLogin: AppScreen("MiniLogin")

}

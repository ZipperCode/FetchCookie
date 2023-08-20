package com.zipper.fetch.cookie.ui.minimt.model

data class MiniTokenData(
    val openId: String,
    val realName: String,
    val phone: String,
    val realNameAuth: Boolean,
    val isCard: String,
    val isPhoneBind: Boolean,
    val status: Int
)

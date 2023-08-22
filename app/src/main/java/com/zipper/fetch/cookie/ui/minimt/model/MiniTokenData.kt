package com.zipper.fetch.cookie.ui.minimt.model

data class MiniTokenData constructor(
    val openId: String,
    val realName: String,
    val phone: String,
    val realNameAuth: Boolean,
    val idCard: String,
    val isPhoneBind: Boolean,
    val status: Int,
)

val EMPTY_TOKEN_DATA = MiniTokenData("", "", "", false, "", false, -1)

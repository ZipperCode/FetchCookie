package com.zipper.fetch.miniprogram.mtaotai

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginConfig(
    val appId: String,
    val host: String,
    val icon: Int,
    val ak: String,
    val sk: String,
    val sendCodeCode: String,
    val phoneLoginCode: String
) : Parcelable

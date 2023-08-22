package com.zipper.fetch.cookie.data

import androidx.compose.ui.graphics.Color
import com.zipper.fetch.cookie.model.AppFunctionItems
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramConfig

object UiDataProvider {

    val mainButtons = listOf(
        AppFunctionItems.MiniMaoTaiItem
    )

    val miniProgramItems = listOf(
        MiniProgramConfig.Zhcs,
        MiniProgramConfig.Lgkx,
        MiniProgramConfig.Gyjp,
        MiniProgramConfig.Xlhg,
        MiniProgramConfig.Glyp,
        MiniProgramConfig.Hljg,
        MiniProgramConfig.Yljx,

    )

    val colors = listOf(
        Color(0xFFffd7d7.toInt()),
        Color(0xFFffe9d6.toInt()),
        Color(0xFFfffbd0.toInt()),
        Color(0xFFe3ffd9.toInt()),
        Color(0xFFd0fff8.toInt())
    )
}
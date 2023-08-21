package com.zipper.fetch.cookie.ui.minimt.model

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/21
 */
data class InitMiniProgramData(
    val appId: String,
    val name: String,
    val channel: Int,
    val ak: String,
    val sk: String,
    val sendCodeCode: String,
    val phoneLoginCode: String
): IDrawDown {
    override val text: String
        get() = name
}

package com.zipper.fetch.cookie.ui.minimt.model

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/21
 */
data class MiniProgramInitData constructor(
    val type: Int,
    val appId: String,
    val name: String,
    val channel: Int,
    val ak: String,
    val sk: String,
    val sendCodeCode: String,
    val phoneLoginCode: String,
) : IDrawDown {
    override val text: String
        get() = name

    companion object {
        val PLACEHOLDER = MiniProgramInitData(
            -1,
            "",
            "请选择一个小程序",
            0,
            "",
            "",
            "",
            "",
        )

        fun createInitFailData(type: Int, appId: String, name: String, sendCodeCode: String, phoneLoginCode: String): MiniProgramInitData {
            return MiniProgramInitData(type, appId, name, 0, "", "", sendCodeCode, phoneLoginCode)
        }
    }

    val available: Boolean get() = ak.isNotEmpty() && sk.isNotEmpty()
}

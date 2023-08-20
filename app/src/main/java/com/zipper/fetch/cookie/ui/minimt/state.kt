package com.zipper.fetch.cookie.ui.minimt

import com.zipper.fetch.cookie.ui.minimt.model.MiniAccount

sealed interface MiniUIState{
    val isLoading: Boolean

    data class NoData(
        override val isLoading: Boolean,
        val errorMessage: String? = null
    ) : MiniUIState

    data class HasAccount(
        val accountList: List<MiniAccount>,
        override val isLoading: Boolean = false
    ) : MiniUIState
}
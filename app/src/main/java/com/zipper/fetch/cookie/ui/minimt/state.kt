package com.zipper.fetch.cookie.ui.minimt

import com.zipper.fetch.cookie.dao.MiniAccount
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramInitData

sealed interface MiniPageUiState {

    object Loading : MiniPageUiState

    data class Error(
        val message: String,
    ) : MiniPageUiState

    object Content : MiniPageUiState
}

sealed interface MiniAccountListUiState {
    val isLoading: Boolean

    data class Empty(
        override val isLoading: Boolean,
        val errorMessage: String? = null,
    ) : MiniAccountListUiState

    data class HasData(
        val accountUiStateList: List<MiniAccountUiState>,
        override val isLoading: Boolean = false,
    ) : MiniAccountListUiState
}

data class MiniAccountUiState constructor(
    val account: MiniAccount,
    val mini: MiniProgramInitData?,
    val isLoading: Boolean = false,
    val appointError: String? = null
)

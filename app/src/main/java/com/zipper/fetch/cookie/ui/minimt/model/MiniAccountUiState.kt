package com.zipper.fetch.cookie.ui.minimt.model

import com.zipper.fetch.cookie.dao.MiniAccount

class MiniAccountUiState(
    val account: MiniAccount,
    val isLoading: Boolean = false
) {
}
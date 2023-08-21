package com.zipper.fetch.cookie.ui.minimt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.logic.MiniProgramHelper
import com.zipper.fetch.cookie.ui.minimt.model.InitMiniProgramData
import com.zipper.fetch.cookie.dao.MiniAccount
import com.zipper.fetch.cookie.util.StoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MiniViewModel(
    private val dataStore: StoreManager,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MiniViewModelState(true))

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value.toUiState(),
    )

    val pageUiState = viewModelState.map {
        it.toPageUiState()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toPageUiState())

    init {
        initMiniProgram()
    }

    fun sendCode(initMiniProgramData: InitMiniProgramData, phone: String) {
        viewModelScope.launch {
            val keyPair = MiniProgramHelper.getInfoKey(initMiniProgramData.appId)
        }
    }

    fun initMiniProgram() {
        viewModelState.update { it.copy(pageLoading = true) }
        viewModelScope.launch {
            val miniList = UiDataProvider.miniProgramItems
            val miniProgramInitList = MiniProgramHelper.init(miniList)
            viewModelState.update { it.copy(miniProgramInitList = miniProgramInitList, pageLoading = false) }
        }
    }

    fun loadAccountList() {
        Log.e("BAAA", "Loading Account")
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {

        }
    }

    companion object {
        fun provideFactory(dataStore: StoreManager): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MiniViewModel(dataStore) as T
            }
        }
    }
}

private data class MiniViewModelState(
    val isLoading: Boolean = true,
    val pageLoading: Boolean = false,
    val accountList: List<MiniAccount> = emptyList(),
    val miniProgramInitList: List<InitMiniProgramData> = emptyList(),
) {
    fun toUiState(): MiniUIState {
        return if (accountList.isEmpty()) {
            MiniUIState.NoData(isLoading)
        } else {
            MiniUIState.HasAccount(
                accountList,
                isLoading = isLoading,
            )
        }
    }

    fun toPageUiState(): MiniPageUiState {
        if (pageLoading) {
            return MiniPageUiState.Loading
        } else if (miniProgramInitList.isNotEmpty()) {
            return MiniPageUiState.Content(miniProgramInitList)
        }
        return MiniPageUiState.Error("初始化小程序失败")
    }
}

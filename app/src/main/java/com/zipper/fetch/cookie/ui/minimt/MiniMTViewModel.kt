package com.zipper.fetch.cookie.ui.minimt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zipper.fetch.cookie.ui.minimt.model.MiniAccount
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class MiniMTViewModel(@MiniProgramType type: Int) : ViewModel() {

    private val viewModelState = MutableStateFlow(MiniMTViewModelState(true))

    init {
        loadAccount()
    }

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value.toUiState()
    )

    fun loadAccount() {
        Log.e("BAAA", "Loading Account")
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val random = Random(System.currentTimeMillis())
            val dataList = mutableListOf<MiniAccount>()
            for(i in 0 until 100) {
                dataList.add(MiniAccount("" + random.nextLong()))
            }
            viewModelState.update { it.copy(accountList = dataList, isLoading = false) }
        }
    }

    companion object {
        fun provideFactory(@MiniProgramType type: Int): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MiniMTViewModel(type) as T
            }
        }
    }
}

private data class MiniMTViewModelState(
    val isLoading: Boolean,
    val accountList: List<MiniAccount> = emptyList()
) {

    fun toUiState(): MiniUIState {
        return if (accountList.isEmpty()) {
            MiniUIState.NoData(isLoading)
        } else {
            MiniUIState.HasAccount(
                accountList,
                isLoading = isLoading
            )
        }
    }
}
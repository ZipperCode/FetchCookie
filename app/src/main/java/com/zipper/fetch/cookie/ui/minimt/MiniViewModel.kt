package com.zipper.fetch.cookie.ui.minimt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.zipper.fetch.cookie.logic.MiniProgramHelper
import com.zipper.fetch.cookie.model.MiniProgramItems
import com.zipper.fetch.cookie.ui.minimt.model.MiniAccount
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramType
import com.zipper.fetch.cookie.util.StoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt

class MiniViewModel(
    private val dataStore: StoreManager
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MiniViewModelState(true))

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value.toUiState()
    )

    init {
        loadAccountList()
    }

    fun sendCode(miniProgramItems: MiniProgramItems, phone: String) {
        viewModelScope.launch {
            val keyPair= MiniProgramHelper.getInfoKey(miniProgramItems.appId)

        }

    }

    fun loadAccountList() {
        Log.e("BAAA", "Loading Account")
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(1000)
            val random = Random(System.currentTimeMillis())
            val dataList = mutableListOf<MiniAccount>()
            val size = random.nextInt(IntRange(1, 100))
            Log.e("BAAA", "Loading size = $size")
            for(i in 0 until size) {
//                dataList.add(MiniAccount("" + random.nextLong()))
            }
            viewModelState.update { it.copy(accountList = dataList, isLoading = false) }
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
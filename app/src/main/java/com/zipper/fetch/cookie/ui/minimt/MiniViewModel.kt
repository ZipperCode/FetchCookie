package com.zipper.fetch.cookie.ui.minimt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zipper.fetch.cookie.dao.AppDataBase
import com.zipper.fetch.cookie.dao.MiniAccountDao
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.logic.MiniProgramHelper
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramInitData
import com.zipper.fetch.cookie.util.StoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files.find

class MiniViewModel(
    private val repository: MiniRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MiniViewModelState())

    val miniProgramUiState = viewModelState.map {
        it.miniProgramInitList
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val loadingMessageUiState = viewModelState.map {
        it.initLoadingMessage
    }.stateIn(viewModelScope, SharingStarted.Lazily, "加载中...")

    private val _pageUiState = MutableStateFlow<MiniPageUiState>(MiniPageUiState.Loading)

    val pageUiState get() = _pageUiState

    private val _accounts: MutableStateFlow<List<MiniAccountUiState>> = MutableStateFlow(emptyList())

    val accounts: StateFlow<List<MiniAccountUiState>> get() = _accounts

    init {
        init()
    }

    private fun init() {
        _pageUiState.update { MiniPageUiState.Loading }
        viewModelScope.launch {
//            repository.init()
            repository.init2().collect { msg ->
                viewModelState.update { it.copy(initLoadingMessage = msg) }
            }
            viewModelState.update { it.copy(miniProgramInitList = repository.miniPrograms, initLoadingMessage = null) }
            loadAccount()
        }
    }

    fun loadAccount() {
        _pageUiState.update { MiniPageUiState.Loading }
        viewModelScope.launch {
            delay(1000)
            repository.accountCount.collect {
                if (it > 0) {
                    val accountList = repository.getAllUsers()
                    _accounts.value = accountList.map { account ->
                        MiniAccountUiState(
                            account,
                            repository.miniPrograms.find { it.type == account.type },
                        )
                    }.toList()
                    _pageUiState.update { MiniPageUiState.Content }
                } else {
                    _pageUiState.update { MiniPageUiState.Error("暂无数据") }
                }
            }
        }
    }

    suspend fun sendCode(data: MiniProgramInitData, phone: String): Boolean {
        return withContext(viewModelScope.coroutineContext) {
            return@withContext repository.sendCode(data, phone)
        }
    }

    fun login(
        miniProgramInitData: MiniProgramInitData,
        phone: String, code: String,
        onLoginSuccess: () -> Unit,
        onLoginFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(miniProgramInitData, phone, code)

            if (result.isFailure) {
                onLoginFailure("登录失败 ${result.exceptionOrNull()?.message}")
            } else {
                onLoginSuccess()
            }
        }
    }

    suspend fun appoint(state: MiniAccountUiState) {
        if (state.mini == null) {
            return
        }

        val updateLoadingState: (Boolean) -> Unit = { loadingState ->
            _accounts.value = _accounts.value.map {
                if (it == state) {
                    it.copy(isLoading = loadingState)
                } else {
                    it
                }
            }
        }
        updateLoadingState(true)

        val checkLoginResult = repository.checkLogin(state.mini, state.account)

        if (checkLoginResult.isFailure) {
            return
        }

        val activity = repository.channelActivity(state.mini, state.account)
        if (activity.isFailure) {
            updateLoadingState(false)
            return
        }
        val channelInfo = activity.getOrThrow()
        if (!channelInfo.inAppointTime) {
            updateLoadingState(false)
            return
        }

        val appointResult = repository.appoint(state.mini, channelInfo.idStr, state.account)

        updateLoadingState(false)

        if (appointResult.isFailure) {
            return
        }

        if (!appointResult.getOrThrow()) {
            log("已经预约过了")
            return
        }
        log("预约成功")
    }

    companion object {
        fun provideFactory(repository: MiniRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MiniViewModel(repository) as T
            }
        }
    }

    fun log(message: String) {
        Log.d("MiniViewModel", message)
    }

    fun le(message: String) {
        Log.e("MiniViewModel", message)
    }
}

private data class MiniViewModelState(
    val initLoadingMessage: String? = null,
    val miniProgramInitList: List<MiniProgramInitData> = emptyList(),
)

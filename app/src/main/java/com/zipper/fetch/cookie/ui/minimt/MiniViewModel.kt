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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MiniViewModel(
    private val dataStore: StoreManager,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(MiniViewModelState())

    val miniProgramUiState = viewModelState.map {
        it.miniProgramInitList
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _pageUiState = MutableStateFlow<MiniPageUiState>(MiniPageUiState.Loading)

    val pageUiState get() = _pageUiState

    private val _accounts: MutableStateFlow<List<MiniAccountUiState>> = MutableStateFlow(emptyList())

    val accounts: StateFlow<List<MiniAccountUiState>> get() = _accounts

    private val accountDao: MiniAccountDao by lazy {
        AppDataBase.current.getMiniAccountDao()
    }

    init {
        init()
    }

    private fun init() {
        _pageUiState.update { MiniPageUiState.Loading }
        viewModelScope.launch {
            kotlin.runCatching {
                val miniList = UiDataProvider.miniProgramItems
                val miniProgramInitList = MiniProgramHelper.testInit(miniList)
                viewModelState.update { it.copy(miniProgramInitList = miniProgramInitList) }
                accountDao.allUsers().collect { accountList ->
                    _accounts.value = accountList.map { account ->
                        MiniAccountUiState(
                            account,
                            miniProgramInitList.find { it.type == account.type },
                        )
                    }.toList()
                }
                if (_accounts.value.isEmpty()) {
                    _pageUiState.update { MiniPageUiState.Error("暂无数据") }
                } else {
                    _pageUiState.update { MiniPageUiState.Content }
                }

            }.onFailure {
                _pageUiState.update { MiniPageUiState.Error(it.toString()) }
            }
        }
    }

    fun loadAccountList() {
        log("loadAccountList")
        viewModelScope.launch {
            val miniProgramInitList = viewModelState.value.miniProgramInitList
            accountDao.allUsers().collect { accountList ->
                _accounts.value = accountList.map { account ->
                    MiniAccountUiState(
                        account,
                        miniProgramInitList.find { it.type == account.type },
                    )
                }.toList()
            }
        }
    }

    suspend fun sendCode(data: MiniProgramInitData, phone: String): Boolean {
        return withContext(viewModelScope.coroutineContext) {
            log("发送验证码")
            val result = kotlin.runCatching {
                MiniProgramHelper.sendCode(phone, data.appId, data.sendCodeCode, data.ak, data.sk)
            }

            if (result.isFailure) {
                le(Log.getStackTraceString(result.exceptionOrNull()))
            }

            return@withContext result.isSuccess
        }
    }

    suspend fun login(miniProgramInitData: MiniProgramInitData, phone: String, code: String, onLoginSuccess: () -> Unit) {
        withContext(viewModelScope.coroutineContext) {
            delay(5000)
        }
    }

    suspend fun appoint(state: MiniAccountUiState) {
        if (state.mini == null) {
            return
        }
        val miniTokenData = MiniProgramHelper.checkLogin(
            state.mini.appId,
            state.account.token,
            state.mini.ak,
            state.mini.sk,
        )

        if (miniTokenData == null) {
            val account = state.account.copy(isExpired = true, lastOperationTime = System.currentTimeMillis())
            accountDao.update(account)

//            viewModelState.update { modelState ->
//                val list = modelState.accountList.map {
//                    if (state.account == it) {
//                        account
//                    } else {
//                        it
//                    }
//                }
//                modelState.copy(accountList = list)
//            }

            _accounts.value = accounts.value.map {
                if (it == state) {
                    it.copy(account = account)
                } else {
                    it
                }
            }
            return
        }

        val account = state.account.copy(
            isExpired = false,
            lastOperationTime = System.currentTimeMillis(),
            userInfo = miniTokenData,
        )
        accountDao.update(account)
        _accounts.value = accounts.value.map {
            if (it == state) {
                it.copy(isLoading = true, account = account)
            } else {
                it
            }
        }

        val cancelLoadingUpdateState: () -> Unit = {
            _accounts.value = accounts.value.map {
                if (it == state) {
                    it.copy(isLoading = false)
                } else {
                    it
                }
            }
        }

        val activity = MiniProgramHelper.channelActivity(
            state.mini.appId,
            state.account.token,
            state.mini.ak,
            state.mini.sk,
        )
        if (activity.isFailure) {
            cancelLoadingUpdateState()
            return
        }
        val channelInfo = activity.getOrThrow()
        if (!channelInfo.inAppointTime) {
            cancelLoadingUpdateState()
            return
        }

        val appointResult = MiniProgramHelper.appoint(
            state.mini.appId,
            channelInfo.idStr,
            state.mini.ak,
            state.mini.sk,
            state.account.token,
        )

        if (appointResult.isFailure) {
            _accounts.value = accounts.value.map {
                if (it == state) {
                    it.copy(isLoading = false, appointError = appointResult.exceptionOrNull()?.message)
                } else {
                    it
                }
            }
            return
        }
        cancelLoadingUpdateState()

        if (!appointResult.getOrThrow()) {
            log("已经预约过了")
            return
        }
        log("预约成功")
    }

    companion object {
        fun provideFactory(dataStore: StoreManager): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MiniViewModel(dataStore) as T
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
    val miniProgramInitList: List<MiniProgramInitData> = emptyList(),
)

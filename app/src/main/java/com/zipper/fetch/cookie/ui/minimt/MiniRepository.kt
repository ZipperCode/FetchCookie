package com.zipper.fetch.cookie.ui.minimt

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.zipper.fetch.cookie.dao.MiniAccount
import com.zipper.fetch.cookie.dao.MiniAccountDao
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.logic.MiniProgramHelper
import com.zipper.fetch.cookie.logic.MiniProgramHelper.isSuccess
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramConfig
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramInitData
import com.zipper.fetch.core.ext.getInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MiniRepository(
    private val dao: MiniAccountDao
) {

    private val _miniPrograms = mutableListOf<MiniProgramInitData>()

    val miniPrograms: List<MiniProgramInitData> get() = _miniPrograms

    val accountCount = flow<Long> {
        emit(dao.count())
    }

    suspend fun init() = withContext(Dispatchers.IO) {
        val miniList = UiDataProvider.miniProgramItems
        val miniProgramInitList = MiniProgramHelper.init(miniList)
        log("miniProgramInitList = $miniProgramInitList")
        _miniPrograms.addAll(miniProgramInitList)
    }

    suspend fun init2(): Flow<String> {
        val miniProgramList = UiDataProvider.miniProgramItems
        return flow {
            for (miniProgramItems in miniProgramList) {
                emit("初始化 ${miniProgramItems.text} ...")
                val result = kotlin.runCatching {
                    var errorMessage: String? = null
                    val keyPair = MiniProgramHelper.getInfoKey(miniProgramItems.appId)
                    if (keyPair != null) {
                        val (ak, sk) = keyPair
                        val channelResp = MiniProgramHelper.getChannelResp(miniProgramItems.appId, ak, sk)
                        if (channelResp.isSuccess()) {
                            val channel = channelResp.getInt("data")
                            val data = MiniProgramInitData(
                                miniProgramItems.type,
                                miniProgramItems.appId,
                                miniProgramItems.text,
                                channel,
                                ak,
                                sk,
                                miniProgramItems.sendCodeCode,
                                miniProgramItems.phoneLoginCode,
                            )
                            _miniPrograms.add(data)
                            emit("初始化 ${miniProgramItems.text} 完成")
                            return@runCatching
                        }
                        errorMessage = "Channel 初始化失败"
                    } else {
                        errorMessage = "获取配置信息失败"
                    }
                    throw IllegalStateException(errorMessage)
                }

                if (result.isFailure) {
                    result.exceptionOrNull()?.printStackTrace()
                    emit("初始化 ${miniProgramItems.text} 失败: ${result.exceptionOrNull()?.message}")
                    delay(500)
                }
            }
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getAllUsers() = withContext(Dispatchers.IO) {
        return@withContext dao.getAllUsers()
    }

    suspend fun sendCode(data: MiniProgramInitData, phone: String): Boolean = withContext(Dispatchers.IO) {
        log("发送验证码")
        val result = kotlin.runCatching {
            MiniProgramHelper.sendCode(phone, data.appId, data.sendCodeCode, data.ak, data.sk)
        }
        log("发送验证码, result = $result")
        if (result.isFailure) {
            le(Log.getStackTraceString(result.exceptionOrNull()))
        }

        return@withContext result.isSuccess
    }

    suspend fun login(
        miniProgramInitData: MiniProgramInitData,
        phone: String, code: String
    ): Result<MiniAccount> = withContext(Dispatchers.IO) {

        return@withContext kotlin.runCatching {
            val token = MiniProgramHelper.login(
                phone, code,
                miniProgramInitData.appId,
                miniProgramInitData.phoneLoginCode,
                miniProgramInitData.ak,
                miniProgramInitData.sk
            )

            var user = dao.get(miniProgramInitData.type, phone)

            if (user == null) {
                user = MiniAccount(
                    phone = phone,
                    token = token,
                    type = miniProgramInitData.type,
                    isExpired = false,
                    lastOperationTime = System.currentTimeMillis()
                )
                dao.insert(user)
            } else {
                dao.update(
                    user.copy(
                        token = token,
                        isExpired = false,
                        lastOperationTime = System.currentTimeMillis()
                    )
                )
            }

            return@runCatching user
        }
    }

    suspend fun checkLogin(miniProgram: MiniProgramInitData, account: MiniAccount): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            val miniTokenData = MiniProgramHelper.checkLogin(
                miniProgram.appId,
                account.token,
                miniProgram.ak,
                miniProgram.sk,
            )
            miniTokenData?.run {
                dao.update(
                    account.copy(
                        isExpired = false,
                        lastOperationTime = System.currentTimeMillis(),
                        userInfo = this,
                    )
                )
            }
            miniTokenData == null
        }
    }

    suspend fun channelActivity(miniProgram: MiniProgramInitData, account: MiniAccount) = withContext(Dispatchers.IO) {
        return@withContext MiniProgramHelper.channelActivity(
            miniProgram.appId,
            account.token,
            miniProgram.ak,
            miniProgram.sk,
        )
    }

    suspend fun appoint(miniProgram: MiniProgramInitData, activityId: String, account: MiniAccount) = withContext(Dispatchers.IO) {
        return@withContext MiniProgramHelper.appoint(
            miniProgram.appId,
            activityId,
            miniProgram.ak,
            miniProgram.sk,
            account.token
        )
    }

    fun log(message: String) {
        Log.d("MiniRepository", message)
    }

    fun le(message: String) {
        Log.e("MiniRepository", message)
    }
}
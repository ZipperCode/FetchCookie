package com.zipper.fetch.cookie.logic

import com.zipper.fetch.cookie.ui.minimt.model.InitMiniProgramData
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramConfig
import com.zipper.fetch.cookie.ui.minimt.model.MiniChannelInfo
import com.zipper.fetch.cookie.ui.minimt.model.MiniTokenData
import com.zipper.fetch.core.ext.awaitResponse
import com.zipper.fetch.core.ext.base64
import com.zipper.fetch.core.ext.getBoolean
import com.zipper.fetch.core.ext.getInt
import com.zipper.fetch.core.ext.getLong
import com.zipper.fetch.core.ext.getMap
import com.zipper.fetch.core.ext.getString
import com.zipper.fetch.core.ext.globalGson
import com.zipper.fetch.core.ext.globalHttpClient
import com.zipper.fetch.core.ext.hmac
import com.zipper.fetch.core.ext.string
import com.zipper.fetch.core.ext.toJsonRequestBody
import com.zipper.fetch.core.ext.typeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object MiniProgramHelper {
    private val commonHeaders = mutableMapOf(
        "content-type" to "application/json",
        "Referer" to "https://hqmall.huiqunchina.com/",
        "Sec-Fetch-Dest" to "empty",
        "Sec-Fetch-Mode" to "cors",
        "Sec-Fetch-Site" to "cross-site",
        "xweb_xhr" to "1",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF XWEB/6945",
    )

    /**
     * ak: betweenTime
     */
    private val betweenTimeMap: MutableMap<String, Long> = mutableMapOf()

    /**
     * ak: Channel
     */
    private val channelMap: MutableMap<String, Int> = mutableMapOf()

    /**
     * 小程序初始化
     */
    suspend fun init(miniProgramList: List<MiniProgramConfig>): List<InitMiniProgramData> {
        return withContext(Dispatchers.IO) {
            val tasks = mutableListOf<Deferred<InitMiniProgramData?>>()
            for (miniProgramItems in miniProgramList) {
                //
                val task: Deferred<InitMiniProgramData?> = async {
                    val keyPair = getInfoKey(miniProgramItems.appId)
                    if (keyPair != null) {
                        val (ak, sk) = keyPair
                        val channelResp = getChannelResp(miniProgramItems.appId, ak, sk)
                        if (channelResp.isSuccess()) {
                            val channel = channelResp.getInt("data")
                            return@async InitMiniProgramData(
                                miniProgramItems.appId,
                                miniProgramItems.text,
                                channel,
                                ak,
                                sk,
                            )
                        }
                    }
                    return@async null
                }
                tasks.add(task)
            }

            return@withContext tasks.mapNotNull { it.await() }.toList()
        }
    }

    suspend fun getInfo(appId: String): Map<String, Any>? {
        val body = """{"appId":"$appId"}"""
        val request = Request.Builder()
            .url("https://callback.huiqunchina.com/api/getInfo")
            .post(body.toJsonRequestBody())
            .build()
        val response = globalHttpClient.newCall(request).awaitResponse()
        if (response.isSuccessful) {
            val string = response.body?.string() ?: return null
            return globalGson.fromJson<Map<String, Any>>(string, typeToken<Map<String, Any>>()) ?: return null
        }
        println("Failed to get info resp = ${response.body?.string()}")
        return null
    }

    suspend fun getInfoKey(appId: String): Pair<String, String>? {
        val infoResp = getInfo(appId) ?: return null
        if (!infoResp.isSuccess()) {
            return null
        }
        val ak = infoResp.getString("ak")
        val sk = infoResp.getString("sk")
        if (ak.isEmpty() || sk.isEmpty()) {
            return null
        }
        return Pair(ak, sk)
    }

    private suspend fun getChannelResp(appId: String, ak: String, sk: String): Map<String, Any>? {
        return post(
            "/front-manager/api/get/channelId",
            """{"appId":"$appId"}""",
            0,
            ak,
            sk,
        )
    }

    private suspend fun getChannelBetweenTime(appId: String, ak: String, sk: String): Long {
        if (!betweenTimeMap.containsKey(ak) || betweenTimeMap[ak] == 0L) {
            val resp = post(
                "/front-manager/api/get/channelId",
                """{"appId":"$appId"}""",
                0,
                ak,
                sk,
            )

            if (resp.isSuccess()) {
                val data = resp?.get("data")
                if (data is Number) {
                    channelMap[ak] = data.toInt()
                }
            }
        }
        return betweenTimeMap[ak] ?: 0
    }

    /**
     * 检查是否登录
     */
    suspend fun checkLogin(appId: String, token: String): Result<MiniTokenData> {
        val tokenMap = mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "X-access-token" to token,
        )

        val (ak, sk) = getInfoKey(appId)
            ?: return Result.failure(Exception("getInfo ak or sk is null"))

        val betweenTime = getChannelBetweenTime(appId, ak, sk)

        val queryTokenResp = post(
            "/front-manager/api/customer/queryById/token",
            """{"channel":"h5"}""",
            betweenTime,
            ak,
            sk,
            tokenMap,
        ) ?: return Result.failure(Exception("queryTokenResp is null "))

        if (!queryTokenResp.isSuccess()) {
            return Result.failure(Exception("queryToken is not success ${queryTokenResp.message()}"))
        }

        val openId = queryTokenResp.getString("openId")
        val realName = queryTokenResp.getString("realName")
        val phone = queryTokenResp.getString("phone")
        val isRealNameAuth = queryTokenResp.getBoolean("isRealNameAuth")
        val idCard = queryTokenResp.getString("idcard")
        val phoneIsBind = queryTokenResp.getBoolean("phoneIsBind")
        val status = queryTokenResp.getInt("status")

        return Result.success(
            MiniTokenData(
                openId,
                realName,
                phone,
                isRealNameAuth,
                idCard,
                phoneIsBind,
                status,
            ),
        )
    }

    suspend fun sendCode(phone: String, appId: String, code: String, ak: String, sk: String): Boolean {
        val url = "/front-manager/api/login/sendSecurityCode"
        val body = """{"phone":"$phone","code":"$code","appId":"$appId"}"""
        val betweenTime = getChannelBetweenTime(appId, ak, sk)
        return post(url, body, betweenTime, ak, sk).isSuccess()
    }

    suspend fun login(
        phone: String,
        verifyCode: String,
        appId: String,
        code: String,
        betweenTime: Long,
        ak: String,
        sk: String,
    ): String {
        val url = "/front-manager/api/login/phoneLogin"
        val body = """{"phone":"$phone","securityCode":"$verifyCode","appId":"$appId","code":"$code"}"""
        val resp = post(url, body, betweenTime, ak, sk)
        return resp.getMap("data").getString("token")
    }

    /**
     * 获取活动
     */
    suspend fun channelActivity(appId: String, token: String, ak: String, sk: String): Result<MiniChannelInfo> {
        val url = "/front-manager/api/customer/promotion/channelActivity"
        val body = """{}"""
        val tokenMap = mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "X-access-token" to token,
        )
        val betweenTime = getChannelBetweenTime(appId, ak, sk)
        val resp = post(url, body, betweenTime, ak, sk, tokenMap)
        if (!resp.isSuccess()) {
            return Result.failure(Exception("Error getChannelActivity $resp"))
        }
        val data = resp.getMap("data")
        val name = data.getString("name")
        val id = data.getInt("id")
        val startTime = data.getLong("startTime")
        val endTime = data.getLong("endTime")
        val sysCurrentTime = data.getLong("sysCurrentTime")
        val appointStartTime = data.getLong("appointStartTime")
        val appointEndTime = data.getLong("appointEndTime")
        val drawTime = data.getLong("drawTime")
        val purchaseStartTime = data.getLong("purchaseStartTime")
        val purchaseEndTime = data.getLong("purchaseEndTime")
        val appointCounts = data.getInt("appointCounts")
        val isAppoint = data.getInt("isAppoint")

        return Result.success(
            MiniChannelInfo(
                id,
                name,
                startTime,
                endTime,
                sysCurrentTime,
                appointStartTime,
                appointEndTime,
                drawTime,
                purchaseStartTime,
                purchaseEndTime,
                appointCounts,
                isAppoint,
            ),
        )
    }

    /**
     * 检查是否预约
     */
    suspend fun checkConsumer(appId: String, activityId: String, ak: String, sk: String, token: String): Boolean {
        val betweenTime = getChannelBetweenTime(appId, ak, sk)
        val channelId = channelMap[ak] ?: 0
        val url = "/front-manager/api/customer/promotion/checkCustomerInQianggou"
        val body = """{"activityId":$activityId,"channelId":$channelId}"""

        val tokenMap = mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "X-access-token" to token,
        )
        return post(url, body, betweenTime, ak, sk, tokenMap).isSuccess()
    }

    suspend fun appoint(appId: String, activityId: String, ak: String, sk: String, token: String): Result<Boolean> {
        if (checkConsumer(appId, activityId, ak, sk, token)) {
            // 已经预约过了
            return Result.success(false)
        }
        val betweenTime = getChannelBetweenTime(appId, ak, sk)
        val channelId = channelMap[ak] ?: 0
        val url = "/front-manager/api/customer/promotion/appoint"
        val body = """{"activityId":$activityId,"channelId":$channelId}"""
        val tokenMap = mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "X-access-token" to token,
        )
        return Result.success(post(url, body, betweenTime, ak, sk, tokenMap).isSuccess())
    }

    suspend fun post(
        url: String,
        body: String,
        serviceBetweenTime: Long,
        ak: String,
        sk: String,
        headers: Map<String, String> = mutableMapOf(),
    ): Map<String, Any>? {
        val requestHeaders = HashMap(commonHeaders)
        requestHeaders.putAll(headers)
        requestHeaders.putAll(Crypto.encryptionData("POST", url, body, serviceBetweenTime, ak, sk))
        val request = Request.Builder()
            .url(url)
            .post(body.toJsonRequestBody())
            .headers(requestHeaders.toHeaders())
            .build()

        val resp = getResp(globalHttpClient.newCall(request).awaitResponse())
        resp?.get("serverTimeStamp")?.let {
            val serviceTime = (it as Double).toLong()
            val currentTime = System.currentTimeMillis()
            val betweenTime = serviceTime - currentTime
            betweenTimeMap[ak] = betweenTime
        }
        return resp
    }

    private fun getResp(response: Response): Map<String, Any>? {
        if (!response.isSuccessful) {
            println("response is not successful code = ${response.code} msg = ${response.body?.string()}")
            return null
        }

        val string = response.body?.string() ?: return null
        println("response body = $string")
        return globalGson.fromJson(string, typeToken<Map<String, Any>>()) ?: return null
    }

    private fun Map<String, Any>?.isSuccess(): Boolean {
        if (this == null) {
            return false
        }
        return get("code") == "10000"
    }

    private fun Map<String, Any>?.message(): String {
        if (this == null) {
            return ""
        }
        return get("message").toString()
    }

    object Crypto {
        private val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)

        init {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
        }

        /**
         * X-HMAC-SIGNATURE
         */
        private fun signature(method: String, url: String, ak: String, sk: String, date: String): String {
            val text = method + "\n" + url + "\n\n" + ak + "\n" + date + "\n"
            return text.toByteArray().hmac(sk.toByteArray()).base64().string()
        }

        /**
         * X-HMAC-DIGEST
         */
        private fun digest(body: String, sk: String): String {
            return body.hmac(sk).base64().string()
        }

        fun encryptionData(
            method: String,
            url: String,
            body: String,
            betweenTime: Long,
            ak: String,
            sk: String,
        ): MutableMap<String, String> {
            val date = sdf.format(Date(System.currentTimeMillis() + betweenTime))
            val signature = signature(method, url, ak, sk, date)
            val digest = digest(body, sk)
            return mutableMapOf(
                "X-HMAC-SIGNATURE" to signature,
                "X-HMAC-ACCESS-KEY" to ak,
                "X-HMAC-ALGORITHM" to "hmac-sha256",
                "X-HMAC-DIGEST" to digest,
                "X-HMAC-Date" to date,
            )
        }
    }
}

package com.zipper.fetch.maotai.zhcs

import com.zipper.fetch.core.ext.awaitResponse
import com.zipper.fetch.core.ext.getBoolean
import com.zipper.fetch.core.ext.getInt
import com.zipper.fetch.core.ext.getLong
import com.zipper.fetch.core.ext.getMap
import com.zipper.fetch.core.ext.getString
import com.zipper.fetch.core.ext.globalGson
import com.zipper.fetch.core.ext.globalHttpClient
import com.zipper.fetch.core.ext.toDateFmt
import com.zipper.fetch.core.ext.toJsonRequestBody
import com.zipper.fetch.core.ext.typeToken
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request
import okhttp3.Response

class MiniProgramMaoTai(
    private val config: MiniProgram,
) {

    private val commonHeaders = mutableMapOf(
        "content-type" to "application/json",
        "Referer" to "https://hqmall.huiqunchina.com/",
        "Sec-Fetch-Dest" to "empty",
        "Sec-Fetch-Mode" to "cors",
        "Sec-Fetch-Site" to "cross-site",
        "xweb_xhr" to "1",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF XWEB/6945"
    )

    private var token: String = ""

    private var serviceBetweenTime: Long = 0

    private var ak: String = config.ak
    private var sk: String = config.sk

    suspend fun getInfo(): Boolean {
        val body = """{"appId":"${config.appId}"}"""
        val request = Request.Builder()
            .url("https://callback.huiqunchina.com/api/getInfo")
            .post(body.toJsonRequestBody())
            .build()
        val response = globalHttpClient.newCall(request).awaitResponse()
        if (response.isSuccessful) {
            val string = response.body?.string() ?: return false
            val resp = globalGson.fromJson<Map<String, Any>>(string, typeToken<Map<String, Any>>()) ?: return false
            if (resp["code"] != "10000") {
                return false
            }
            val data = resp["data"] as Map<*, *>? ?: return false
            ak = data["ak"] as String? ?: return false
            sk = data["sk"] as String? ?: return false
            return getChannel()
        }
        println("Failed to get info resp = ${response.body?.string()}")
        return false
    }

    private suspend fun getChannel(): Boolean {
        val resp = post("/front-manager/api/get/channelId", """{"appId":"wx624149b74233c99a"}""")
        if (!resp.isSuccess()) {
            return false
        }
        val data = resp?.get("data")
        if (data is Number) {
            val channel = resp.getInt("data")
        } else if (data is Map<*, *>) {
            val openId = data.getString("openId")
            val realName = data.getString("realName")
            val phone = data.getString("phone")
            val isRealNameAuth = data.getBoolean("isRealNameAuth")
            val idCard = data.getString("idcard")
            val phoneIsBind = data.getBoolean("phoneIsBind")
            val status = data.getInt("status")
        }

        return true
    }

    suspend fun queryByIdToken(): Boolean {
        val url = "/front-manager/api/customer/queryById/token"
        val body = """{"channel":"h5"}"""
        val resp = post(url, body)
        if (resp?.get("code")?.toString() == "401") {
            println("未登录")
            return false
        }
        return true
    }

    suspend fun sendCode(phone: String): Boolean {
        val url = "/front-manager/api/login/sendSecurityCode"
        val body = """{"phone":"$phone","code":"0d1iSrFa1yx2SF0XGaHa1aWD7I2iSrFB","appId":"${config.appId}"}"""
        return post(url, body).isSuccess()
    }

    suspend fun login(phone: String, code: String) {
        val url = "/front-manager/api/login/phoneLogin"
        val body = """{"phone":"$phone","securityCode":"$code","appId":"${config.appId}","code":"0c1D4hGa12ERSF0xUAHa1fmYeY0D4hGw"}"""
        val resp = post(url, body)
        if (resp.isSuccess()) {
            // 存储token
            val data = resp?.get("data") as Map<*, *>?
            token = data.getString("token")
            // TODO 保存token

        }
    }

    suspend fun channelActivity() {
        val url = "/front-manager/api/customer/promotion/channelActivity"
        val body = """{}"""
        val resp = post(url, body)
        if (resp.isSuccess()) {
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

            println(
                """
                活动编号        = $id
                活动名称        = $name
                是否预约        = ${if (isAppoint == 1) "是" else "否"}
                服务时间        = ${sysCurrentTime.toDateFmt()}
                活动时间        = ${startTime.toDateFmt()} ~ ${endTime.toDateFmt()}
                预约人数        = $appointCounts
                预约时间        = ${appointStartTime.toDateFmt()} ~ ${appointEndTime.toDateFmt()}
                开奖时间        = ${drawTime.toDateFmt()}
                下单时间        = ${purchaseStartTime.toDateFmt()} ~ ${purchaseEndTime.toDateFmt()}
            """.trimIndent()
            )

            if (isAppoint == 0 && sysCurrentTime in appointStartTime until appointEndTime) {
                println("处于活动时间内，且未预约，处理预约")
            }
        }
    }

    /**
     * 预约
     */
    suspend fun appoint() {
        val url = "/front-manager/api/customer/promotion/appoint"
        val body = """{"activityId":374,"channelId":3}"""
        val resp = post(url, body, mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "Referer" to "https://hqmall.huiqunchina.com/",
        ))
        if (resp.isSuccess()) {
            if (resp?.get("data") == true) {
                // 成功
            }
        }
    }

    /**
     * 检查是否预约成功
     */
    suspend fun checkCustomer() {
        val url = "/front-manager/api/customer/promotion/checkCustomerInQianggou"
        val body = """{"activityId":374,"channelId":3}"""
        val resp = post(url, body, mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "Referer" to "https://hqmall.huiqunchina.com/",
        ))

        if (resp.isSuccess()) {
            if (resp?.get("data") == true) {
                // 成功
            }
        }
    }

    suspend fun getChannelInfo() {
        val url = "/front-manager/api/get/getChannelInfoId"
        val body = """{"appId":"wx5508e31ffe9366b8"}"""
        val resp = post(url, body, mapOf(
            "Channel" to "miniapp",
            "DataType" to "json",
            "Referer" to "https://hqmall.huiqunchina.com/",
        ))

        val data = resp.getMap("data")

        val channelId = data.getInt("channelId")
        val companyName = data.getString("companyName")
    }

    private suspend fun post(url: String, body: String, extHeader: Map<String, String> = emptyMap()): Map<String, Any>? {
        val headers = Crypto.encryptionData("POST", url, body, serviceBetweenTime, ak, sk)
        headers.putAll(commonHeaders)

        if (token.isNotEmpty()) {
            headers["X-access-token"] = token
        }
        if (extHeader.isNotEmpty()) {
            headers.putAll(extHeader)
        }

        val request = Request.Builder()
            .url(config.host + url)
            .post(body.toJsonRequestBody())
            .headers(headers.toHeaders())
            .build()
        val resp = getResp(globalHttpClient.newCall(request).awaitResponse())
        resp?.get("serverTimeStamp")?.let {
            val serviceTime = (it as Double).toLong()
            val currentTime = System.currentTimeMillis()
            serviceBetweenTime = serviceTime - currentTime
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
}
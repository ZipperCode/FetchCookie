package com.zipper.fetch.imaotai

import com.zipper.fetch.core.ext.appendExtHeader
import com.zipper.fetch.core.ext.awaitResponse
import com.zipper.fetch.core.ext.hex
import com.zipper.fetch.core.ext.md5
import com.zipper.fetch.core.ext.secondTimeStr
import com.zipper.fetch.core.ext.toFormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/18
 */
object Api {

    private val commonHeader = """
MT-Lat: 28.499562
MT-K: 1675213490331
MT-Lng: 102.182324
Host: app.moutai519.com.cn
MT-User-Tag: 0
Accept: */*
MT-Network-Type: WIFI
MT-Token: 1
MT-Team-ID: 
MT-Info: 028e7f96f6369cafe1d105579c5b9377
MT-Device-ID: 2F2075D0-B66C-4287-A903-DBFF6358342A
MT-Bundle-ID: com.moutai.mall
Accept-Language: en-CN;q=1, zh-Hans-CN;q=0.9
MT-Request-ID: 167560018873318465
MT-APP-Version: 1.3.7
User-Agent: iOS;16.3;Apple;?unrecognized?
MT-R: clips_OlU6TmFRag5rCXwbNAQ/Tz1SKlN8THcecBp/HGhHdw==
Content-Length: 93
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Content-Type: application/json
userId: 2
    """.trimIndent()

    private val headers: Map<String, String> by lazy {
        val headerMap = mutableMapOf<String, String>()
        val split = commonHeader.split("\n")
        for (s in split) {
            val ss = s.split(":")
            if (ss.size < 2) {
                continue
            }
            headerMap[ss[0]] = ss[1]
        }
        headerMap
    }

    private var newVersion: String = "1.3.7"

    private val httpClient = OkHttpClient.Builder().build()

    suspend fun sendCode(mobile: String, version: String = "1.3.7"): String? {
        val param = mutableMapOf<String, Any>(
            "mobile" to mobile,
        )
        signature(param)

        val request = Request.Builder()
            .url("https://app.moutai519.com.cn/xhr/front/user/register/vcode")
            .post(param.toFormBody())
            .appendExtHeader(headers)
            .build()
        val response = httpClient.newCall(request).awaitResponse()
        if (response.isSuccessful) {
            return response.body?.string()
        }
        return null
    }

    suspend fun login(mobile: String, code: String): Response {
        val param = mutableMapOf<String, Any>(
            "mobile" to mobile,
            "vCode" to code,
            "ydToken" to "",
            "ydLogId" to "",
        )
        signature(param)

        val request = Request.Builder()
            .url("https://app.moutai519.com.cn/xhr/front/user/register/login")
            .post(param.toFormBody())
            .appendExtHeader(headers)
            .build()

        return httpClient.newCall(request).awaitResponse()
    }

    private fun signature(param: MutableMap<String, Any>) {
        val timeStr = secondTimeStr
        val sortKeys = param.keys.sorted()
        val sb = StringBuilder(SALT)
        for (sortKey in sortKeys) {
            val value = param[sortKey]
            sb.append(value.toString())
        }
        val md5 = sb.append(timeStr).toString().toByteArray(Charsets.UTF_8).md5().hex()
        param["md5"] = md5
        param["timestamp"] = timeStr
        param["MT-APP-Version"] = newVersion
    }
}

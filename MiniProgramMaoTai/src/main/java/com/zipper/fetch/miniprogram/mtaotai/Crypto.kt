package com.zipper.fetch.miniprogram.mtaotai

import com.zipper.fetch.core.ext.base64
import com.zipper.fetch.core.ext.hmac
import com.zipper.fetch.core.ext.string
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


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


    fun encryptionData(method: String, url: String, body: String, betweenTime: Long, ak: String, sk: String): MutableMap<String, String> {
        val date = sdf.format(Date(System.currentTimeMillis() + betweenTime))
        val signature = signature(method, url, ak, sk, date)
        val digest = digest(body, sk)
        return mutableMapOf(
            "X-HMAC-SIGNATURE" to signature,
            "X-HMAC-ACCESS-KEY" to ak,
            "X-HMAC-ALGORITHM" to "hmac-sha256",
            "X-HMAC-DIGEST" to digest,
            "X-HMAC-Date" to date
        )
    }
}
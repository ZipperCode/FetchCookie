package com.zipper.fetch.core.ext

import com.zipper.fetch.core.Base64
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

fun String.unHex(): ByteArray {
    val bytes = ByteArray(length / 2)
    for (i in 0 until length / 2) {
        bytes[i] = substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return bytes
}


fun String.base64(flag: Int = 0): String {
    return toByteArray(Charsets.UTF_8).base64(flag)
}

fun String.decBase64(flag: Int = 0): ByteArray {
    return Base64.decode(this, flag)
}

fun String.urlEncode(enc: String = "UTF-8"): String {
    return URLEncoder.encode(this, enc)
}

fun String.urlDecode(enc: String = "UTF-8"): String {
    return URLDecoder.decode(this, enc)
}

fun String.charset(): Charset {
    return try {
        Charset.forName(this)
    } catch (e: Exception) {
        Charset.forName("UTF-8")
    }
}

fun String.formatGetParam(): Map<String, String> {
    val uri = URI(this)
    return uri.rawQuery.formatParam()
}

fun String.formatParam(): Map<String, String> {
    val param = mutableMapOf<String, String>()
    split("&").forEach {
        val keyValues = it.split("=")
        if (keyValues.size == 2) {
            param[keyValues[0]] = keyValues[1]
        } else if (keyValues.size == 1) {
            param[keyValues[0]] = ""
        }
    }
    return param
}

var MEDIA_JSON: MediaType? = "application/json;charset=utf-8".toMediaTypeOrNull()


package com.zipper.fetch.core.ext

import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.floor

const val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

val millsTime: Long get() = Date().time

val secondTime: Long get() = Date().time / 1000

val millsTimeStr: String get() = "$millsTime"

val secondTimeStr: String get() = "$secondTime"

val uuidStr: String get() = UUID.randomUUID().toString().replace("-", "")

fun randomChar(): Char {
    return chars.random()
}

val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).apply {
    timeZone = TimeZone.getTimeZone("Asia/Shanghai")
}

fun Long.toDateFmt(): String {
    return dateFmt.format(Date(this))
}

fun Any?.asMap(): Map<*, *>? {
    return this as Map<*, *>?
}

fun Map<*, *>?.getString(key: Any): String {
    return getNoNull(key, "")
}

fun Map<*, *>?.getBoolean(key: Any): Boolean {
    return getNoNull(key, false)
}

fun Map<*, *>?.getInt(key: Any): Int {
    return getNumber(key, 0).toInt()
}

fun Map<*, *>?.getLong(key: Any): Long {
    return getNumber(key, 0).toLong()
}

inline fun <reified T : Number> Map<*, *>?.getNumber(key: Any, default: T): Number {
    if (this == null) {
        return default
    }
    val res = get(key)

    if (res is Number) {
        return res
    }
    return default
}

fun Map<*, *>?.getMap(key: Any): Map<*, *> {
    return if (this == null) mapOf<Any, Any>() else get(key) as Map<*, *>
}

inline fun <reified T> Map<*, *>?.getNoNull(key: Any, default: T): T {
    return if (this == null) default else get(key) as T
}

fun String.formatDate(date: Date = Date()): String {
    val defaultFormat = "yyyy-MM-dd"
    return try {
        SimpleDateFormat(this).format(date)
    } catch (e: Exception) {
        SimpleDateFormat(defaultFormat).format(date)
    }
}

fun String.toDate(format: String = "yyyy-MM-dd"): Date {
    return try {
        SimpleDateFormat(format).parse(this)
    } catch (e: Exception) {
        Date()
    }
}

fun Map<String, Any>.toUrlParam(): String {
    if (isEmpty()) {
        return ""
    }
    val builder = StringBuilder()
    forEach { (k, v) ->
        builder.append(k).append("=").append(v).append("&")
    }
    return builder.deleteCharAt(builder.length - 1).toString()
}

fun randomInt(min: Int = 0, max: Int = 100): Int {
    return kotlin.math.min(floor(min + Math.random() * (max - min)).toInt(), max)
}

suspend fun delaySecond(timeSecond: Int) {
    delay(timeSecond * 1000L)
}

suspend fun delayRandomSecond(timeSecondStart: Int = 5, timeSecondEnd: Int = 10) {
    val randomBoolean = Random().nextBoolean()
    if (randomBoolean) {
        delaySecond((timeSecondStart + (Math.random() * (timeSecondEnd - timeSecondStart)).toInt()))
    } else {
        delaySecond((timeSecondStart + 1 - (Math.random() * (timeSecondEnd - timeSecondStart)).toInt()))
    }
}

suspend fun <T> catchException(block: suspend () -> T?): T? {
    return try {
        block.invoke()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

suspend fun Call.awaitResponse(): okhttp3.Response {
    return suspendCoroutine {
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                try {
                    it.resume(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.resumeWithException(e)
                }
            }
        })
    }
}

fun Map<String, String>.toCookieValue(): String {
    val stringBuilder = StringBuilder()
    forEach { (key, value) ->
        stringBuilder.append(key).append("=").append(value).append(";")
    }
    return stringBuilder.toString()
}

fun Request.getParam(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    if ("GET".equals(method, true)) {
        for (i in 0 until url.querySize) {
            val name = url.queryParameterName(i)
            val value = url.queryParameterValue(i)
            if (name.isNotEmpty()) {
                result[name] = value ?: ""
            }
        }
    } else if ("POST".equals(method, true)) {
        val tBody = body
        if (tBody is FormBody) {
            for (i in 0 until tBody.size) {
                if (tBody.name(i).isNotEmpty() && tBody.value(i).isNotEmpty()) {
                    result[tBody.name(i)] = tBody.value(i)
                }
            }
        } else if (tBody is MultipartBody) {
            val mulBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (i in 0 until tBody.size) {
                mulBody.addPart(tBody.part(i))
                val formBody = tBody.part(i).body
                if (formBody is FormBody) {
                    for (j in 0 until formBody.size) {
                        if (formBody.name(j).isNotEmpty() && formBody.value(j).isNotEmpty()) {
                            result[formBody.name(j)] = formBody.value(j)
                        }
                    }
                }
            }
        }
    }
    return result
}

fun Request.Builder.appendExtHeader(extHeader: Map<String, String>) = apply {
    extHeader.forEach { (k, v) ->
        if (k.isNotEmpty() && v.isNotEmpty()) {
            addHeader(k, v)
        }
    }
}

fun Request.Builder.appendExtHeader(oldRequest: Request, extHeader: Map<String, String>) = apply {
    extHeader.forEach { (k, v) ->
        if (k.isNotEmpty() && v.isNotEmpty()) {
            if (!oldRequest.header(k).isNullOrEmpty()) {
                addHeader(k, v)
            }
        }
    }
}

fun Request.Builder.appendExtParam(oldRequest: Request, extParam: Map<String, String>) = apply {
    if ("GET".equals(oldRequest.method, true)) {
        val urlBuilder = oldRequest.url.newBuilder()
        extParam.forEach {
            if (oldRequest.url.queryParameter(it.key).isNullOrEmpty()) {
                urlBuilder.addQueryParameter(it.key, it.value)
            }
        }
        url(urlBuilder.build())
    } else if ("POST".equals(oldRequest.method, true)) {
        val body = oldRequest.body
        if (body is FormBody) {
            val bodyBuilder = FormBody.Builder()
            extParam.forEach {
                if (it.key.isNotEmpty() && it.value.isNotEmpty()) {
                    bodyBuilder.add(it.key, it.value)
                }
            }
            for (i in 0 until body.size) {
                if (body.name(i).isNotEmpty()) {
                    bodyBuilder.add(body.name(i), body.value(i) ?: "")
                }
            }
            post(bodyBuilder.build())
        } else if (body is MultipartBody) {
            val mulBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            extParam.forEach { (k, v) ->
                if (k.isNotEmpty() && v.isNotEmpty()) {
                    mulBody.addFormDataPart(k, v)
                }
            }
            for (i in 0 until body.size) {
                mulBody.addPart(body.part(i))
            }
            post(mulBody.build())
        }
    }
}

fun Request.getParam(key: String): String? {
    if ("GET".equals(method, true)) {
        return url.queryParameter(key)
    } else if ("POST".equals(method, true)) {
        val vBody = body
        if (vBody is FormBody) {
            for (i in 0 until vBody.size) {
                if (key.contentEquals(vBody.name(i))) {
                    return vBody.value(i)
                }
            }
        }
    }
    return null
}

package com.zipper.fetch.core.ext

import com.zipper.fetch.core.Base64
import java.nio.charset.Charset
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

fun ByteArray.hex(): String {
    val stringBuffer = StringBuilder()
    for (datum in this) {
        stringBuffer.append(String.format("%02X", datum.toInt() and 0xFF))
    }
    return stringBuffer.toString()
}

fun ByteArray.base64(flag: Int = 0): ByteArray {
    return Base64.encode(this, flag)
}

fun ByteArray.string(charset: Charset = Charsets.UTF_8): String {
    return String(this, charset)
}

fun ByteArray.md5(): ByteArray {
    return try {
        MessageDigest.getInstance("MD5").digest(this)
    } catch (e: Exception) {
        e.printStackTrace()
        ByteArray(0)
    }
}

fun Byte.hex(): String {
    var tmp = toInt()
    if (tmp < 0) {
        tmp = this + 256
    }
    return String.format("%02X", tmp and 0xFF)
}

fun ByteArray.hmac(key: ByteArray, algorithm: String = "HmacSHA256"): ByteArray {
    val skey: SecretKey = SecretKeySpec(key, algorithm)
    val mac = Mac.getInstance(algorithm)
    mac.init(skey)
    mac.update(this)
    return mac.doFinal()
}

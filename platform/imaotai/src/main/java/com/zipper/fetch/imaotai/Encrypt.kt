package com.zipper.fetch.imaotai

import com.zipper.fetch.imaotai.Encrypt.unHex
import java.awt.SystemColor.text
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 *
 * @author zhangzhipeng
 * @date 2023/8/18
 */

const val AES_KEY = "qbhajinldepmucsonaaaccgypwuvcjaa"
const val AES_IV = "2018534749963515"
const val SALT = "2af72f100c356273d46284f6fd1dfc08"

const val AMAP_KEY = "9449339b6c4aee04d69481e6e6c84a84"

object Encrypt {

    private val key = AES_KEY.toByteArray(Charsets.UTF_8)
    private val iv = AES_IV.toByteArray(Charsets.UTF_8)

    private val secretKeySpec = SecretKeySpec(key, "AES")
    private val ivParameterSpec = IvParameterSpec(iv)

    private fun pkCs7Padding(text: String): String {
        val padding = 16 - (text.length % 16)
        return text + " ".repeat(padding)
    }

    fun encrypt(content: String): String {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val data = pkCs7Padding(content).toByteArray(Charsets.UTF_8)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        return cipher.doFinal(data).base64()
    }

    fun decrypt(encryptContent: String): String {
        val encryptBytes = encryptContent.unBase64()
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        return String(cipher.doFinal(encryptBytes), Charsets.UTF_8).trim()
    }

    private fun ByteArray.hex(): String {
        val stringBuffer = StringBuilder()
        for (datum in this) {
            stringBuffer.append(String.format("%02X", datum.toInt() and 0xFF))
        }
        return stringBuffer.toString()
    }

    private fun String.unHex(): ByteArray {
        val bytes = ByteArray(length / 2)
        for (i in 0 until length / 2) {
            bytes[i] = substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return bytes
    }

    private fun ByteArray.base64(): String {
        return Base64.getEncoder().encode(this).hex()
    }

    private fun String.unBase64(): ByteArray {
        return Base64.getDecoder().decode(unHex())
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val data = encrypt("hhhhhh")
        println("dat = $data")
        val text = decrypt(data)
        println("text = $text")
    }
}

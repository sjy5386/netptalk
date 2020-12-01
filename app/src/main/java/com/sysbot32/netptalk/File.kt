package com.sysbot32.netptalk

import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

data class ChatFile(val filename: String, val data: ByteArray) {
    constructor(jsonObject: JSONObject) : this(
        jsonObject.getString("filename"),
        Base64.decode(jsonObject.getString("data"), 0)
    )

    fun toJSONObject(): JSONObject = JSONObject()
        .put("filename", filename)
        .put("data", Base64.encodeToString(data, 0))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatFile

        if (filename != other.filename) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

fun compress(data: ByteArray): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    GZIPOutputStream(byteArrayOutputStream).write(data)
    return byteArrayOutputStream.toByteArray()
}

fun decompress(data: ByteArray): ByteArray = GZIPInputStream(data.inputStream()).readBytes()

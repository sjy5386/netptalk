package com.sysbot32.netptalk

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun compress(data: ByteArray): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    GZIPOutputStream(byteArrayOutputStream).write(data)
    return byteArrayOutputStream.toByteArray()
}

fun decompress(data: ByteArray): ByteArray = GZIPInputStream(data.inputStream()).readBytes()

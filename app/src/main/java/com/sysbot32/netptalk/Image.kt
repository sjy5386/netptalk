package com.sysbot32.netptalk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun loadBitmapByUri(uri: Uri): Bitmap {
    val contentResolver = chatActivity.contentResolver
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver,
                uri
            )
        )
    else
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val buf: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(buf, 0)
}

fun base64ToBitmap(base64: String): Bitmap {
    val buf: ByteArray = Base64.decode(base64, 0)
    val byteArrayInputStream = ByteArrayInputStream(buf)
    return BitmapFactory.decodeStream(byteArrayInputStream)
}

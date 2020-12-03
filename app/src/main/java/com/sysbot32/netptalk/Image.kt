package com.sysbot32.netptalk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.core.graphics.scale
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.math.min

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

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    var width: Int = bitmap.width
    var height: Int = bitmap.height
    val ratio: Double = width.toDouble() / height.toDouble()
    if ((width > height) && (width > maxWidth)) {
        width = maxWidth
        height = (width / ratio).toInt()
    } else if ((width < height) && (height > maxHeight)) {
        height = maxHeight
        width = (height * ratio).toInt()
    } else if ((width == height) && ((width > maxWidth) || (height > maxHeight))) {
        val size: Int = min(width, height)
        width = size
        height = size
    }
    return bitmap.scale(width, height)
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

package com.sysbot32.netptalk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun createNotificationChannel(context: Context, channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel: NotificationChannel = NotificationChannel(
            channelId,
            "default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun notifyChatMessage(context: Context, chatMessage: ChatMessage) {
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(chatMessage.username)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    when (chatMessage.chatType) {
        "text" -> {
            builder.setContentText(chatMessage.content)
        }
        "emoticon" -> {
            val emoticon = chatMessage.content.toInt()
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(BitmapFactory.decodeResource(context.resources, emoticon))
                    .bigLargeIcon(null)
            ).setContentText(mainActivity.getString(R.string.notification_text_emoticon))
        }
        "image" -> {
            val bitmap = base64ToBitmap(chatMessage.content)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null)
            ).setContentText(mainActivity.getString(R.string.notification_text_image))
        }
    }
    NotificationManagerCompat.from(context).notify(0, builder.build())
}

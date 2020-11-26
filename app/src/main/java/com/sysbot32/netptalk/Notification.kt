package com.sysbot32.netptalk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun createNotificationChannel(channelId: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel: NotificationChannel = NotificationChannel(
            channelId,
            "default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager: NotificationManager =
            mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun notifyChatMessage(chatMessage: ChatMessage) {
    val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(mainActivity, "default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(chatMessage.username)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    if (chatMessage.chatType == "text") {
        builder.setContentText(chatMessage.content)
    } else if (chatMessage.chatType == "image") {
        val bitmap = base64ToBitmap(chatMessage.content)
        builder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null)
        ).setContentText("(이미지)")
    }
    NotificationManagerCompat.from(mainActivity).notify(0, builder.build())
}

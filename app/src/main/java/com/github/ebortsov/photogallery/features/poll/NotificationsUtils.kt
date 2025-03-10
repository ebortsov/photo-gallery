package com.github.ebortsov.photogallery.features.poll

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.ebortsov.photogallery.R
import com.github.ebortsov.photogallery.ui.MainActivity

const val POLL_NOTIFICATION_CHANNEL_ID = "POLL_NOTIFICATION_CHANNEL_ID"

fun createPollNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= 26) {
        val name = context.resources.getString(R.string.poll_channel_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(
            POLL_NOTIFICATION_CHANNEL_ID,
            name,
            importance
        )
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }
}

fun makeNotification(context: Context): Notification {
    val resources = context.resources
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat
        .Builder(context, POLL_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.image_placeholder)
        .setContentTitle(resources.getString(R.string.new_photo_notification))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // for API < 26
        .build()

    return notification
}

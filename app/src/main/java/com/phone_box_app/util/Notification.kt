package com.phone_box_app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.UiContext
import androidx.core.app.NotificationCompat
import com.phone_box_app.HomeActivity
import com.phone_box_app.R


/**
 * Created by Ram Mandal on 28/10/2025
 * @System: Apple M1 Pro
 */

fun buildNotification(
    context: Context,
    notificationTitle: String,
    notificationContent: String,
    channelId: String,
    smallIcon: Int = R.drawable.ic_launcher_foreground,
    setOnGoing: Boolean = true
): Notification {
    val notificationIntent = Intent(context, HomeActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, channelId)
        .setContentTitle(notificationTitle)
        .setContentText(notificationContent)
        .setSmallIcon(smallIcon)
        .setContentIntent(pendingIntent)
        .setOngoing(setOnGoing)
        .build()
}

fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}
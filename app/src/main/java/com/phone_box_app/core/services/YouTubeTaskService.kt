package com.phone_box_app.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class YouTubeTaskService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "YouTubeTaskService"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = Notification.Builder(this, "task_channel")
            .setContentTitle("Running Task")
            .setContentText("Executing scheduled YouTube task...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
        startForeground(101, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url") ?: return START_NOT_STICKY
        val duration = intent.getIntExtra("duration", 30)

        scope.launch {
            executeTask(url, duration)
        }

        return START_NOT_STICKY
    }

    private suspend fun executeTask(url: String, duration: Int) {
        val startRx = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()

        Log.d(TAG, "Launching YouTube: $url")
        val youtubeIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(youtubeIntent)

        delay(duration * 1000L)

        val endRx = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()
        val usedMB = (endRx - startRx) / (1024f * 1024f)
        Log.d(TAG, "YouTube closed. Used approx: %.2f MB".format(usedMB))

        bringAppToFront()
        stopSelf()
    }

    private fun bringAppToFront() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("task_channel", "Task Runner", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

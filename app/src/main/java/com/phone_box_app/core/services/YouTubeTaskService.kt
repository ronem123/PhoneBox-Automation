package com.phone_box_app.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
import kotlinx.coroutines.*

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class YouTubeTaskService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "YouTubeTaskService"

    private val channelId = "youtube_task_channel"
    private val channelName = "Youtube task runner"


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, channelId, channelName)

        val notification = buildNotification(
            context = this,
            notificationTitle = "Running Task",
            notificationContent = "Executing scheduled Youtube task...",
            channelId = channelId,
            smallIcon = android.R.drawable.ic_media_play,
            setOnGoing = false
        )
        startForeground(101, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url") ?: return START_NOT_STICKY
        val duration = intent.getIntExtra("duration", 30) // in seconds

        scope.launch {
            executeTask(url, duration)
        }

        return START_NOT_STICKY
    }

    private suspend fun executeTask(url: String, duration: Int) {
        // 1️⃣ Get UID for this app
        val uid = packageManager.getApplicationInfo(packageName, 0).uid

        // 2️⃣ Capture initial data usage
        val startMobile = getUidDataUsage(uid, ConnectivityManager.TYPE_MOBILE)
        val startWifi = getUidDataUsage(uid, ConnectivityManager.TYPE_WIFI)

        // 3️⃣ Launch YouTube
        Log.d(TAG, "Launching YouTube: $url")
        val youtubeIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(youtubeIntent)

        // 4️⃣ Wait for the scheduled duration
        delay(duration * 1000L)

        // 5️⃣ Capture final data usage
        val endMobile = getUidDataUsage(uid, ConnectivityManager.TYPE_MOBILE)
        val endWifi = getUidDataUsage(uid, ConnectivityManager.TYPE_WIFI)

        // 6️⃣ Calculate used data in MB
        val usedMobileMB = (endMobile - startMobile) / (1024f * 1024f)
        val usedWifiMB = (endWifi - startWifi) / (1024f * 1024f)

        Log.d(
            TAG, "Task finished. Mobile: %.2f MB, Wi-Fi: %.2f MB".format(usedMobileMB, usedWifiMB)
        )

        // 7️⃣ Bring app back to front
        bringAppToFront()
        stopSelf()
    }

    private fun getUidDataUsage(uid: Int, networkType: Int): Long {
        return try {
            val nsm = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            val now = System.currentTimeMillis()
            val bucket = nsm.queryDetailsForUid(
                networkType, null, 0L, // start from epoch, we will subtract later
                now, uid
            )

            var totalBytes = 0L
            val usageBucket = NetworkStats.Bucket()
            while (bucket.hasNextBucket()) {
                bucket.getNextBucket(usageBucket)
                totalBytes += usageBucket.rxBytes + usageBucket.txBytes
            }
            bucket.close()
            totalBytes
        } catch (e: Exception) {
            Log.e(TAG, "Error reading data usage: ${e.message}")
            0L
        }
    }

    private fun bringAppToFront() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

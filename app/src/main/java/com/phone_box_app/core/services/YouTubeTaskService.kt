package com.phone_box_app.core.services

import android.app.Service
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.phone_box_app.core.receivers.alarm.ArcAlarmScheduler.bringAppToFront
import com.phone_box_app.util.ArcBroadCastIntentAction
import com.phone_box_app.util.ArgIntent
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        val url = intent?.getStringExtra(ArgIntent.ARG_URL) ?: return START_NOT_STICKY
        val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30) // in seconds

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
        Log.d(TAG, "Launching YouTube: $url for $duration minute")
        val youtubeIntent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(youtubeIntent)

        // 4️⃣ Wait for the scheduled duration
        //converting minute to milliseconds
        val durationInMilliSeconds = duration * 60L * 1000L

        //fake estimated time ; taken to lunch the app
        val timeToOpenYoutube = 2 * 1000L

        val totalWaitingTime = durationInMilliSeconds + timeToOpenYoutube

        //disable wifi for provided time [converting millisecond to second]
        disableWifi(totalWaitingTime / 1000)

        delay(totalWaitingTime)

        // 5️⃣ Capture final data usage
        val endMobile = getUidDataUsage(uid, ConnectivityManager.TYPE_MOBILE)
        val endWifi = getUidDataUsage(uid, ConnectivityManager.TYPE_WIFI)

        // 6️⃣ Calculate used data in MB
        val usedMobileMB = (endMobile - startMobile) / (1024f * 1024f)
        val usedWifiMB = (endWifi - startWifi) / (1024f * 1024f)

        Log.d(
            TAG,
            "Task finished. Mobile: %.2f MB, Wi-Fi: %.2f MB".format(usedMobileMB, usedWifiMB)
        )

        // 7️⃣ Bring app back to front
        bringAppToFront(context = this)
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun disableWifi(delay: Long) {
        Log.d(TAG, "Toggling WiFi via broadcast with delay: $delay")

        try {
            // Create a broadcast intent with action "com.phone_box_app.Wifi"
            val intent = Intent(ArcBroadCastIntentAction.DISABLE_WIFI)
            intent.putExtra(ArgIntent.ARG_DELAY, delay) // Pass the delay as an extra
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            sendBroadcast(intent) // Send a broadcast to Tasker or a listener
            Log.d(TAG, "WiFi toggle broadcast sent successfully with delay: $delay")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WiFi toggle broadcast", e)
        }
    }
}
///Users/rammandal/Documents/Home/AndroidStudioProjects/Freelance/phone-box-android/app/build/outputs/apk/debug/app-debug.apk
//adb shell dpm set-device-owner com.phone_box_app/.DeviceAdminReceiverImpl



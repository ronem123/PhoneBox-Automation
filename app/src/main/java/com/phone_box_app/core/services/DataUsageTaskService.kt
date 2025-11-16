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
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.data.repository.ArcRepositoryEntryPoint
import com.phone_box_app.util.ArcTaskType
import com.phone_box_app.util.ArgIntent
import com.phone_box_app.util.TaskerTaskType
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
import com.phone_box_app.util.sendBroadcastToTasker
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

@AndroidEntryPoint
class DataUsageTaskService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val TAG = "DataUsageTaskService"

    private val channelId = "data_usage_task_channel"
    private val channelName = "data_usage task runner"

    @Inject
    lateinit var appLogger: Logger


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this, channelId, channelName)

        val notification = buildNotification(
            context = this,
            notificationTitle = "Running Task",
            notificationContent = "Executing scheduled data_usage task...",
            channelId = channelId,
            smallIcon = android.R.drawable.ic_media_play,
            setOnGoing = false
        )
        startForeground(101, notification)
    }

    private fun deleteTaskById(id: Int?) {
        id?.let {
            serviceScope.launch {
                while (isActive) {
                    try {
                        val entryPoint = EntryPointAccessors.fromApplication(
                            this@DataUsageTaskService.applicationContext,
                            ArcRepositoryEntryPoint::class.java
                        )
                        val arcRepository = entryPoint.repository()

                        arcRepository.deleteTaskById(taskId = id)


                    } catch (e: Exception) {
                        appLogger.v(TAG, "Error deleting task $id: ${e.message}")
                    }
                    delay(60_000L) // every 1 minute
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "Starting Data usage service from onStartCommand")
        val taskType = intent?.getStringExtra(ArgIntent.ARG_TASK_TYPE)
        val taskId = intent?.getIntExtra(ArgIntent.ARG_TASK_ID, -1)
        val url = intent?.getStringExtra(ArgIntent.ARG_URL) ?: return START_NOT_STICKY
        val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30) // in seconds

        Log.v(TAG, "TaskType : $taskType")

        serviceScope.launch {
            taskType?.let {
                executeTask(taskType, url, duration)
                deleteTaskById(id = taskId)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun executeTask(taskType: String, url: String, duration: Int) {
        // 1️⃣ Get UID for this app
        val uid = packageManager.getApplicationInfo(packageName, 0).uid

        // 2️⃣ Capture initial data usage
        val startMobile = getUidDataUsage(uid, ConnectivityManager.TYPE_MOBILE)
        val startWifi = getUidDataUsage(uid, ConnectivityManager.TYPE_WIFI)

        // 3️⃣ Launch YouTube
        Log.d(TAG, "Launching Browser: $url for $duration minute")
        val dataUsageIntent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(dataUsageIntent)

        // 4️⃣ Wait for the scheduled duration
        //converting minute to milliseconds
        val durationInMilliSeconds = duration * 60L * 1000L

        //fake estimated time ; taken to lunch the app
        val timeToOpenBrowserOrApp = 2 * 1000L

        val totalWaitingTime = durationInMilliSeconds + timeToOpenBrowserOrApp

        //disable wifi for provided time [converting millisecond to second]
        this.sendBroadcastToTasker(TaskerTaskType.DISABLE_WIFI, totalWaitingTime / 1000)

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

        //kill the respective app or browser
        val taskerTaskType =
            when (taskType) {
                ArcTaskType.TASK_TYPE_YOUTUBE -> TaskerTaskType.KILL_YOUTUBE
                ArcTaskType.TASK_TYPE_FACEBOOK -> TaskerTaskType.KILL_FACEBOOK
                ArcTaskType.TASK_TYPE_CHROME -> TaskerTaskType.KILL_CHROME
                else -> {
                    TaskerTaskType.NONE
                }
            }
        this.sendBroadcastToTasker(taskerTaskType = taskerTaskType)

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
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null


}
///Users/rammandal/Documents/Home/AndroidStudioProjects/Freelance/phone-box-android/app/build/outputs/apk/debug/app-debug.apk
//adb shell dpm set-device-owner com.phone_box_app/.DeviceAdminReceiverImpl



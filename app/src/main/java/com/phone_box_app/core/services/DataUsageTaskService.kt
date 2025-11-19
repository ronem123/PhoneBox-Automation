package com.phone_box_app.core.services

//import okhttp3.internal.wait
import android.app.Service
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.net.toUri
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.data.model.SaveDataUsagePostData
import com.phone_box_app.data.repository.ArcRepository
import com.phone_box_app.data.repository.ArcRepositoryEntryPoint
import com.phone_box_app.util.ArcTaskType
import com.phone_box_app.util.ArgIntent
import com.phone_box_app.util.FileDownloadManager
import com.phone_box_app.util.TaskerTaskType
import com.phone_box_app.util.TimeUtil
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
import com.phone_box_app.util.sendBroadcastToTasker
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
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

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

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

    private fun deleteTaskById(arcRepository: ArcRepository, id: Int?) {
        id?.let {
            serviceScope.launch {
                try {
                    appLogger.v(TAG, "Deleting task $id")
                    arcRepository.deleteTaskById(taskId = id)
                } catch (e: Exception) {
                    appLogger.v(TAG, "Error deleting task $id: ${e.message}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "Starting Data usage service from onStartCommand")
        serviceScope.launch {
            try {
                handleIntent(intent)
            } catch (e: Exception) {
                appLogger.e(TAG, "Error occurred handling intent ${e.message}")
                e.printStackTrace()
            } finally {
                // stop service when done (or you may want to keep running if continuous)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun handleIntent(intent: Intent?) {
        if (intent == null) {
            stopSelf()
            return
        }

        val taskType = intent.getStringExtra(ArgIntent.ARG_TASK_TYPE) ?: run {
            stopSelf(); return
        }
        val url = intent.getStringExtra(ArgIntent.ARG_URL)
        val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30)
        val taskId = intent.getIntExtra(ArgIntent.ARG_TASK_ID, -1)

        appLogger.v(
            TAG,
            "Starting data usage task: $taskType url=$url duration=$duration taskId=$taskId"
        )

        when (taskType) {
            ArcTaskType.TASK_TYPE_YOUTUBE,
            ArcTaskType.TASK_TYPE_FACEBOOK,
            ArcTaskType.TASK_TYPE_CHROME -> handleAppTask(url, duration, taskType, taskId)

            ArcTaskType.TASK_TYPE_DOWNLOAD -> handleDownloadTask(url ?: return, taskType, taskId)
        }

//        stopSelf()
//
//
//        // 1) Read TOTAL MOBILE data before task
//        val startMobile = getTotalMobileDataUsage()
//
//        // 2) Disable WiFi using Tasker
//        sendBroadcastToTasker(TaskerTaskType.DISABLE_WIFI, 0)
//
//        // 3) Launch the required app/browser
//        launchAppOrUrl(url)
//
//        // 4) Wait
//        val durationInMilliSeconds = duration * 60L * 1000L
//        delay(durationInMilliSeconds)
//
//        // 5) Read TOTAL MOBILE data after
//        val endMobile = getTotalMobileDataUsage()
//
//        // 6) Calculate used MB
//        val usedMB = (endMobile - startMobile) / (1024f * 1024f)
//
//        Log.d(TAG, "Task finished → Used: %.2f MB".format(usedMB))
//
//        // 7) enable wifi
//        sendBroadcastToTasker(TaskerTaskType.ENABLE_WIFI, 0)
//
//        // 8) Kill apps via Tasker
//        killAppOrBrowser(taskType)
//
//        val entryPoint = EntryPointAccessors.fromApplication(
//            this@DataUsageTaskService.applicationContext,
//            ArcRepositoryEntryPoint::class.java
//        )
//        val arcRepository = entryPoint.repository()
//
//        // 9) Delete task from Room DB
//        deleteTaskById(arcRepository, taskId)
//
//        // 10) save total used Data in MB
//        //fake delay to wait until wifi is enabled back
//        delay(5 * 1000L)
//        val job = serviceScope.async {
//            saveDataUsagesToServer(
//                taskType = taskType,
//                dataUsage = usedMB,
//                usageTime = durationInMilliSeconds / 1000,
//                arcRepository = arcRepository
//            )
//        }
//
//        job.wait()
//
//        stopSelf()
    }

    private suspend fun handleAppTask(url: String?, duration: Int, taskType: String, taskId: Int) {
        // 1) Read TOTAL MOBILE data before task
        val startMobile = getTotalMobileDataUsage()

        // 2) Disable WiFi using Tasker
        sendBroadcastToTasker(TaskerTaskType.DISABLE_WIFI, 0)

        // 3) Launch the required app/browser
        launchAppOrUrl(url)

        // 4) Wait
        val durationInMilliSeconds = duration * 60L * 1000L
        delay(durationInMilliSeconds)

        // 5) Read TOTAL MOBILE data after
        val endMobile = getTotalMobileDataUsage()

        // 6) Calculate used MB
        val usedMB = (endMobile - startMobile) / (1024f * 1024f)

        Log.d(TAG, "Task finished → Used: %.2f MB".format(usedMB))

        // 7) enable wifi
        sendBroadcastToTasker(TaskerTaskType.ENABLE_WIFI, 0)

        // 8) Kill apps via Tasker
        killAppOrBrowser(taskType)

        val entryPoint = EntryPointAccessors.fromApplication(
            this@DataUsageTaskService.applicationContext,
            ArcRepositoryEntryPoint::class.java
        )
        val arcRepository = entryPoint.repository()

        // 9) Delete task from Room DB
        deleteTaskById(arcRepository, taskId)

        // 10) save total used Data in MB
        //fake delay to wait until wifi is enabled back
        delay(10 * 1000L)
        val job = serviceScope.async {
            saveDataUsagesToServer(
                taskType = taskType,
                dataUsage = usedMB,
                usageTime = durationInMilliSeconds / 1000,
                arcRepository = arcRepository
            )
        }

        job.join()

        stopSelf()
    }

    private suspend fun handleDownloadTask(
        fileUrl: String,
        taskType: String,
        taskId: Int
    ) {
        // 1️⃣ Disable WiFi for mobile data measurement
        sendBroadcastToTasker(TaskerTaskType.DISABLE_WIFI, 0)

        try {
            // 2️⃣ Create FileDownloadManager
            val downloadManager = FileDownloadManager(this, dispatcherProvider, appLogger)

            // 3️⃣ Use CompletableDeferred to suspend until download completes
            val downloadedFileDeferred = CompletableDeferred<File?>()

            val downloadJob = downloadManager.downloadFile(
                fileUrl = fileUrl,
                onProgress = { progress ->
                    Log.d(TAG, "Download progress: $progress%")
                },
                onComplete = { file ->
                    downloadedFileDeferred.complete(file)
                }
            )

            // Wait until download completes
            val downloadedFile = downloadedFileDeferred.await()
            downloadJob.join() // Ensure the coroutine inside download manager completes

            // 4️⃣ Calculate file size in MB
            val fileSizeMB = downloadedFile?.length()?.toFloat()?.div(1024 * 1024) ?: 0f
            Log.d(TAG, "File download completed → Size: %.2f MB".format(fileSizeMB))

            // 5️⃣ Access repository
            val arcRepository = EntryPointAccessors.fromApplication(
                this@DataUsageTaskService.applicationContext,
                ArcRepositoryEntryPoint::class.java
            ).repository()

            // 6️⃣ Save data usage to server
            saveDataUsagesToServer(
                taskType = taskType,
                dataUsage = fileSizeMB,
                usageTime = 0, // time can be ignored for file download
                arcRepository = arcRepository
            )

            // 7️⃣ Delete task from Room DB
            deleteTaskById(arcRepository, taskId)

        } catch (e: Exception) {
            Log.e(TAG, "Download task failed: ${e.message}")
        } finally {
            // 8️⃣ Always re-enable WiFi, whether download succeeded or failed
            sendBroadcastToTasker(TaskerTaskType.ENABLE_WIFI, 0)

            // 9️⃣ Stop the service
            stopSelf()
        }
    }


    private fun saveDataUsagesToServer(
        taskType: String,
        dataUsage: Float,
        usageTime: Long,
        arcRepository: ArcRepository
    ) {
        serviceScope.launch {
            try {
                arcRepository.getLocalDeviceInfo()?.deviceIdInt?.let { deviceId ->
                    arcRepository.saveDataUsageToServer(
                        postData = SaveDataUsagePostData(
                            appName = taskType,
                            date = TimeUtil.getCurrentDate(),
                            deviceId = deviceId,
                            usageDataMb = dataUsage,
                            usageTimeSeconds = usageTime
                        )
                    )
                }
            } catch (e: Exception) {
                appLogger.v(TAG, "Exception occurred while saving Data usage: ${e.message}")
            }
        }
    }

    private fun killAppOrBrowser(taskType: String) {
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
    }

    private fun getTotalMobileDataUsage(): Long {
        return try {
            val nsm = getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            val now = System.currentTimeMillis()

            val bucket = nsm.querySummary(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(),
                0L,
                now
            )

            var total = 0L
            val summaryBucket = NetworkStats.Bucket()
            while (bucket.hasNextBucket()) {
                bucket.getNextBucket(summaryBucket)
                total += summaryBucket.rxBytes + summaryBucket.txBytes
            }
            bucket.close()
            total
        } catch (e: Exception) {
            Log.e("DataUsage", "Error reading total mobile data: ${e.message}")
            0L
        }
    }

    private fun getSubscriberId(): String? {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) null else tm.subscriberId
    }


    private fun launchAppOrUrl(url: String?) {
        try {
            if (!url.isNullOrBlank()) {
                Log.d(TAG, "Launching Browser: $url")


                val dataUsageIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = url.toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    startActivity(dataUsageIntent)
                    return
                } catch (e: Exception) {
                    // fallback to open url in browser if app not handle it
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            appLogger.v(TAG, "Failed to launch app/$url: ${e.message}")
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



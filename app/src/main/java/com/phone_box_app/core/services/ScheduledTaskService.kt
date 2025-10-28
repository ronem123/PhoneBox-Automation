package com.phone_box_app.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.phone_box_app.HomeActivity
import com.phone_box_app.R
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.data.repository.ArcRepositoryEntryPoint
import com.phone_box_app.util.buildNotification
import com.phone_box_app.util.createNotificationChannel
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
class ScheduledTaskService : Service() {

    private val TAG = "TaskPollingService"
    private val CHANNEL_ID = "scheduled_task_channel"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created. Starting polling...")
        createNotificationChannel(
            context = this,
            channelId = CHANNEL_ID,
            channelName = "Scheduled Task Polling"
        )
        startForeground(
            1,
            buildNotification(
                context = this,
                notificationTitle = "Polling for scheduled tasks...",
                notificationContent = "",
                channelId = CHANNEL_ID
            )
        )

        startPolling()
    }

    private fun startPolling() {
        serviceScope.launch {
            while (isActive) {
                try {
                    val entryPoint =
                        EntryPointAccessors.fromApplication(
                            this@ScheduledTaskService.applicationContext,
                            ArcRepositoryEntryPoint::class.java
                        )
                    val arcRepository = entryPoint.repository()

                    arcRepository.getLocalDeviceInfo()?.deviceIdInt?.let { deviceId ->
                        arcRepository.getScheduledTask(deviceId = deviceId)
                            .flowOn(dispatcherProvider.io)
                            .catch {
                                //failed to catch due to error
                            }
                            .collect {
                                //success
                            }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Polling error: ${e.message}")
                }
                delay(60_000L) // every 1 minute
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}

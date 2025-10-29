package com.phone_box_app.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.receivers.alarm.ArcAlarmScheduler
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.data.repository.ArcRepository
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
import kotlin.let

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
            context = this, channelId = CHANNEL_ID, channelName = "Scheduled Task Polling"
        )
        startForeground(
            1, buildNotification(
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
                    val entryPoint = EntryPointAccessors.fromApplication(
                        this@ScheduledTaskService.applicationContext,
                        ArcRepositoryEntryPoint::class.java
                    )
                    val arcRepository = entryPoint.repository()

                    arcRepository.getLocalDeviceInfo()?.deviceIdInt?.let { deviceId ->

                        arcRepository.getScheduledTask(
                            deviceId = deviceId,
                            availableTasks = arcRepository.getArrayOfTaskPresent()
                        )
                            .flowOn(dispatcherProvider.io).catch {
                                //failed to catch due to error
                            }.collect { taskResponse ->
                                //success: cache to room db and set the task to alarm manager
                                cacheTaskToRoomDbAndSetAlarm(arcRepository, taskResponse)
                            }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Polling error: ${e.message}")
                }
                delay(60_000L) // every 1 minute
            }
        }
    }

    private suspend fun cacheTaskToRoomDbAndSetAlarm(
        arcRepository: ArcRepository, taskResponse: ScheduledTaskResponse?
    ) {
        taskResponse?.scheduledTaskData?.let { scheduledTasks ->
            if (scheduledTasks.isNotEmpty()) {
                scheduledTasks[0]?.scheduledTask?.let { scheduledTask ->
                    val taskEntity = arcRepository.insertScheduledTask(scheduledTask)
                    //set alarm
                    ArcAlarmScheduler.scheduleTask(
                        this@ScheduledTaskService.applicationContext,
                        taskEntity
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}

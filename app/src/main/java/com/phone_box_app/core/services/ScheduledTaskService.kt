package com.phone_box_app.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.phone_box_app.core.receivers.alarm.ArcAlarmScheduler
import com.phone_box_app.data.repository.ArcRepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.*

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class ScheduledTaskService : Service() {

    private val TAG = "TaskPollingService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created. Starting polling...")
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

                    //todo
                    val task = arcRepository.getScheduledTask()

                    if (task != null) {
                        Log.d(TAG, "Fetched scheduled task: $task")
                        ArcAlarmScheduler.scheduleTask(this@ScheduledTaskService, task)
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

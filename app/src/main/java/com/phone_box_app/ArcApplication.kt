package com.phone_box_app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.phone_box_app.core.workers.PendingSmsSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Ram Mandal on 09/10/2025
 * @System: Apple M1 Pro
 */
@HiltAndroidApp
class ArcApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()


    override fun onCreate() {
        super.onCreate()

        Log.d("ArcApp", "HiltWorkerFactory active: ${this::workerFactory.isInitialized}")

        schedulePendingSmsSyncWork()
    }

    private fun schedulePendingSmsSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<PendingSmsSyncWorker>(
            30, TimeUnit.MINUTES // minimum interval enforced by WorkManager
        )
            .setConstraints(constraints)
            .addTag("pending_sms_sync_periodic")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "pending_sms_sync",
            ExistingPeriodicWorkPolicy.KEEP, // avoids rescheduling duplicate workers
            periodicWork
        )
    }
}
package com.phone_box_app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.phone_box_app.core.workers.PendingSmsSyncWorker
import com.phone_box_app.core.workers.SmsSyncWorker
import com.phone_box_app.data.repository.ArcRepository
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
    lateinit var customSmsSyncWorkFactory: CustomSmsSyncWorkFactory


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(customSmsSyncWorkFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()


    override fun onCreate() {
        super.onCreate()

        schedulePendingSmsSyncWork()

        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("pending_sms_sync")
            .observeForever {
                it.forEach { workInfo ->
                    Log.d(
                        "WorkManagerStatus",
                        "Work: ${workInfo.id} - ${workInfo.state} - ${workInfo.tags}"
                    )
                }
            }
    }

    private fun schedulePendingSmsSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<PendingSmsSyncWorker>(
            1, TimeUnit.MINUTES // minimum interval enforced by WorkManager
        )
            .setConstraints(constraints)
            .addTag("pending_sms_sync_periodic")
            .build()

        val uniqueWorkerName = "pending_sms_sync"

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            uniqueWorkerName,
            ExistingPeriodicWorkPolicy.KEEP, // avoids rescheduling duplicate workers
            periodicWork
        )
    }
}

class CustomSmsSyncWorkFactory @Inject constructor(private val arcRepository: ArcRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SmsSyncWorker::class.qualifiedName -> {
                SmsSyncWorker(appContext, workerParameters, arcRepository)
            }

            PendingSmsSyncWorker::class.qualifiedName -> {
                PendingSmsSyncWorker(appContext, workerParameters, arcRepository)
            }

            else -> null
        }
    }

}
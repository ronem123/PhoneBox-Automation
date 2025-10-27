package com.phone_box_app.core.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phone_box_app.data.repository.ArcRepository


/**
 * Created by Ram Mandal on 21/10/2025
 * @System: Apple M1 Pro
 */
class PendingTaskWorker(
    context: Context,
    workerParameters: WorkerParameters,
    archRepository: ArcRepository
) : CoroutineWorker(context, workerParameters) {
    private val TAG = "PendingTaskWorker"
    override suspend fun doWork(): Result {
        return try {

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
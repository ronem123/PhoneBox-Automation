package com.phone_box_app.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.repository.ArcRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
@HiltWorker
class PendingSmsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val arcRepository: ArcRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val pendingMessages = arcRepository.getPendingSmsLog()
            for (sms in pendingMessages) {
                val success = arcRepository.saveSmsLogToServer(
                    SmsPostData(
                        sender = sms.sender,
                        message = sms.message,
                        timeStamp = sms.timeStamp.toString()
                    )
                )
                if (success) {
                    arcRepository.deleteSms(sms.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}


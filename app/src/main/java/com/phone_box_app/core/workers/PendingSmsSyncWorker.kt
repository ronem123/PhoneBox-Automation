package com.phone_box_app.core.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.repository.ArcRepository

/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */

class PendingSmsSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
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


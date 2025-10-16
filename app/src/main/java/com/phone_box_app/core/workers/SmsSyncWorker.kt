package com.phone_box_app.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.repository.ArcRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */

class SmsSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val arcRepository: ArcRepository
) : CoroutineWorker(appContext, workerParams) {

    init {
        Log.d(TAG, "SmsSyncWorker initialized successfully with Hilt")
    }

    companion object {
        const val KEY_SMS_ID = "sms_id"
        const val TAG = "sms_sync_worker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val smsId = inputData.getLong(KEY_SMS_ID, -1L)
        if (smsId <= 0) {
            Log.w(TAG, "Invalid smsId provided to worker: $smsId")
            return@withContext Result.failure()
        }

        try {
            val sms = arcRepository.getSmsById(smsId)
            if (sms == null) {
                // Already removed or doesn't exist -> nothing to do
                return@withContext Result.success()
            }

            // Optionally: mark attempt count to avoid infinite retries
            arcRepository.incrementSmsAttempts(smsId)

            // 1) Send to server
            val success = arcRepository.saveSmsLogToServer(
                SmsPostData(
                    sender = sms.sender,
                    message = sms.message,
                    timeStamp = sms.timeStamp.toString()
                )
            )

            // 2) On success, either mark as synced or delete
            if (success) arcRepository.deleteSms(smsId)

            Log.d(TAG, "SMS (id=$smsId) uploaded successfully")
            Result.success()

        } catch (e: IOException) {
            // likely a network problem — retry later
            e.printStackTrace()
            Result.retry()
        } catch (e: Exception) {
            // unrecoverable server/client error
            e.printStackTrace()
            Result.failure()
        }
    }
}

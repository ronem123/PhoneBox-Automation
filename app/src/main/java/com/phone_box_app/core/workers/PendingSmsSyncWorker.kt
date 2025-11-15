package com.phone_box_app.core.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.repository.ArcRepository
import com.phone_box_app.util.TimeUtil

/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */

class PendingSmsSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val arcRepository: ArcRepository
) : CoroutineWorker(context, workerParams) {

    private val TAG = "PendingSmsSyncWorker"
    override suspend fun doWork(): Result {
        return try {
            //get DeviceIdInt from local db
            val deviceIdInt = arcRepository.getLocalDeviceInfo()?.deviceIdInt

            val pendingMessages = arcRepository.getPendingSmsLog()
            for (sms in pendingMessages) {
                val success = arcRepository.saveSmsLogToServer(
                    SmsPostData(
                        deviceId = deviceIdInt,
                        isSent = 1,
                        senderNumber = sms.sender,
                        message = sms.message,
                        timeStamp = TimeUtil.getDateTimeFromTimeStamp(timeStamp = sms.timeStamp.toString())
                    )
                )
                Log.v(TAG, "Pending SMS with id: ${sms.id}")
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


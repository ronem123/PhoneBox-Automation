package com.phone_box_app.core.receivers.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.phone_box_app.ArcApplication
import com.phone_box_app.core.workers.SmsSyncWorker
import com.phone_box_app.data.repository.ArcRepositoryEntryPoint
import com.phone_box_app.util.getFullMessage
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
class ArcSmsReceiver : BroadcastReceiver() {

    private val TAG = "ArcSmsReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        // Use the ApplicationContext to ensure HiltWorkerFactory is used
        val appContext = context.applicationContext

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

            val entryPoint =
                EntryPointAccessors.fromApplication(appContext, ArcRepositoryEntryPoint::class.java)
            val arcRepository = entryPoint.repository()
            //read the message from the Telephony sms intents
            //if the messages are too long the messages are divided into the chunk and placed in the stack
            //in reverse order, we need to extract and reverse to correct order
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val fullMessage = messages.getFullMessage()
            if (fullMessage.isNotEmpty()) {
                val sender = messages[0].displayOriginatingAddress ?: ""
                val ts = System.currentTimeMillis()

                Log.d(TAG, "SMS from=$sender body=$fullMessage")

                // Insert to DB and enqueue worker in background coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // 1) Save locally
                        val rowId = arcRepository.insertSms(
                            sender = sender,
                            message = fullMessage.toString(),
                            timeStamp = ts
                        )

                        Log.v(TAG, "saved with rowId $rowId")

                        // 2) Build WorkRequest with smsId
                        val input = workDataOf(SmsSyncWorker.Companion.KEY_SMS_ID to rowId)

                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()

                        val workRequest = OneTimeWorkRequestBuilder<SmsSyncWorker>()
                            .setInputData(input)
                            .setConstraints(constraints)
                            .setBackoffCriteria(
                                backoffPolicy = BackoffPolicy.LINEAR,
                                backoffDelay = 10_000L,
                                timeUnit = TimeUnit.MILLISECONDS
                            )
                            .build()

                        // Use unique work to avoid duplicates keyed by rowId
                        val uniqueName = "sms_sync_$rowId"

                        if (appContext is ArcApplication) {
                            WorkManager.getInstance(appContext)
                                .enqueue(workRequest)
                        } else {
                            Log.e(
                                "SmsReceiver",
                                "App context not Hilt-aware! SmsSyncWorker will fail."
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}

/**
 * You can let your customers know about the new product launch in no
 * time with a simple SMS. Don’t forget the link to the landing page o
 * f your website,as this will take your customers to the right place.
 */
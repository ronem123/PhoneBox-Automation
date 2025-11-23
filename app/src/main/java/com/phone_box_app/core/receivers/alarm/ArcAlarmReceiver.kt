package com.phone_box_app.core.receivers.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone_box_app.core.services.CallService
import com.phone_box_app.core.services.SmsService
import com.phone_box_app.core.services.DataUsageTaskService
import com.phone_box_app.util.ArcTaskType
import com.phone_box_app.util.ArgIntent

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class ArcAlarmReceiver : BroadcastReceiver() {
    val TAG = "ArcAlarmReceiver"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(ArgIntent.ARG_TASK_TYPE)
        val taskId = intent.getIntExtra(ArgIntent.ARG_TASK_ID, -1)
        Log.d(TAG, "Received for $type")

        when (type) {
            ArcTaskType.TASK_TYPE_YOUTUBE,
            ArcTaskType.TASK_TYPE_CHROME,
            ArcTaskType.TASK_TYPE_FACEBOOK,
            ArcTaskType.TASK_TYPE_DOWNLOAD -> {
                val url = intent.getStringExtra(ArgIntent.ARG_URL)
                val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30)

                Log.d(TAG, "Alarm received for ${type}→ starting DataUsage Task Service for $url")

                val serviceIntent = Intent(context, DataUsageTaskService::class.java).apply {
                    putExtra(ArgIntent.ARG_URL, url)
                    putExtra(ArgIntent.ARG_TASK_TYPE, type)
                    putExtra(ArgIntent.ARG_TASK_ID, taskId)
                    putExtra(ArgIntent.ARG_DURATION, duration)
                }
                context.startForegroundService(serviceIntent)
            }

            ArcTaskType.TASK_TYPE_CALL -> {
                val phoneNumber = intent.getStringExtra(ArgIntent.ARG_RECEIVER_PHONE)
                val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30)

                Log.d(TAG, "Alarm received → Making call to $phoneNumber")

                val callServiceIntent = Intent(context, CallService::class.java).apply {
                    putExtra(ArgIntent.ARG_RECEIVER_PHONE, phoneNumber)
                    putExtra(ArgIntent.ARG_TASK_TYPE, type)
                    putExtra(ArgIntent.ARG_TASK_ID, taskId)
                    putExtra(ArgIntent.ARG_DURATION, duration)
                }
                context.startForegroundService(callServiceIntent)
                Log.d(TAG, "Started Call service from Alarm")

            }

            ArcTaskType.TASK_TYPE_SMS -> {
                Log.d(TAG, "Alarm received → sending sms now via service")

                val phoneNumber = intent.getStringExtra(ArgIntent.ARG_RECEIVER_PHONE) ?: return
                val message = intent.getStringExtra(ArgIntent.ARG_MESSAGE) ?: return

                Log.d(TAG, "PhoneNumber:$phoneNumber Message: $message")

                val smsServiceIntent = Intent(context, SmsService::class.java).apply {
                    putExtra(ArgIntent.ARG_RECEIVER_PHONE, phoneNumber)
                    putExtra(ArgIntent.ARG_TASK_TYPE, type)
                    putExtra(ArgIntent.ARG_TASK_ID, taskId)
                    putExtra(ArgIntent.ARG_MESSAGE, message)
                }
                context.startForegroundService(smsServiceIntent)
                Log.d(TAG, "Started sms service from Alarm")

            }

        }
    }
}
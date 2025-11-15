package com.phone_box_app.core.receivers.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.phone_box_app.HomeActivity
import com.phone_box_app.core.services.CallService
import com.phone_box_app.core.services.SmsService
import com.phone_box_app.core.services.YouTubeTaskService
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
        Log.d(TAG, "Received for $type")

        when (type) {
            ArcTaskType.TASK_TYPE_YOUTUBE -> {
                val url = intent.getStringExtra(ArgIntent.ARG_URL)
                val duration = intent.getIntExtra(ArgIntent.ARG_DURATION, 30)

                Log.d(TAG, "Alarm received → starting YouTubeTaskService for $url")

                val serviceIntent = Intent(context, YouTubeTaskService::class.java).apply {
                    putExtra(ArgIntent.ARG_URL, url)
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
                    putExtra(ArgIntent.ARG_DURATION, duration)
                }
                context.startForegroundService(callServiceIntent)
                Log.d(TAG, "Started Call service from Alarm")

            }

            ArcTaskType.TASK_TYPE_SMS -> {
                Log.d(TAG, "Alarm received → sending sms now via service")

                val phoneNumber = intent.getStringExtra(ArgIntent.ARG_RECEIVER_PHONE) ?: return
                val message = intent.getStringExtra(ArgIntent.ARG_MESSAGE) ?: return

                Log.d(TAG, "PhoneNumber:" + phoneNumber + " Message: " + message)

                val smsServiceIntent = Intent(context, SmsService::class.java).apply {
                    putExtra(ArgIntent.ARG_RECEIVER_PHONE, phoneNumber)
                    putExtra(ArgIntent.ARG_MESSAGE, message)
                }
                context.startForegroundService(smsServiceIntent)
                Log.d(TAG, "Started sms service from Alarm")

            }

            ArcTaskType.TASK_TYPE_BRING_APP_TO_FRONT -> {
                Log.d("TaskAlarmReceiver", "Alarm received → Opening App to front")

                val youtubeIntent = Intent(context, HomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(youtubeIntent)
            }
        }
    }
}
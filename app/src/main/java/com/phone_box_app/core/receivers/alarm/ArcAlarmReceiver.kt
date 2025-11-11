package com.phone_box_app.core.receivers.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone_box_app.HomeActivity
import com.phone_box_app.core.services.YouTubeTaskService
import androidx.core.net.toUri

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class ArcAlarmReceiver : BroadcastReceiver() {
    companion object AlarmReceiverType {
        const val ARG_TASK_TYPE = "task_type"

        const val TASK_TYPE_YOUTUBE = "youtube"
        const val TASK_TYPE_CALL = "call"
        const val TASK_TYPE_BRING_APP_TO_FRONT = "call"

        const val ARG_URL = "url"
        const val ARG_DURATION = "duration"
        const val ARG_TASK_ID = "task_id"
        const val ARG_RECEIVER_PHONE = "receiver_phone_number"

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(ARG_TASK_TYPE)
        Log.d("TaskAlarmReceiver", "Received for $type")

        when (type) {
            TASK_TYPE_YOUTUBE -> {
                val url = intent.getStringExtra(ARG_URL)
                val duration = intent.getIntExtra(ARG_DURATION, 30)

                Log.d("TaskAlarmReceiver", "Alarm received → starting YouTubeTaskService for $url")

                val serviceIntent = Intent(context, YouTubeTaskService::class.java).apply {
                    putExtra(ARG_URL, url)
                    putExtra(ARG_DURATION, duration)
                }
                context.startForegroundService(serviceIntent)

            }

            TASK_TYPE_CALL -> {
                Log.d("TaskAlarmReceiver", "Making a call")
                val phoneNumber = intent.getStringExtra(ARG_RECEIVER_PHONE) ?: return
                Log.d("TaskAlarmReceiver", "Alarm received → Making call to $phoneNumber")

                // Place call from foreground context
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = "tel:$phoneNumber".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(callIntent)
            }

            TASK_TYPE_BRING_APP_TO_FRONT -> {
                Log.d("TaskAlarmReceiver", "Alarm received → Opening App to front")

                val youtubeIntent = Intent(context, HomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(youtubeIntent)
            }
        }
    }
}
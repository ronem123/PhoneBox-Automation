package com.phone_box_app.core.receivers.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.phone_box_app.HomeActivity
import com.phone_box_app.core.services.YouTubeTaskService

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class ArcAlarmReceiver : BroadcastReceiver() {
    companion object AlarmReceiverType {
        const val ALARM_TYPE = "alarm_type"
        const val OPEN_APP = 1
        const val TAKE_APP_TO_FRONT = 2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(ALARM_TYPE, OPEN_APP)
        Log.d("TaskAlarmReceiver", "Received for $type")

        if (type == OPEN_APP) {
            val url = intent.getStringExtra("url")
            val duration = intent.getIntExtra("duration", 30)

            Log.d("TaskAlarmReceiver", "Alarm received → starting YouTubeTaskService for $url")

            val serviceIntent = Intent(context, YouTubeTaskService::class.java).apply {
                putExtra("url", url)
                putExtra("duration", duration)
            }
            context.startForegroundService(serviceIntent)
        } else if (type == TAKE_APP_TO_FRONT) {
            Log.d("TaskAlarmReceiver", "Alarm received → Opening App to front")

            val youtubeIntent = Intent(context, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(youtubeIntent)
        }
    }
}
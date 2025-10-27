package com.phone_box_app.core.receivers.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.phone_box_app.core.services.YouTubeTaskService

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */

class ArcAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val url = intent.getStringExtra("url")
        val duration = intent.getIntExtra("duration", 30)

        Log.d("TaskAlarmReceiver", "Alarm received → starting YouTubeTaskService for $url")

        val serviceIntent = Intent(context, YouTubeTaskService::class.java).apply {
            putExtra("url", url)
            putExtra("duration", duration)
        }
        context.startForegroundService(serviceIntent)
    }
}
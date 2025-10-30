package com.phone_box_app.core.receivers.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskEntity
import com.phone_box_app.util.TimeUtil

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */


object ArcAlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTask(context: Context, task: ScheduledTaskEntity) {
        val intent = Intent(context, ArcAlarmReceiver::class.java).apply {
            putExtra("url", task.url)
            putExtra("duration", task.duration)
            putExtra("id", task.taskId)
        }

        if (task.taskId == null) {
            Log.v("ScheduleTask", "TaskId was null during alarm setup")
            return
        }

        if (task.startDate == null || task.scheduledTime == null) {
            Log.v("ScheduleTask", "startDate or scheduled time was null during alarm setup")
            return
        }

        // Combine start_date + scheduled_time → milliseconds
        val triggerTimeMillis = TimeUtil.getTimeInMilliSec(task.startDate, task.scheduledTime)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = task.duration ?: 0

        Log.d("AlarmScheduler", "Scheduling task ${task.taskId} at $triggerTime")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }
}

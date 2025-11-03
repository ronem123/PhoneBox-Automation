package com.phone_box_app.core.receivers.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.phone_box_app.core.logger.AppLogger
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskEntity
import com.phone_box_app.util.TimeUtil

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */


object ArcAlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTask(context: Context, task: ScheduledTaskEntity, appLogger: Logger) {
        val intent = Intent(context, ArcAlarmReceiver::class.java).apply {
            putExtra(ArcAlarmReceiver.ALARM_TYPE, ArcAlarmReceiver.OPEN_APP)
            putExtra("url", task.url)
            putExtra("duration", task.duration)
            putExtra("id", task.taskId)
        }

        if (task.taskId == null) {
            appLogger.v("ScheduleTask", "TaskId was null during alarm setup")
            return
        }

        if (task.startDate == null || task.scheduledTime == null) {
            appLogger.v("ScheduleTask", "startDate or scheduled time was null during alarm setup")
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

        appLogger.v("AlarmScheduler", "Scheduling task ${task.taskId} at $triggerTime")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )

    }

    @SuppressLint("ScheduleExactAlarm")
    fun bringAppToFront(context: Context) {
        val intent = Intent(context, ArcAlarmReceiver::class.java).apply {
            putExtra(ArcAlarmReceiver.ALARM_TYPE, ArcAlarmReceiver.TAKE_APP_TO_FRONT)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            pendingIntent
        )
    }
}

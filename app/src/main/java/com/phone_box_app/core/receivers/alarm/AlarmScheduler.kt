package com.phone_box_app.core.receivers.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskEntity
import com.phone_box_app.util.ArcTaskType
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_CALL
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_CHROME
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_DOWNLOAD
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_FACEBOOK
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_SMS
import com.phone_box_app.util.ArcTaskType.TASK_TYPE_YOUTUBE
import com.phone_box_app.util.ArgIntent
import com.phone_box_app.util.TimeUtil

/**
 * Created by Ram Mandal on 27/10/2025
 * @System: Apple M1 Pro
 */


object ArcAlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTask(context: Context, task: ScheduledTaskEntity, appLogger: Logger) {

        val intent = Intent(context, ArcAlarmReceiver::class.java)
            .apply {
                putExtra(ArgIntent.ARG_TASK_TYPE, task.taskType)
                putExtra(ArgIntent.ARG_TASK_ID, task.taskId)

                when (task.taskType) {
                    TASK_TYPE_YOUTUBE,
                    TASK_TYPE_DOWNLOAD,
                    TASK_TYPE_FACEBOOK,
                    TASK_TYPE_CHROME -> {
                        putExtra(ArgIntent.ARG_URL, task.url)
                        putExtra(ArgIntent.ARG_DURATION, task.duration)
                    }

                    TASK_TYPE_CALL -> {
                        putExtra(ArgIntent.ARG_RECEIVER_PHONE, task.deviceSimNumber)
                    }

                    TASK_TYPE_SMS -> {
                        putExtra(ArgIntent.ARG_RECEIVER_PHONE, task.deviceSimNumber)
                        putExtra(ArgIntent.ARG_MESSAGE, task.message)
                    }

                }
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

        appLogger.v("AlarmScheduler", "Scheduling task ${task.taskId} for $triggerTime minute")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )

    }

}

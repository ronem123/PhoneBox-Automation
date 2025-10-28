//package com.phone_box_app.core.receivers.alarm
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//
///**
// * Created by Ram Mandal on 27/10/2025
// * @System: Apple M1 Pro
// */
//
//
//object ArcAlarmScheduler {
//    fun scheduleTask(context: Context, task: Task) {
//        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
//            putExtra("url", task.url)
//            putExtra("duration", task.durationSeconds)
//            putExtra("id", task.id)
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            task.id,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val triggerTime = task.scheduledTimeMillis
//
//        Log.d("AlarmScheduler", "Scheduling task ${task.id} at $triggerTime")
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                triggerTime,
//                pendingIntent
//            )
//        } else {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//        }
//    }
//}

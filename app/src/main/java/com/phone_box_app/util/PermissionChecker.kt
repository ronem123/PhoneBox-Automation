package com.phone_box_app.util

import android.app.AlarmManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import kotlin.collections.isNotEmpty


/**
 * Created by Ram Mandal on 29/10/2025
 * @System: Apple M1 Pro
 */
fun ensureExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}

fun isUsageAccessGranted(context: Context): Boolean {
    return try {
        val now = System.currentTimeMillis()
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val list = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000L * 60L, now)
        list != null && list.isNotEmpty()
    } catch (e: Exception) {
        false
    }
}
//used permission to get data usages
fun launchUsageAccessSettings(context: Context) {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

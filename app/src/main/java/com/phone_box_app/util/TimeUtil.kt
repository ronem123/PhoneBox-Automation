package com.phone_box_app.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

//
//import java.util.Calendar
//
/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

object TimeUtil {

    fun getInitialDelay(): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 30)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - System.currentTimeMillis()
    }

    fun getTimeInMilliSec(
        date: String,
        time: String,
        datePattern: String = "yyyy-MM-dd HH:mm"
    ): Long {
        // Combine start_date + scheduled_time → milliseconds
        val triggerTimeMillis = try {
            val dateTimeString = "$date $time" // e.g. "2025-10-29 06:43"
            val formatter = SimpleDateFormat(datePattern, Locale.getDefault())
            val date = formatter.parse(dateTimeString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
            System.currentTimeMillis()
        }

        try {
            // Log the scheduled time for debugging
            val formattedTime = SimpleDateFormat(datePattern, Locale.getDefault())
                .format(Date(triggerTimeMillis))
            Log.v("TimeUtil", "Formated Date Time $formattedTime")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return triggerTimeMillis
    }
}
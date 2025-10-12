package com.ram.mandal.nepaldrivingliscense.util
//
//import java.util.Calendar
//
/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

//object TimeUtil {
//
//    fun getInitialDelay(): Long {
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, Const.MORNING_UPDATE_TIME)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
//
//        if (calendar.timeInMillis <= System.currentTimeMillis()) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1)
//        }
//
//        return calendar.timeInMillis - System.currentTimeMillis()
//    }
//}
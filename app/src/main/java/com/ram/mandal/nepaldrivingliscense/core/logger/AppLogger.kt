package com.ram.mandal.nepaldrivingliscense.core.logger

import android.util.Log

/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
class AppLogger : Logger {
    override fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }
}
package com.phone_box_app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Ram Mandal on 03/11/2025
 * @System: Apple M1 Pro
 */


object ArcWifiHelper {

    /**
     * Toggle Wi-Fi on/off using shell command. Works for device-owner apps.
     *
     * @param enable true to turn Wi-Fi on, false to turn it off
     */
    suspend fun setWifiEnabled(enable: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val cmd = if (enable) "svc wifi enable" else "svc wifi disable"
                Runtime.getRuntime().exec(cmd)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

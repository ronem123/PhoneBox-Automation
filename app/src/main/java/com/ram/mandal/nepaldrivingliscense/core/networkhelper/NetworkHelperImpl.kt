package com.ram.mandal.nepaldrivingliscense.core.networkhelper

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

class NetworkHelperImpl(
    private val context: Context
) : NetworkHelper {
    override fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}
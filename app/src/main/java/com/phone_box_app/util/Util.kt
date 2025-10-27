package com.phone_box_app.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.telephony.SmsMessage
import java.util.UUID

/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

object MyIntent {
    val route = "route_key"
    val menu_item = "menu_item"
    val navigationDrawer = "navigation_key"

}

inline fun <reified T : Parcelable> getBundleObject(bundle: Bundle?, key: String): T? {
    return if (Build.VERSION.SDK_INT >= 33) bundle?.getParcelable(key, T::class.java)
    else bundle?.getParcelable(key)
}

fun getMyDeviceId(context: Context?): String {
    var androidId: String
    androidId = try {
        Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    } catch (e: Exception) {
        e.printStackTrace()
        UUID.randomUUID().toString()
    }

    return androidId
}


fun getMyDeviceName(): String {
    val deviceModel = Build.MODEL // e.g. "Pixel 7"
    val manufacturer = Build.MANUFACTURER // e.g. "Google"
    val deviceBrand = Build.BRAND // e.g. "google"

    val deviceName = "$manufacturer $deviceModel $deviceBrand"

//    val deviceName = Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
//        ?: "${Build.MANUFACTURER} ${Build.MODEL}"

    return deviceName
}

fun getMyDeviceModel(): String = Build.MODEL

fun Array<SmsMessage?>?.getFullMessage(): String {
    val stringBuilder = StringBuilder()
    if (this.isNullOrEmpty()) return ""

    else if (this.isNotEmpty()) {
        for (sms in this.sortedBy { it?.indexOnIcc }) {
            stringBuilder.append(sms?.displayMessageBody ?: "")
        }
    }
    return stringBuilder.toString()
}
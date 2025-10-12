package com.ram.mandal.nepaldrivingliscense.util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

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
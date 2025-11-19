package com.phone_box_app.util

import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * Created by Ram Mandal on 12/11/2025
 * @System: Apple M1 Pro
 */

object ArcTaskType {
    const val TASK_TYPE_YOUTUBE = "youtube"
    const val TASK_TYPE_FACEBOOK = "facebook"
    const val TASK_TYPE_CHROME = "chrome"
    const val TASK_TYPE_DOWNLOAD = "download"
    const val TASK_TYPE_CALL = "call"
    const val TASK_TYPE_SMS = "sms"

}

enum class TaskerTaskType {
    KILL_FACEBOOK,
    KILL_YOUTUBE,
    KILL_CHROME,
    ENABLE_WIFI,
    DISABLE_WIFI,
    END_CALL,
    NONE
}

object ArgIntent {
    const val ARG_DELAY = "arg_delay"
    const val ARG_DURATION = "arg_duration"
    const val ARG_URL = "arg_url"
    const val ARG_RECEIVER_PHONE = "arg_receiver_number"
    const val ARG_TASK_TYPE = "arg_task_type"
    const val ARG_TASK_ID = "arg_task_id"
    const val ARG_MESSAGE = "arg_message"

}

object ArcBroadCastIntentAction {
    const val DISABLE_WIFI = "com.phone_box_app.DisableWifi"

    const val ENABLE_WIFI = "com.phone_box_app.EnableWifi"
    const val END_CALL = "com.phone_box_app.EndCall"
    const val KILL_YOUTUBE = "com.phone_box_app.KillYoutube"
    const val KILL_FACEBOOK = "com.phone_box_app.KillFacebook"
    const val KILL_CHROME = "com.phone_box_app.KillChrome"
}


private fun getAction(taskerTaskType: TaskerTaskType): String {
    return when (taskerTaskType) {
        TaskerTaskType.KILL_YOUTUBE -> ArcBroadCastIntentAction.KILL_YOUTUBE
        TaskerTaskType.KILL_FACEBOOK -> ArcBroadCastIntentAction.KILL_FACEBOOK
        TaskerTaskType.KILL_CHROME -> ArcBroadCastIntentAction.KILL_CHROME
        TaskerTaskType.END_CALL -> ArcBroadCastIntentAction.END_CALL
        TaskerTaskType.DISABLE_WIFI -> ArcBroadCastIntentAction.DISABLE_WIFI
        TaskerTaskType.ENABLE_WIFI -> ArcBroadCastIntentAction.ENABLE_WIFI
        TaskerTaskType.NONE -> ""
    }
}

fun Context.sendBroadcastToTasker(taskerTaskType: TaskerTaskType, delay: Long = 0) {
    val TAG = "TASKER"

    Log.d(TAG, "Sending broadcast to tasker for $taskerTaskType with delay: $delay sec")

    try {
        val intent = Intent(getAction(taskerTaskType))
        intent.putExtra(ArgIntent.ARG_DELAY, delay) // Pass the delay as an extra
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        this.sendBroadcast(intent) // Send a broadcast to Tasker or a listener
        Log.d(TAG, "$taskerTaskType broadcast sent successfully with delay: $delay")
    } catch (e: Exception) {
        Log.e(TAG, "Error sending $taskerTaskType broadcast", e)
    }


}
package com.phone_box_app.util


/**
 * Created by Ram Mandal on 12/11/2025
 * @System: Apple M1 Pro
 */

object ArcTaskType {
    const val TASK_TYPE_YOUTUBE = "youtube"
    const val TASK_TYPE_CALL = "call"
    const val TASK_TYPE_SMS = "sms"
    const val TASK_TYPE_BRING_APP_TO_FRONT = "task_bring_app_to_front"
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
    const val DISABLE_WIFI = "com.phone_box_app.Wifi"
    const val END_CALL = "com.phone_box_app.EndCall"
}
package com.phone_box_app.data.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

data class DeviceRegistrationPostData(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_model")
    val deviceModel: String,
    @SerializedName("device_name")
    val deviceName: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("device_sim_number")
    val deviceSimNumber: String,
    @SerializedName("profile_name")
    val profileName: String
)

data class SmsPostData(
    @SerializedName("device_id")
    val deviceId: Int?,
    @SerializedName("is_sent")
    val isSent: Int?,
    @SerializedName("sender_number")
    val senderNumber: String?,
    @SerializedName("sms")
    val message: String?,
    @SerializedName("timestamp")
    val timeStamp: String?

)

data class SaveDataUsagePostData(
    @SerializedName("app_name")
    val appName: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("device_id")
    val deviceId: Int?,
    @SerializedName("usage_data_mb")
    val usageDataMb: Float?,
    @SerializedName("usage_time_seconds")
    val usageTimeSeconds: Long?
)
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
    @SerializedName("sender")
    val sender: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("time_stamp")
    val timeStamp: String,
)
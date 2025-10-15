package com.phone_box_app.data.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
data class RegisterDeviceResponse(
    @SerializedName("data")
    val deviceData: DeviceData?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Boolean?
)

data class DeviceData(
    @SerializedName("auth_token")
    val authToken: Any?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("device_id")
    val deviceId: String?,
    @SerializedName("device_model")
    val deviceModel: String?,
    @SerializedName("device_name")
    val deviceName: String?,
    @SerializedName("device_sim_number")
    val deviceSimNumber: String?,
    @SerializedName("device_token")
    val deviceToken: Any?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("updated_at")
    val updatedAt: String?
)


/**
 * Scheduled Task Response
 */
data class ScheduledTaskResponse(
    @SerializedName("status")
    val status: String? = null
)
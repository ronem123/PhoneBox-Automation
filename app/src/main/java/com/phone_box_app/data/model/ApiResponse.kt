package com.phone_box_app.data.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
data class RegisterDeviceResponse(
    @SerializedName("data") val deviceData: DeviceData?,
    @SerializedName("message") val message: String?,
    @SerializedName("success") val success: Boolean?
)

data class DeviceData(
    @SerializedName("auth_token") val authToken: Any?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("device_model") val deviceModel: String?,
    @SerializedName("device_name") val deviceName: String?,
    @SerializedName("device_sim_number") val deviceSimNumber: String?,
    @SerializedName("device_token") val deviceToken: Any?,
    @SerializedName("id") val id: Int?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("updated_at") val updatedAt: String?
)


/**
 * Scheduled Task Response
 */
data class ScheduledTaskResponse(
    @SerializedName("data") val scheduledTaskData: List<ScheduledTaskData?>?,
    @SerializedName("message") val message: String?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("version") val version: String?
)

data class ScheduledTaskData(
    @SerializedName("task") val scheduledTask: ScheduledTask?,
    @SerializedName("receiver") val receiverData: ReceiverData?
)

data class ScheduledTask(
    @SerializedName("caller_id") val callerId: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("device") val device: String?,
    @SerializedName("deviceId") val deviceId: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("is_active") val isActive: String?,
    @SerializedName("is_plan_data") val isPlanData: Int?,
    @SerializedName("message") val message: String?,
    @SerializedName("receiver_id") val receiverId: Int?,
    @SerializedName("scheduled_time") val scheduledTime: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("task_type") val taskType: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("url") val url: String?
)

data class ReceiverData(
    @SerializedName("auth_token") val authToken: Any?,
    @SerializedName("country") val country: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("device_id") val deviceId: String?,
    @SerializedName("device_model") val deviceModel: String?,
    @SerializedName("device_name") val deviceName: String?,
    @SerializedName("device_sim_number") val deviceSimNumber: String?,
    @SerializedName("device_token") val deviceToken: Any?,
    @SerializedName("id") val id: Int?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("profile_name") val profileName: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
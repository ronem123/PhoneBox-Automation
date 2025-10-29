package com.phone_box_app.data.network

import com.phone_box_app.data.model.DeviceRegistrationPostData
import com.phone_box_app.data.model.RegisterDeviceResponse
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.data.model.SmsPostData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("v1/registerDevice")
    suspend fun registerDevice(@Body post: DeviceRegistrationPostData): RegisterDeviceResponse

    @GET("v2/getAllActiveTasks")
    suspend fun getTask(
        @Query(value = "device_id") deviceId: Int,
        @Query(value = "task_ids") availableTasks: String,
    ): ScheduledTaskResponse


    @POST("v1/saveSms")
    suspend fun saveSms(@Body post: SmsPostData): Response<Unit>

    @GET("v1/deviceDetail")
    suspend fun getDeviceDetail(@Query(value = "device_id") deviceId: String): Response<Unit>


}
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
    @POST("registerDevice")
    suspend fun registerDevice(@Body post: DeviceRegistrationPostData): RegisterDeviceResponse

    @GET("getScheduledTasks")
    suspend fun getTask(@Query("device_id") deviceId: String): ScheduledTaskResponse


    @POST("saveSms")
    suspend fun saveSms(@Body post: SmsPostData): Response<Unit>

}
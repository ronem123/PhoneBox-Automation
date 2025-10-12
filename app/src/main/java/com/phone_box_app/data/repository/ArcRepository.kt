package com.phone_box_app.data.repository

import com.phone_box_app.data.network.ApiService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun saveDeviceId(deviceId: String) =
        flow { emit(apiService.saveDevice()) }

    suspend fun getScheduledTask(deviceId:String) =
        flow { emit(apiService.getTask()) }

}
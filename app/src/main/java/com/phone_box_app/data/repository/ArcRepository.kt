package com.phone_box_app.data.repository

import com.phone_box_app.data.model.DeviceInfoData
import com.phone_box_app.data.network.ApiService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun saveDeviceId(data: DeviceInfoData) =
        flow { emit(apiService.saveDevice(data)) }

    suspend fun getScheduledTask(deviceId: String) =
        flow { emit(apiService.getTask()) }

}
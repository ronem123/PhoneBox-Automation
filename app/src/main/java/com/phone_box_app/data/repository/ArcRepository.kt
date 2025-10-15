package com.phone_box_app.data.repository

import com.phone_box_app.data.model.DeviceRegistrationData
import com.phone_box_app.data.network.ApiService
import com.phone_box_app.data.room.deviceinfo.DeviceInfoDao
import com.phone_box_app.data.room.deviceinfo.DeviceInfoEntity
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(
    private val apiService: ApiService,
    private val deviceInfoDao: DeviceInfoDao
) {

    suspend fun getScheduledTask(deviceId: String) =
        flow { emit(apiService.getTask()) }

    /**
     * Method to register Device to server and save to local room db
     * emit the response as flow
     */
    suspend fun registerDevice(data: DeviceRegistrationData) = flow {
        val response = apiService.registerDevice(data)
        /**
         * Save device info to local room db once saved to server
         */
        if (response.success == true) {
            deviceInfoDao.insertDeviceInfo(
                DeviceInfoEntity(
                    deviceId = data.deviceId,
                    mobileNumber = data.deviceSimNumber,
                    isRegistered = true,
                    deviceModel = data.deviceModel,
                    deviceSimNumber = data.deviceSimNumber,
                    countryCode = data.countryCode,
                    deviceName = data.deviceName,
                    profileName = data.profileName
                )
            )
        }
        emit(response)
    }

    suspend fun getLocalDeviceInfo(): DeviceInfoEntity? = deviceInfoDao.getDeviceInfo()
}
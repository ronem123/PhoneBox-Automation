package com.phone_box_app.data.repository

import com.phone_box_app.data.model.DeviceRegistrationPostData
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.network.ApiService
import com.phone_box_app.data.room.deviceinfo.DeviceInfoDao
import com.phone_box_app.data.room.deviceinfo.DeviceInfoEntity
import com.phone_box_app.data.room.smslog.SmsEntity
import com.phone_box_app.data.room.smslog.SmsLogDao
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(
    private val apiService: ApiService,
    private val deviceInfoDao: DeviceInfoDao,
    private val smsLogDao: SmsLogDao
) {

    suspend fun getScheduledTask(deviceId: String) =
        flow { emit(apiService.getTask(deviceId)) }

    /**
     * Method to register Device to server and save to local room db
     * emit the response as flow
     */
    suspend fun registerDevice(data: DeviceRegistrationPostData) = flow {
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

    /**
     * Insert sms to sms_log locally and return the row id
     */
    suspend fun insertSms(sender: String, message: String, timeStamp: Long): Long {
        val smsEntity = SmsEntity(
            sender = sender,
            message = message,
            timeStamp = timeStamp,
            isSynced = false,
            attempts = 0
        )

        return smsLogDao.insertSms(smsEntity)
    }

    suspend fun getSmsById(smsId: Long): SmsEntity? {
        return smsLogDao.getSmsById(smsId)
    }

    suspend fun deleteSms(smsId: Long) {
        smsLogDao.deleteById(smsId)
    }

    suspend fun incrementSmsAttempts(smsId: Long) {
        val sms = smsLogDao.getSmsById(smsId) ?: return
        val updated = sms.copy(attempts = sms.attempts + 1)
        smsLogDao.updateSms(updated)
    }

    suspend fun saveSmsLogToServer(smsPostData: SmsPostData): Boolean {
        val response = apiService.saveSms(smsPostData)
        return response.isSuccessful
    }

    suspend fun getPendingSmsLog():List<SmsEntity>{
        return smsLogDao.getPendingSms()
    }

}
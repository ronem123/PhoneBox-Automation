package com.phone_box_app.data.repository

import android.content.Intent
import com.phone_box_app.core.services.ScheduledTaskService
import com.phone_box_app.data.model.DeviceRegistrationPostData
import com.phone_box_app.data.model.ScheduledTask
import com.phone_box_app.data.model.ScheduledTaskData
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.data.model.SmsPostData
import com.phone_box_app.data.network.ApiService
import com.phone_box_app.data.room.deviceinfo.DeviceInfoDao
import com.phone_box_app.data.room.deviceinfo.DeviceInfoEntity
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskDao
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskEntity
import com.phone_box_app.data.room.smslog.SmsEntity
import com.phone_box_app.data.room.smslog.SmsLogDao
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(
    private val apiService: ApiService,
    private val deviceInfoDao: DeviceInfoDao,
    private val smsLogDao: SmsLogDao,
    private val scheduledTaskDao: ScheduledTaskDao
) {

    /**
     * Method to register Device to server and save to local room db
     * emit the response as flow
     */
    fun registerDevice(data: DeviceRegistrationPostData) = flow {
        val response = apiService.registerDevice(data)
        /**
         * Save device info to local room db once saved to server
         */
        if (response.success == true) {
            deviceInfoDao.insertDeviceInfo(
                DeviceInfoEntity(
                    deviceId = data.deviceId,
                    deviceIdInt = response.deviceData?.id ?: 0,
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
     * DAO and remote methods for SMS
     * Insert sms to sms_log locally and return the row id
     * ---------------------------------------------------------------------------------------------
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

    suspend fun getPendingSmsLog(): List<SmsEntity> {
        return smsLogDao.getPendingSms()
    }

    /**
     * DAO and remote methods for Scheduled tasks
     *  --------------------------------------------------------------------------------------------
     */
    fun getScheduledTask(deviceId: Int, availableTasks: String) =
        flow { emit(apiService.getTask(deviceId, availableTasks)) }

    //insert Scheduled Task
    suspend fun insertScheduledTask(data: ScheduledTaskData): ScheduledTaskEntity {
        val entity = ScheduledTaskEntity(
            callerId = data.scheduledTask?.callerId,
            createdAt = data.scheduledTask?.createdAt,
            receiverId = data.scheduledTask?.receiverId,
            duration = data.scheduledTask?.duration,
            endDate = data.scheduledTask?.endDate,
            taskId = data.scheduledTask?.id,
            isActive = data.scheduledTask?.isActive,
            isPlanData = data.scheduledTask?.isPlanData,
            message = data.scheduledTask?.message,
            scheduledTime = data.scheduledTask?.scheduledTime,
            startDate = data.scheduledTask?.startDate,
            taskType = data.scheduledTask?.taskType,
            updatedAt = data.scheduledTask?.updatedAt,
            url = data.scheduledTask?.url,
            //receiver
            deviceId = data.receiverData?.deviceId,
            deviceName = data.receiverData?.deviceName,
            profileName = data.receiverData?.profileName,
            deviceModel = data.receiverData?.deviceModel,
            deviceSimNumber = data.receiverData?.deviceSimNumber,
            country = data.receiverData?.country
        )

        scheduledTaskDao.insertScheduledTask(entity)
        return entity

    }

    //get list of ScheduledTask
    suspend fun getScheduledTasks(): List<ScheduledTaskEntity> =
        scheduledTaskDao.getPendingScheduledTasks()

    //get array of taskId present in RoomDB
    suspend fun getArrayOfTaskPresent(): String {
        //return value inform of 23,23,34
        val stringBuilder = StringBuilder()
        scheduledTaskDao.getPendingScheduledTasks().forEach {
            stringBuilder.append(it.taskId).append(",")
        }
        val tasksString = stringBuilder.toString()
        if (tasksString.isNotEmpty()) {
            tasksString.substring(0, tasksString.length - 1)
        }
        return tasksString
    }

    //get Scheduled task by taskId
    suspend fun getScheduledTaskById(taskId: Int): ScheduledTaskEntity? =
        scheduledTaskDao.getTaskById(taskId)

    //delete task by taskId
    suspend fun deleteTaskById(taskId: Int) {
        scheduledTaskDao.deleteById(taskId)
    }

    //delete all tasks
    suspend fun deleteAllTasks() {
        scheduledTaskDao.deleteAllTask()
    }
}
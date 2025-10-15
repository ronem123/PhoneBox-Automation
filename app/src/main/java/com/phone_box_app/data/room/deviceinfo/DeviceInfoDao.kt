package com.phone_box_app.data.room.deviceinfo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


/**
 * Created by Ram Mandal on 15/10/2025
 * @System: Apple M1 Pro
 */
@Dao
interface DeviceInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceInfo(info: DeviceInfoEntity)

    @Query("SELECT * FROM device_info WHERE id = 0 LIMIT 1")
    suspend fun getDeviceInfo(): DeviceInfoEntity?

    @Query("DELETE FROM device_info")
    suspend fun clearDeviceInfo()
}

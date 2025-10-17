package com.phone_box_app.data.room.deviceinfo

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Ram Mandal on 15/10/2025
 * @System: Apple M1 Pro
 */

@Entity(tableName = "device_info")
data class DeviceInfoEntity(
    @PrimaryKey val id: Int = 0,
    val deviceId: String,
    val deviceIdInt: Int,
    val mobileNumber: String,
    val isRegistered: Boolean,
    val deviceModel: String,
    val deviceSimNumber: String,
    val countryCode: String,
    val deviceName: String,
    val profileName:String
)

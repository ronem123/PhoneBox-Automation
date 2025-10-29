package com.phone_box_app.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phone_box_app.data.room.deviceinfo.DeviceInfoDao
import com.phone_box_app.data.room.deviceinfo.DeviceInfoEntity
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskDao
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskEntity
import com.phone_box_app.data.room.smslog.SmsEntity
import com.phone_box_app.data.room.smslog.SmsLogDao


/**
 * Created by Ram Mandal on 15/10/2025
 * @System: Apple M1 Pro
 */

@Database(
    entities = [DeviceInfoEntity::class, SmsEntity::class, ScheduledTaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ArcDatabase : RoomDatabase() {
    abstract fun deviceInfoDao(): DeviceInfoDao
    abstract fun smsLogDao(): SmsLogDao
    abstract fun scheduledTaskDao(): ScheduledTaskDao

}

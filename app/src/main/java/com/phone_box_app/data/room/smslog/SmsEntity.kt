package com.phone_box_app.data.room.smslog

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
@Entity(tableName = "sms_logs")
data class SmsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String,
    val message: String,
    val timeStamp: Long,
    val isSynced: Boolean = false,
    val attempts: Int = 0
)
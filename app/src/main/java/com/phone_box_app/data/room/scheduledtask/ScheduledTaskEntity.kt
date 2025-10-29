package com.phone_box_app.data.room.scheduledtask

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Ram Mandal on 29/10/2025
 * @System: Apple M1 Pro
 */
@Entity(tableName = "scheduled_task")
data class ScheduledTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val idLocal: Long = 0,
    val callerId: Int?,
    val createdAt: String?,
    val duration: Int?,
    val endDate: String?,
    val taskId: Int?,
    val isActive: String?,
    val isPlanData: Int?,
    val message: String?,
    val scheduledTime: String?,
    val startDate: String?,
    val taskType: String?,
    val updatedAt: String?,
    val url: String?
)


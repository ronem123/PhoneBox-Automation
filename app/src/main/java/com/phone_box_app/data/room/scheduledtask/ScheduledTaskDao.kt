package com.phone_box_app.data.room.scheduledtask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


/**
 * Created by Ram Mandal on 29/10/2025
 * @System: Apple M1 Pro
 */
@Dao
interface ScheduledTaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertScheduledTask(entity: ScheduledTaskEntity): Long

    @Query("SELECT * FROM scheduled_task WHERE taskId =:taskId LIMIT 1")
    suspend fun getTaskById(taskId: Int): ScheduledTaskEntity?

    @Query("SELECT * FROM scheduled_task ORDER BY scheduledTime ASC")
    suspend fun getPendingScheduledTasks(): List<ScheduledTaskEntity>

    @Update
    suspend fun updateScheduledTask(task: ScheduledTaskEntity)

    @Query("DELETE FROM scheduled_task WHERE taskId = :taskId")
    suspend fun deleteById(taskId: Int)

    @Query("DELETE FROM scheduled_task")
    suspend fun deleteAllTask()
}
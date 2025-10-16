package com.phone_box_app.data.room.smslog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
@Dao
interface SmsLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSms(entity: SmsEntity): Long

    @Query("SELECT * FROM sms_logs WHERE id =:id LIMIT 1")
    suspend fun getSmsById(id: Long): SmsEntity?

    @Query("SELECT * FROM sms_logs WHERE isSynced = 0 ORDER BY timeStamp ASC")
    suspend fun getPendingSms(): List<SmsEntity>

    @Update
    suspend fun updateSms(sms: SmsEntity)

    @Query("DELETE FROM sms_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
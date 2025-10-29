package com.phone_box_app.di.module

import android.content.Context
import androidx.room.Room
import com.phone_box_app.data.room.ArcDatabase
import com.phone_box_app.data.room.deviceinfo.DeviceInfoDao
import com.phone_box_app.data.room.scheduledtask.ScheduledTaskDao
import com.phone_box_app.data.room.smslog.SmsLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Created by Ram Mandal on 16/10/2025
 * @System: Apple M1 Pro
 */
@Module
@InstallIn(SingletonComponent::class)
class ArcDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ArcDatabase {
        return Room.databaseBuilder(
            context, ArcDatabase::class.java, "phone_box_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDeviceInfoDao(db: ArcDatabase): DeviceInfoDao = db.deviceInfoDao()

    @Provides
    @Singleton
    fun provideSmsLogDao(db: ArcDatabase):      SmsLogDao = db.smsLogDao()

    @Provides
    @Singleton
    fun provideScheduledTaskDao(db: ArcDatabase): ScheduledTaskDao = db.scheduledTaskDao()

}
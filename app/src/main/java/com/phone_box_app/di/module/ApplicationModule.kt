package com.phone_box_app.di.module

import android.content.Context
import androidx.work.WorkManager
import com.phone_box_app.BuildConfig
import com.phone_box_app.core.dispatcher.DefaultDispatcherProvider
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.logger.AppLogger
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.core.networkhelper.NetworkHelper
import com.phone_box_app.core.networkhelper.NetworkHelperImpl
import com.phone_box_app.data.network.ApiService
import com.phone_box_app.data.network.PBHeaderInterceptor
import com.phone_box_app.di.ApiKey
import com.phone_box_app.di.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {


    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @ApiKey
    @Provides
    fun provideApiKey(): String = "IwAR3ZEbtRGyHewHmtWX4pKXii7cn04A2DlQ8RAc5NQutk1hjTRcXKtVyHMGQ"

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = "https://phonebox.tarifica.online/api/v1/"


    @Singleton
    @Provides
    fun provideArcApiService(
        @BaseUrl baseUrl: String,
        gsonFactory: GsonConverterFactory,
        apiKeyInterceptor: PBHeaderInterceptor
    ): ApiService {

        val client = OkHttpClient
            .Builder()
            .addInterceptor(apiKeyInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            client.addInterceptor(loggingInterceptor)
        }

        return Retrofit
            .Builder()
            .client(client.build()) //adding client to intercept all responses
            .baseUrl(baseUrl)
            .addConverterFactory(gsonFactory)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger = AppLogger()

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()


    @Provides
    @Singleton
    fun provideNetworkHelper(
        @ApplicationContext context: Context
    ): NetworkHelper {
        return NetworkHelperImpl(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): ArcDatabase {
//        return Room.databaseBuilder(
//            context,
//            ArcDatabase::class.java,
//            "phone_box_db"
//        ).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideDeviceInfoDao(db: ArcDatabase): DeviceInfoDao = db.deviceInfoDao()


}
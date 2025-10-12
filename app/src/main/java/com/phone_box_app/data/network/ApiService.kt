package com.phone_box_app.data.network

import com.phone_box_app.data.model.SaveDeviceResponse
import com.phone_box_app.data.model.ScheduledTaskResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("androidapp/get-article-json.php")
    suspend fun saveDevice(
        @Query("type") country: String = "1",
        @Query("fbclid") pageNum: String = "IwAR3ZEbtRGyHewHmtWX4pKXii7cn04A2DlQ8RAc5NQutk1hjTRcXKtVyHMGQ",
    ): SaveDeviceResponse

    @GET("androidapp/get-article-json.php")
    suspend fun getTask(
        @Query("type") country: String = "1",
        @Query("fbclid") pageNum: String = "IwAR3ZEbtRGyHewHmtWX4pKXii7cn04A2DlQ8RAc5NQutk1hjTRcXKtVyHMGQ",
    ): ScheduledTaskResponse



}
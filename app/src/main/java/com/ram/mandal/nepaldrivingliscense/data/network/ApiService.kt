package com.ram.mandal.nepaldrivingliscense.data.network

import com.ram.mandal.nepaldrivingliscense.data.model.SaveDeviceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("androidapp/get-article-json.php")
    suspend fun saveDevice(
        @Query("type") country: String = "1",
        @Query("fbclid") pageNum: String = "IwAR3ZEbtRGyHewHmtWX4pKXii7cn04A2DlQ8RAc5NQutk1hjTRcXKtVyHMGQ",
    ): SaveDeviceResponse



}
package com.ram.mandal.nepaldrivingliscense.data.repository

import com.ram.mandal.nepaldrivingliscense.data.network.ApiService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArcRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getNews(country: String, pageNum: String) =
        flow { emit(apiService.saveDevice()) }


}
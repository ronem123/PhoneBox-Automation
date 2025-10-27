package com.phone_box_app.data.network

import com.phone_box_app.di.ApiKey
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PBHeaderInterceptor @Inject constructor(@ApiKey private val apiKey: String) : Interceptor {

    @Throws(IOException::class)
    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
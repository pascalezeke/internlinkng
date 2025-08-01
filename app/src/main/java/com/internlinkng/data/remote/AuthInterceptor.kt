package com.internlinkng.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import com.internlinkng.data.model.UserSession

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        UserSession.token?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
} 
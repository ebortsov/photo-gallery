package com.github.ebortsov.photogallery.data.api

import com.github.ebortsov.photogallery.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AccessKeyInterceptor : Interceptor {
    // Add unsplash client_id key as a query parameter
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request
            .url
            .newBuilder()
            .addQueryParameter("client_id", BuildConfig.UNSPLASH_ACCESS_KEY)
            .build()

        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
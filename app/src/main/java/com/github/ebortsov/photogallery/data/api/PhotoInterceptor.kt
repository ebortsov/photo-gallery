package com.github.ebortsov.photogallery.data.api

import com.github.ebortsov.photogallery.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        val newUrl = originalRequest.url
            .newBuilder()
            .addQueryParameter("client_id", BuildConfig.UNSPLASH_ACCESS_KEY)
            .build()

        val newRequest = originalRequest
            .newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
package com.github.ebortsov.photogallery.api

import com.github.ebortsov.photogallery.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos?client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}")
    suspend fun fetchPhotos(@Query("per_page") perPage: Int = 10): List<PhotoResponse>
}
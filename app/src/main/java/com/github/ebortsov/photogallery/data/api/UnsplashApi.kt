package com.github.ebortsov.photogallery.data.api

import com.github.ebortsov.photogallery.BuildConfig
import com.github.ebortsov.photogallery.data.PHOTOS_PER_PAGE
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos?client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}")
    suspend fun fetchPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PHOTOS_PER_PAGE,
    ): List<PhotoResponse>
}
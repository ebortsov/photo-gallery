package com.github.ebortsov.photogallery.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos")
    suspend fun fetchPhotos(@Query("per_page") perPage: Int = 10): List<PhotoResponse>

    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 10
    ): PhotoSearchResponse
}
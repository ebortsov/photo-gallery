package com.github.ebortsov.photogallery.data.api

import com.github.ebortsov.photogallery.data.PHOTOS_PER_PAGE
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos")
    suspend fun fetchPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PHOTOS_PER_PAGE,
    ): List<PhotoResponse>

    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PHOTOS_PER_PAGE
    ): SearchPhotosResponse
}
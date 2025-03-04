package com.github.ebortsov.photogallery.data

import com.github.ebortsov.photogallery.data.api.PhotoInterceptor
import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.api.UnsplashApi
import com.github.ebortsov.photogallery.data.model.GalleryItem
import com.github.ebortsov.photogallery.data.model.toGalleryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PhotoRepository {
    private val unsplashApi: UnsplashApi

    init {
        // Object to retrieve adapter for json conversion
        // Used as an argument in MoshiConverterFactory in Retrofit.Builder
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") // Note the trailing slash
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        unsplashApi = retrofit.create(UnsplashApi::class.java)
    }

    suspend fun searchPhotos(query: String, perPage: Int = 10): List<GalleryItem> =
        unsplashApi.searchPhotos(query, perPage).results.map(PhotoResponse::toGalleryItem)

    suspend fun fetchPhotos(perPage: Int = 10): List<GalleryItem> =
        unsplashApi.fetchPhotos(perPage).map(PhotoResponse::toGalleryItem)
}
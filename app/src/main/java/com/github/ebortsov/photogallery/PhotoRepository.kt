package com.github.ebortsov.photogallery

import com.github.ebortsov.photogallery.api.UnsplashApi
import com.github.ebortsov.photogallery.model.GalleryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

        val apiCreator = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") // Note the trailing slash
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        unsplashApi = apiCreator.create(UnsplashApi::class.java)
    }

    suspend fun fetchPhotos(perPage: Int = 10): List<GalleryItem> {
        fun slugToTitle(slug: String) =
            slug.split("-")
                .dropLast(1)
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

        val response = unsplashApi.fetchPhotos(perPage)
        return response.map { responseItem ->
            GalleryItem(
                title = responseItem.slug?.let { slugToTitle(it) } ?: "Missing Title",
                id = responseItem.id,
                urls = GalleryItem.Urls(
                    regular = responseItem.urls.regular,
                    small = responseItem.urls.small,
                    thumb = responseItem.urls.thumb
                )
            )
        }
    }
}
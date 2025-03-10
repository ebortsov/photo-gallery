package com.github.ebortsov.photogallery.data

import android.util.Log
import com.github.ebortsov.photogallery.data.api.AccessKeyInterceptor
import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.api.UnsplashApi
import com.github.ebortsov.photogallery.models.GalleryItem
import com.github.ebortsov.photogallery.models.toGalleryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.random.Random

const val PHOTOS_PER_PAGE = 30

interface PhotoRepository {
    suspend fun fetchPhotos(page: Int): List<GalleryItem>
    suspend fun searchPhotos(query: String, page: Int): List<GalleryItem>
}

class PhotoRepositoryUnsplashApi : PhotoRepository {
    private val unsplashApi: UnsplashApi

    init {
        // Object to retrieve adapter for json conversion
        // Used as an argument in MoshiConverterFactory in Retrofit.Builder
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(AccessKeyInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

        val apiCreator = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/") // Note the trailing slash
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()

        unsplashApi = apiCreator.create(UnsplashApi::class.java)
    }

    override suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        val response = unsplashApi.fetchPhotos(page)
        return response.map(PhotoResponse::toGalleryItem)
    }

    override suspend fun searchPhotos(query: String, page: Int): List<GalleryItem> {
        val response = unsplashApi.searchPhotos(query, page)
        return response.results.map(PhotoResponse::toGalleryItem)
    }
}

class PhotoRepositoryMock : PhotoRepository {
    override suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        val result = (1..PHOTOS_PER_PAGE).map {
            val photoId = Random.nextLong().toString()
            val photoUrl = loremPicsumPhotoUrl(photoId) // Nuh just fetch the same small photo
            PhotoResponse(
                id = photoId,
                urls = PhotoResponse.Urls(
                    raw = photoUrl,
                    full = photoUrl,
                    regular = photoUrl,
                    small = photoUrl,
                    thumb = photoUrl
                ),
                description = "Photo with id $photoId",
                slug = "photo-$page-$photoId-RANDOMSUFFIX"
            )
        }
        delay(2000) // simulate the delay

        if (Random.nextInt(6) == 0) { // randomly throw exception for page with change 1/6
            throw Exception("PhotoMockRepository test exception")
        }
        return result.map(PhotoResponse::toGalleryItem)
    }

    override suspend fun searchPhotos(query: String, page: Int): List<GalleryItem> {
        Log.d(this::class.simpleName, "searchPhotos query=\"$query\" page=$page")
        return fetchPhotos(page)
    }

    private fun loremPicsumPhotoUrl(id: String): String {
        return "https://picsum.photos/seed/$id/200"
    }
}
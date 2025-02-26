package com.github.ebortsov.photogallery.data

import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.api.UnsplashApi
import com.github.ebortsov.photogallery.data.model.GalleryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.random.Random

const val PHOTOS_PER_PAGE = 50

interface PhotoRepository {
    suspend fun fetchPhotos(page: Int): List<PhotoResponse>
}

class PhotoRepositoryUnsplashApi : PhotoRepository {
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

    override suspend fun fetchPhotos(page: Int): List<PhotoResponse> {
        val response = unsplashApi.fetchPhotos(page)
        return response
    }
}

class PhotoRepositoryMock : PhotoRepository {
    override suspend fun fetchPhotos(page: Int): List<PhotoResponse> {
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
                slug = "photo-$photoId-RANDOMSUFFIX"
            )
        }
        delay(2000) // simulate the delay
        return result
    }
    private fun loremPicsumPhotoUrl(id: String): String {
        return "https://picsum.photos/seed/$id/200"
    }
}
package com.github.ebortsov.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.ebortsov.photogallery.models.GalleryItem

private const val TAG = "GalleryPagingSource"

class GalleryPagingSource(
    private val searchQuery: String
) : PagingSource<Int, GalleryItem>() {
    private val photoRepository = PhotoRepositoryUnsplashApi()
    private val preferencesRepository = PreferencesRepository.getInstance()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val pageIndex = params.key ?: 1

        try {
            val photos = if (searchQuery.isEmpty())
                photoRepository.fetchPhotos(pageIndex)
            else
                photoRepository.searchPhotos(searchQuery, pageIndex)

            val prevKey = if (pageIndex > 1) pageIndex - 1 else null
            val nextKey = if (photos.isNotEmpty()) pageIndex + 1 else null

            // Write back to the preference repository
            if (pageIndex == 1) {
                // The most recent photos are fetched at page 1
                val lastFetchedPhotoId = photos.getOrNull(0)?.id ?: ""
                Log.d(TAG, "lastFetchedPhotoId=$lastFetchedPhotoId")
                preferencesRepository.writeLastFetchedPhotoId(lastFetchedPhotoId)
            }

            Log.d(TAG, "load: fetched: $photos")
            return LoadResult.Page(
                photos,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (ex: Exception) {
            Log.d(TAG, "load: failed to fetch photos, $ex")
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int = 1
}
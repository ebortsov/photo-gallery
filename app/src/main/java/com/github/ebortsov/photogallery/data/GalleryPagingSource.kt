package com.github.ebortsov.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.model.GalleryItem

private const val TAG = "GalleryPagingSource"

class GalleryPagingSource(
    private val photoRepository: PhotoRepository,
    private val searchQuery: String
) : PagingSource<Int, GalleryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val pageIndex = params.key ?: 1

        try {
            val photos = if (searchQuery.isEmpty())
                photoRepository.fetchPhotos(pageIndex)
            else
                photoRepository.searchPhotos(searchQuery, pageIndex)

            Log.d(TAG, "load: fetched: $photos")
            return LoadResult.Page(
                photos,
                prevKey = if (pageIndex > 1) pageIndex - 1 else null,
                nextKey = pageIndex + 1
            )
        } catch (ex: Exception) {
            Log.d(TAG, "load: failed to fetch photos, $ex")
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int = 1
}
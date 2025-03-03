package com.github.ebortsov.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.model.GalleryItem

private const val TAG = "GalleryPagingSource"

class GalleryPagingSource(
    private val photoRepository: PhotoRepository
) : PagingSource<Int, GalleryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val pageIndex = params.key ?: 1

        try {
            val photos = photoRepository.fetchPhotos(pageIndex)
            Log.d(TAG, "load: successfully fetched ${photos}: $photos")
            return LoadResult.Page(
                photos.map(PhotoResponse::toGalleryItem),
                prevKey = if (pageIndex > 1) pageIndex - 1 else null,
                nextKey = pageIndex + 1
            )
        } catch (ex: Exception) {
            Log.e(TAG, "load: $ex")
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val currentPage = state.closestPageToPosition(anchorPosition) ?: return null
        return currentPage.prevKey?.plus(1) ?: currentPage.nextKey?.minus(1)
    }
}
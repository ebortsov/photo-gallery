package com.github.ebortsov.photogallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.model.GalleryItem

class GalleryPagingSource(
    private val photoRepository: PhotoRepository
) : PagingSource<Int, GalleryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val pageIndex = params.key ?: 1

        try {
            val photos = photoRepository.fetchPhotos(pageIndex)
            return LoadResult.Page(
                photos.map(PhotoResponse::toGalleryItem),
                prevKey = if (pageIndex > 1) pageIndex - 1 else null,
                nextKey = pageIndex + 1
            )
        } catch (ex: Exception) {
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val currentPage = state.closestPageToPosition(anchorPosition) ?: return null
        return currentPage.prevKey?.plus(1) ?: currentPage.nextKey?.minus(1)
    }
}
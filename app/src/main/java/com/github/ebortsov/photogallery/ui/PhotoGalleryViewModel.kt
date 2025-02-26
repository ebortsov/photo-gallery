package com.github.ebortsov.photogallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.ebortsov.photogallery.data.GalleryPagingSource
import com.github.ebortsov.photogallery.data.PHOTOS_PER_PAGE
import com.github.ebortsov.photogallery.data.PhotoRepositoryMock
import com.github.ebortsov.photogallery.data.PhotoRepositoryUnsplashApi

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository = PhotoRepositoryMock()

    val galleryPages = Pager(
        PagingConfig(pageSize = PHOTOS_PER_PAGE)
    ) {
        GalleryPagingSource(photoRepository)
    }
        .flow
        .cachedIn(viewModelScope)
}
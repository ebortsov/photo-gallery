package com.github.ebortsov.photogallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.ebortsov.photogallery.data.GalleryPagingSource
import com.github.ebortsov.photogallery.data.PHOTOS_PER_PAGE
import com.github.ebortsov.photogallery.data.PhotoRepository
import com.github.ebortsov.photogallery.data.PhotoRepositoryUnsplashApi
import com.github.ebortsov.photogallery.data.PreferencesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository: PhotoRepository = PhotoRepositoryUnsplashApi()
    private val preferencesRepository = PreferencesRepository.get()

    private var _searchQuery = MutableStateFlow("")
    private val searchQuery = _searchQuery.asStateFlow()

    private var pagingSource: GalleryPagingSource? = null

    private var _isPagingSourceReady = MutableStateFlow(false)
    val isPagingSourceReady = _isPagingSourceReady.asStateFlow()

    private suspend fun initialLoad() {
        // Load the search query stored in the preferences
        val storedSearchQuery = viewModelScope.async { preferencesRepository.readSearchQuery().first() }
        _searchQuery.value = storedSearchQuery.await()
    }

    init {
        viewModelScope.launch {
            // Suspend any subscribers before the data is loaded
            initialLoad()

            launch {
                searchQuery.collectLatest {
                    invalidatePagingSource()
                    _isPagingSourceReady.value = true
                }
            }
        }
    }

    // Force Pager to load new PagingSource
    private fun invalidatePagingSource() {
        pagingSource?.invalidate()
    }

    val galleryPages = Pager(
        PagingConfig(pageSize = PHOTOS_PER_PAGE)
    ) {
        GalleryPagingSource(
            photoRepository,
            searchQuery.value
        ).also { pagingSource = it } // Store the reference to the instance of PagingStore inside the ViewModel
    }
        .flow
        .cachedIn(viewModelScope)


    fun setQuery(newQuery: String) {
        viewModelScope.launch {
            preferencesRepository.writeSearchQuery(newQuery)
        }
    }
}
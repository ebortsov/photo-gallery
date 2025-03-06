package com.github.ebortsov.photogallery.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.github.ebortsov.photogallery.data.GalleryPagingSource
import com.github.ebortsov.photogallery.data.PHOTOS_PER_PAGE
import com.github.ebortsov.photogallery.data.PhotoRepository
import com.github.ebortsov.photogallery.data.PhotoRepositoryMock
import com.github.ebortsov.photogallery.data.PhotoRepositoryUnsplashApi
import com.github.ebortsov.photogallery.data.PreferencesRepository
import com.github.ebortsov.photogallery.data.SearchHistoryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class GalleryViewModel : ViewModel() {
    private val photoRepository: PhotoRepository = PhotoRepositoryUnsplashApi()
    private val preferencesRepository = PreferencesRepository.getInstance()
    private val searchHistoryRepository = SearchHistoryRepository.getInstance()

    private var _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _searchHistory = MutableStateFlow(listOf<String>())
    val searchHistory = _searchHistory.asStateFlow()

    private var pagingSource: GalleryPagingSource? = null

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private suspend fun initialLoad() {
        // Load the search query stored in the preferences
        val storedSearchQuery =
            viewModelScope.async { preferencesRepository.readSearchQuery().first() }
        _searchQuery.value = storedSearchQuery.await()
        _isLoading.value = true
    }

    init {
        viewModelScope.launch {
            // Suspend any subscribers before the data is loaded
            initialLoad()

            launch {
                preferencesRepository.readSearchQuery().collectLatest {
                    _searchQuery.value = it
                    invalidatePagingSource()
                }
            }

            launch {
                searchHistoryRepository.getRecentQueriesAsFlow().collectLatest {
                    _searchHistory.value = it
                    Log.d(TAG, "recentQueries $it")
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
        ).also {
            pagingSource = it
        } // Store the reference to the instance of PagingStore inside the ViewModel
    }
        .flow
        .cachedIn(viewModelScope)


    fun setQuery(newQuery: String) {
        viewModelScope.launch {
            launch {
                // Store query to the preferences
                preferencesRepository.writeSearchQuery(newQuery)
            }
            launch {
                // Store the query to the search history
                searchHistoryRepository.addSearchQuery(newQuery)
            }
        }
    }
}
package com.github.ebortsov.photogallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ebortsov.photogallery.model.GalleryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PhotoGalleryViewModel"

class PhotoGalleryViewModel : ViewModel() {
    private val photoRepository = PhotoRepository()

    private val _galleryItems = MutableStateFlow(listOf<GalleryItem>())
    val galleryItems get() = _galleryItems.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _galleryItems.value = photoRepository.fetchPhotos(30)
                Log.d(TAG, "Remote fetch of photos. Count of items received: ${galleryItems.value.size}")
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to fetch photos", ex)
            }
        }
    }
}
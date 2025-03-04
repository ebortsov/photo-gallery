package com.github.ebortsov.photogallery.ui

import com.github.ebortsov.photogallery.data.model.GalleryItem

data class PhotoGalleryUiState(
    val images: List<GalleryItem> = listOf(),
    val query: String = ""
)
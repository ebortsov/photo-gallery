package com.github.ebortsov.photogallery.data.model

data class GalleryItem(
    val title: String,
    val id: String,
    val urls: Urls
) {
    data class Urls(
        val regular: String,
        val small: String,
        val thumb: String
    )
}
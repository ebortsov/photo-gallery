package com.github.ebortsov.photogallery.models

import android.net.Uri

data class GalleryItem(
    val title: String,
    val id: String,
    val urls: Urls,
    val slug: String
) {
    data class Urls(
        val regular: String,
        val small: String,
        val thumb: String
    )

    val unsplashPhotoPageUri: Uri
        get() = Uri.parse("https://unsplash.com/photos")
            .buildUpon()
            .appendPath(slug)
            .build()
}
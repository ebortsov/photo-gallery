package com.github.ebortsov.photogallery.data.model

import com.github.ebortsov.photogallery.data.api.PhotoResponse

fun slugToTitle(slug: String) =
    slug.split("-")
        .dropLast(1)
        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

fun PhotoResponse.toGalleryItem(): GalleryItem =
    GalleryItem(
        title = slug?.let { slugToTitle(it) } ?: "Missing Title",
        id = id,
        urls = GalleryItem.Urls(
            regular = urls.regular,
            small = urls.small,
            thumb = urls.thumb
        )
    )

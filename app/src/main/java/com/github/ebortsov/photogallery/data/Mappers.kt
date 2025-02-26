package com.github.ebortsov.photogallery.data

import com.github.ebortsov.photogallery.data.api.PhotoResponse
import com.github.ebortsov.photogallery.data.model.GalleryItem

private fun slugToTitle(slug: String) =
    slug.split("-")
        .dropLast(1)
        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

internal fun PhotoResponse.toGalleryItem(): GalleryItem {
    return GalleryItem(
        title = slug?.let { slugToTitle(it) } ?: "Missing Title",
        id = id,
        urls = GalleryItem.Urls(
            regular = urls.regular,
            small = urls.small,
            thumb = urls.thumb
        )
    )
}
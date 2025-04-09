package com.github.ebortsov.photogallery.models

import com.github.ebortsov.photogallery.data.api.PhotoResponse

private fun slugToTitle(slug: String) =
    slug.split("-")
        .dropLast(1)
        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

internal fun PhotoResponse.toGalleryItem(): GalleryItem {
    return GalleryItem(
        title = slug?.let { slugToTitle(it) } ?: "Missing Title",
        id = id,
        slug = slug ?: "",
        urls = GalleryItem.Urls(
            regular = urls.regular,
            small = urls.small,
            thumb = urls.thumb
        )
    )
}
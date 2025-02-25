package com.github.ebortsov.photogallery.api

data class PhotoResponse(
    val id: String,
    val urls: Urls,
    val description: String?,
    val slug: String?
    ) {
    data class Urls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    )
}
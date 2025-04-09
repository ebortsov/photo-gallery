package com.github.ebortsov.photogallery.data.api

import com.squareup.moshi.Json

data class SearchPhotosResponse(
    val total: Int,
    @Json(name="total_pages") val totalPages: Int,
    val results: List<PhotoResponse>
)
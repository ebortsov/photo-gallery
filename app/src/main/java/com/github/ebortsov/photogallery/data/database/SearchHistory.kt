package com.github.ebortsov.photogallery.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val query: String,
    val timestamp: Instant
)
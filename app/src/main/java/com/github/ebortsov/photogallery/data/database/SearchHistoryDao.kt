package com.github.ebortsov.photogallery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentQueriesAsFlow(): Flow<List<SearchHistory>>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    suspend fun getAllQueries(): List<SearchHistory>

    @Insert
    suspend fun insertSearchHistoryQuery(searchHistoryQuery: SearchHistory)
}
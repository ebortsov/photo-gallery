package com.github.ebortsov.photogallery.data

import com.github.ebortsov.photogallery.data.database.SearchHistory
import com.github.ebortsov.photogallery.data.database.SearchHistoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

class SearchHistoryRepository private constructor(private val dao: SearchHistoryDao) {
    suspend fun addSearchQuery(query: String) {
        val instant = OffsetDateTime.now().toInstant()
        dao.insertSearchHistoryQuery(
            SearchHistory(
                query = query,
                timestamp = instant
            )
        )
    }

    fun getRecentQueriesAsFlow(): Flow<List<String>> {
        return dao.getRecentQueriesAsFlow().map { list ->
            list.map { it.query }
        }
    }

    companion object {
        private var instance: SearchHistoryRepository? = null

        fun getInstance() = checkNotNull(instance) { "SearchHistoryRepository must be initialized" }

        fun initialize(dao: SearchHistoryDao) {
            instance = SearchHistoryRepository(dao)
        }
    }
}
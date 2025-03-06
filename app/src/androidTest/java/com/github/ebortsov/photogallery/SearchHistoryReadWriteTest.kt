package com.github.ebortsov.photogallery

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ebortsov.photogallery.data.database.AppDatabase
import com.github.ebortsov.photogallery.data.database.SearchHistory
import com.github.ebortsov.photogallery.data.database.SearchHistoryDao
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.runner.RunWith

import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SearchHistoryReadWriteTest {
    private lateinit var searchHistoryDao: SearchHistoryDao
    private lateinit var appDatabase: AppDatabase

    private lateinit var inputQueries: List<SearchHistory>

    @Before
    fun setup() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        searchHistoryDao = appDatabase.getSearchHistoryDao()

        // Generate mock queries
        inputQueries = List(
            size = 50
        ) { index ->
            SearchHistory(
                query = "query$index",
                timestamp = Instant.ofEpochSecond(Random.nextLong() % 10000)
            )
        }


        // Insert the data
        runBlocking {
            inputQueries.forEach {
                launch {
                    searchHistoryDao.insertSearchHistoryQuery(it)
                }
            }
        }
    }

    @Test
    fun getAllQueriesTest() {
        var resultQueries: List<SearchHistory>
        // Receive the data
        runBlocking {
            resultQueries = searchHistoryDao.getAllQueries()
        }

        // Check that the result indeed match (and the results are sorted in descending order)
        val sortedInputQueries = inputQueries.sortedByDescending { it.timestamp }
        assert((1..<sortedInputQueries.size).all { i ->
            resultQueries[i].query == sortedInputQueries[i].query &&
                    resultQueries[i].timestamp == sortedInputQueries[i].timestamp
        })
    }

    @Test
    fun getRecentQueriesTest() {
        var resultQueries = listOf<SearchHistory>()
        runBlocking {
            resultQueries = searchHistoryDao.getRecentQueriesAsFlow().take(1).last()
        }

        // Check that the queries are sorted in descending order and match the corresponding inserted queries
        val sortedInputQueries = inputQueries.sortedByDescending { it.timestamp }
        assert((1..<resultQueries.size).all { i ->
            resultQueries[i].query == sortedInputQueries[i].query &&
                    resultQueries[i].timestamp == sortedInputQueries[i].timestamp
        })
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }
}
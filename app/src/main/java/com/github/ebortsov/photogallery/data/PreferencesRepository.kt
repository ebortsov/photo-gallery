package com.github.ebortsov.photogallery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery: Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
        }
    }

    val lastFetchedPhotoId: Flow<String> = dataStore.data.map {
        it[LAST_FETCHED_PHOTO_ID_KEY] ?: ""
    }

    suspend fun setLastFetchedPhotoId(lastResultId: String) {
        dataStore.edit {
            it[LAST_FETCHED_PHOTO_ID_KEY] = lastResultId
        }
    }

    val isPolling: Flow<Boolean> = dataStore.data.map {
        it[IS_POLLING_KEY] ?: false
    }

    suspend fun setPolling(isPolling: Boolean) {
        dataStore.edit {
            it[IS_POLLING_KEY] = isPolling
        }
    }
    companion object {
        private var instance: PreferencesRepository? = null

        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val LAST_FETCHED_PHOTO_ID_KEY = stringPreferencesKey("last_result_id")
        private val IS_POLLING_KEY = booleanPreferencesKey("isPolling")

        fun initialize(context: Context) {
            if (instance == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }

                instance = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository =
            instance ?: throw IllegalStateException("PreferencesRepository must be initialized")
    }
}
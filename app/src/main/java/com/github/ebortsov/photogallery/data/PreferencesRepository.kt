package com.github.ebortsov.photogallery.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val SEARCH_QUERY = stringPreferencesKey("search-query")
    }

    fun readSearchQuery(): Flow<String> = dataStore.data.map {
        val searchQuery: String = it[PreferencesKeys.SEARCH_QUERY] ?: ""
        searchQuery
    }

    suspend fun writeSearchQuery(query: String) {
        dataStore.edit { pref ->
            pref[PreferencesKeys.SEARCH_QUERY] = query
        }
    }

    companion object {
        private var instance: PreferencesRepository? = null

        fun initialize(dataStore: DataStore<Preferences>) {
            instance = PreferencesRepository(dataStore)
        }

        fun getInstance() = checkNotNull(instance) { "PreferencesRepository must be initialized" }
    }
}
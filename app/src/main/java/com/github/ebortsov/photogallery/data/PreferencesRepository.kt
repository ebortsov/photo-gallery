package com.github.ebortsov.photogallery.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KProperty

class PreferencesDelegate<T>(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T
) {
    operator fun getValue(owner: Any, property: KProperty<*>): Flow<T> = dataStore.data.map {
        it[key] ?: defaultValue
    }
}

suspend fun <T> DataStore<Preferences>.write(value: T, key: Preferences.Key<T>) {
    edit {
        if (value == null) {
            it.remove(key)
        } else {
            it[key] = value
        }
    }
}

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val SEARCH_QUERY = stringPreferencesKey("search-query")
        val LAST_FETCHED_PHOTO_ID = stringPreferencesKey("last-fetched-photo-id")
        val IS_POLLING_ACTIVE = booleanPreferencesKey("is-polling-active")
    }

    val isPollingActive by PreferencesDelegate(
        dataStore,
        PreferencesKeys.IS_POLLING_ACTIVE,
        false
    )

    suspend fun writeIsPollingActive(isPollingActive: Boolean) =
        dataStore.write(isPollingActive, PreferencesKeys.IS_POLLING_ACTIVE)

    val lastFetchedPhotoId by PreferencesDelegate(
        dataStore,
        PreferencesKeys.LAST_FETCHED_PHOTO_ID,
        ""
    )

    suspend fun writeLastFetchedPhotoId(lastFetchedPhotoId: String) =
        dataStore.write(lastFetchedPhotoId, PreferencesKeys.LAST_FETCHED_PHOTO_ID)

    val searchQuery: Flow<String> by PreferencesDelegate(
        dataStore,
        PreferencesKeys.SEARCH_QUERY,
        ""
    )

    suspend fun writeSearchQuery(query: String) =
        dataStore.write(query, PreferencesKeys.SEARCH_QUERY)

    companion object {
        private var instance: PreferencesRepository? = null

        fun initialize(dataStore: DataStore<Preferences>) {
            instance = PreferencesRepository(dataStore)
        }

        fun getInstance() = checkNotNull(instance) { "PreferencesRepository must be initialized" }
    }
}
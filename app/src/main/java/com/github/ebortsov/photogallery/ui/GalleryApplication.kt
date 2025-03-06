package com.github.ebortsov.photogallery.ui

import android.app.Application
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.github.ebortsov.photogallery.data.PreferencesRepository
import com.github.ebortsov.photogallery.data.SearchHistoryRepository
import com.github.ebortsov.photogallery.data.database.AppDatabase

class GalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize preferences
        val preferencesDataStore = PreferenceDataStoreFactory.create {
            preferencesDataStoreFile("settings")
        }
        PreferencesRepository.initialize(preferencesDataStore)

        // Initialize AppDatabase
        AppDatabase.initialize(applicationContext)

        // Initialize SearchHistoryRepository
        SearchHistoryRepository.initialize(AppDatabase.getInstance().getSearchHistoryDao())
    }
}
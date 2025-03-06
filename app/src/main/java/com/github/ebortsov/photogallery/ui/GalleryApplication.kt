package com.github.ebortsov.photogallery.ui

import android.app.Application
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.github.ebortsov.photogallery.data.PreferencesRepository

class GalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize preferences
        val preferencesDataStore = PreferenceDataStoreFactory.create {
            preferencesDataStoreFile("settings")
        }
        PreferencesRepository.initialize(preferencesDataStore)
    }
}
package com.github.ebortsov.photogallery.data

import android.app.Application

class PhotoGalleryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(applicationContext)
    }
}
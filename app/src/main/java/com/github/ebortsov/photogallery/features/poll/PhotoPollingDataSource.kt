package com.github.ebortsov.photogallery.features.poll

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val POLL_RATE_MINUTES = 15L
private const val POLL_PHOTOS_WORK_NAME = "PollPhotosWork"
private const val POLL_PHOTOS_WORK_TAG = "PollPhotosWorkTag"

private const val TAG = "PhotoPollingDataSource"

class PhotoPollingDataSource(
    private val workManager: WorkManager
) {
    fun startPhotoPolling() {
        Log.d(TAG, "startPhotoPolling")
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val request = PeriodicWorkRequestBuilder<PollWorker>(
            POLL_RATE_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(POLL_PHOTOS_WORK_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            POLL_PHOTOS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelPhotoPolling() {
        Log.d(TAG, "cancelPhotoPolling")
        workManager.cancelAllWorkByTag(POLL_PHOTOS_WORK_TAG)
    }
}
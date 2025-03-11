package com.github.ebortsov.photogallery.features.poll

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ebortsov.photogallery.data.PhotoRepositoryUnsplashApi
import com.github.ebortsov.photogallery.data.PreferencesRepository
import com.github.ebortsov.photogallery.R
import com.github.ebortsov.photogallery.ui.MainActivity
import kotlinx.coroutines.flow.first

private const val TAG = "PollWorker"

class PollWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val photoRepository = PhotoRepositoryUnsplashApi()
    private val preferencesRepository = PreferencesRepository.getInstance()

    override suspend fun doWork(): Result {
        val query = preferencesRepository.searchQuery.first()
        val lastId = preferencesRepository.lastFetchedPhotoId.first()

        // Obtain photos that correspond to the stored query
        val photos = try {
            if (query.isEmpty()) {
                photoRepository.fetchPhotos(1)
            } else {
                photoRepository.searchPhotos(query, 1)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "doWork: $ex")
            return Result.failure()
        }

        val currentLastId = photos.getOrNull(0)?.id ?: ""
        // Compare the stored id the recent photo id
        Log.d(TAG, "doWork: (currentLastId=${currentLastId}; lastId=$lastId)")
        if (photos.isNotEmpty() && currentLastId != lastId) {
            // The new photos has appeared and the app should notify the user
            val notificationManager = NotificationManagerCompat.from(appContext)
            if (notificationManager.areNotificationsEnabled()) {
                // The notifications are allowed
                val notification = makeNotification(appContext)
                notificationManager.notify(0, notification)
            } else {
                Log.w(TAG, "doWork: notifications are not enabled")
            }
        }
        return Result.success()
    }
}
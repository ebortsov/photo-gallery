package com.github.ebortsov.photogallery.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.ebortsov.photogallery.NOTIFICATION_CHANNEL_ID
import com.github.ebortsov.photogallery.data.PhotoRepository
import com.github.ebortsov.photogallery.data.PreferencesRepository
import com.github.ebortsov.photogallery.ui.MainActivity
import kotlinx.coroutines.flow.first
import com.github.ebortsov.photogallery.R

class PollWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val preferencesRepository = PreferencesRepository.get()
        val photoRepository = PhotoRepository()

        val query = preferencesRepository.storedQuery.first()
        val lastFetchedPhotoId = preferencesRepository.lastFetchedPhotoId.first()

        if (query.isEmpty()) {
            Log.i(this::class.simpleName, "No saved query, finishing early")
            return Result.success()
        }

        return try {
            val items = photoRepository.searchPhotos(query)
            if (items.isNotEmpty()) {
                val currentLastPhoto = items.first()
                if (currentLastPhoto.id == lastFetchedPhotoId) {
                    // The results are the same
                    Log.i(
                        this::class.simpleName,
                        "Still have the same result: lastFetchedPhotoId = $lastFetchedPhotoId"
                    )
                } else {
                    // The result has been changed (i.e. new photo has been uploaded)
                    Log.i(
                        this::class.simpleName,
                        "Got a new result: ${currentLastPhoto.id}"
                    )
                    preferencesRepository.setLastFetchedPhotoId(currentLastPhoto.id)
                    notifyUser()
                }
            }
            return Result.success()
        } catch (ex: Exception) {
            return Result.failure()
        }
    }

    private fun notifyUser() {
        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val resources = context.resources
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(0, notification)
            Log.i(this::class.simpleName, "notifyUser")
        } else {
            Log.i(this::class.simpleName, "Manifest.permission.POST_NOTIFICATIONS is not granted")
        }
    }
}

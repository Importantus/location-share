package digital.fischers.locationshare.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.fischers.locationshare.R
import digital.fischers.locationshare.domain.repositories.LocationRepository


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: LocationRepository
) : CoroutineWorker(
    appContext, workerParams
) {
    companion object {
        const val WORK_NAME = "SyncWorker"
        private const val CHANNEL_ID = "sync_notification"
        private const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        try {
            // Todo: Only set foreground if the current location has to be fetched
            setForeground(getForegroundInfo())

            locationRepository.ensureDbHasLocation()
            locationRepository.sendLocationData()
            return Result.success()
        } catch (e: Exception) {
            Log.d("SyncWorker", "Error: ${e.message}, ${e.stackTraceToString()}")
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = createNotification()

        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
    }

    private fun createNotificationChannel() {
        val name = "Standort synchronisieren"
        val descriptionText = "Synchronisiert den Standort mit dem Server"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Getting current location")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
    }
}
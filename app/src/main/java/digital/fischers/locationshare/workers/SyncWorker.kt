package digital.fischers.locationshare.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import digital.fischers.locationshare.domain.repositories.RemoteRepository


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteRepository: RemoteRepository
) : CoroutineWorker(
    appContext, workerParams
) {
    companion object {
        const val WORK_NAME = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        try {
            remoteRepository.sendLocationData()
            return Result.success()
        } catch (e: Exception) {
            Log.d("SyncWorker", "Error: ${e.message}")
            return Result.retry()
        }
    }
}
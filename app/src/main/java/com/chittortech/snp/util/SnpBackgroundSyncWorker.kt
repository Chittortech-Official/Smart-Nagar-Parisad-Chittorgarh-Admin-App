package com.chittortech.snp.util

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class SnpBackgroundSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Trigger comprehensive status diff & notification check
            SnpTicketMonitor.syncTickets(appContext)

            // Self-schedule next one-time check to ensure frequent background checks
            scheduleNextOneTimeSync(appContext)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_PERIODIC_WORK = "snp_background_notification_sync"
        private const val UNIQUE_ONETIME_WORK = "snp_background_sync_onetimer"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SnpBackgroundSyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // 5 min flex window
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicRequest
            )

            // Also trigger an initial sync immediately
            val immediateRequest = OneTimeWorkRequestBuilder<SnpBackgroundSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(immediateRequest)
        }

        fun scheduleNextOneTimeSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<SnpBackgroundSyncWorker>()
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_ONETIME_WORK,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }
    }
}

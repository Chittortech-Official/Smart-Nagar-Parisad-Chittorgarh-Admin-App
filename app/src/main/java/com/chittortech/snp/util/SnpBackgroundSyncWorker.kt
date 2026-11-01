package com.chittortech.snp.util

import android.content.Context
import androidx.work.*
import com.chittortech.snp.data.SupabaseManager
import java.util.concurrent.TimeUnit

class SnpBackgroundSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val tickets = SupabaseManager.getTickets()

            // Filter tickets within active 5-day window
            val activeTickets = tickets.filter { ticket ->
                DateUtils.isLastNDays(ticket.registrationTime, 5)
            }

            for (ticket in activeTickets) {
                // 1. Check for unnotified new complaints
                if (!SnpNotificationManager.isAlreadyNotified(appContext, ticket.ticketNumber)) {
                    val desc = ticket.problemDescription
                    val isEmerg = desc.contains("आग") || desc.contains("मृत") || desc.contains("दमकल") || desc.contains("दुर्घटना") || desc.contains("धंस")
                    SnpNotificationManager.showNewComplaintAlert(
                        context = appContext,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = desc,
                        citizenName = ticket.citizenName,
                        isEmergency = isEmerg
                    )
                }

                // 2. Check for unnotified SLA overdues
                val overdueKey = "${ticket.ticketNumber}_overdue"
                if ((ticket.isOverdue || ticket.status.equals("OVERDUE", ignoreCase = true)) &&
                    !SnpNotificationManager.isAlreadyNotified(appContext, overdueKey)
                ) {
                    SnpNotificationManager.showOverdueAlert(
                        context = appContext,
                        ticketNumber = ticket.ticketNumber,
                        wardNumber = ticket.wardNumber,
                        categoryTitle = ticket.problemDescription
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "snp_background_notification_sync"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val periodicRequest = PeriodicWorkRequestBuilder<SnpBackgroundSyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // 5 min flex interval
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }
}


package com.chittortech.snp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SnpBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            SnpNotificationManager.initNotificationChannels(context)
            SnpLiveMonitoringService.start(context)
            SnpBackgroundSyncWorker.schedulePeriodicSync(context)
        }
    }
}


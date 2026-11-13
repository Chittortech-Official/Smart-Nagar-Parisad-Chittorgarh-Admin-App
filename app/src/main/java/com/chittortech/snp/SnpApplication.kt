package com.chittortech.snp

import android.app.Application
import android.util.Log
import com.chittortech.snp.util.SnpBackgroundSyncWorker
import com.chittortech.snp.util.SnpNotificationManager

class SnpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            SnpNotificationManager.initNotificationChannels(this)
            SnpBackgroundSyncWorker.schedulePeriodicSync(this)
        } catch (e: Exception) {
            Log.e("SnpApplication", "Error during app init: ${e.message}", e)
        }
    }
}

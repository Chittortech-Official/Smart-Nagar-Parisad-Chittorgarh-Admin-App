package com.chittortech.snp

import android.app.Application
import com.chittortech.snp.util.SnpBackgroundSyncWorker
import com.chittortech.snp.util.SnpNotificationManager

class SnpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SnpNotificationManager.initNotificationChannels(this)
        SnpBackgroundSyncWorker.schedulePeriodicSync(this)
    }
}


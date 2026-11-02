package com.chittortech.snp

import android.app.Application
import com.chittortech.snp.util.SnpBackgroundSyncWorker
import com.chittortech.snp.util.SnpLiveMonitoringService
import com.chittortech.snp.util.SnpNotificationManager
import com.chittortech.snp.util.SnpTicketMonitor

class SnpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SnpNotificationManager.initNotificationChannels(this)
        SnpTicketMonitor.startMonitoring(this)
        SnpLiveMonitoringService.start(this)
        SnpBackgroundSyncWorker.schedulePeriodicSync(this)
    }
}


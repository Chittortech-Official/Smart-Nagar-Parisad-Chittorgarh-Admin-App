package com.chittortech.snp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.chittortech.snp.MainActivity
import com.chittortech.snp.R

class SnpLiveMonitoringService : Service() {

    override fun onCreate() {
        super.onCreate()
        createServiceNotificationChannel()
        startInForeground()
        SnpTicketMonitor.startMonitoring(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SnpTicketMonitor.startMonitoring(applicationContext)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startInForeground() {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            9001,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_SERVICE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("गढ़-सेवा कमांड सेंटर")
            .setContentText("24×7 लाइव शिकायत व आपातकालीन मॉनिटरिंग सक्रिय")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF0D47A1.toInt())
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                "बैकग्राउंड लाइव सेवा (Background Live Service)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "नगर परिषद चित्तौड़गढ़ 24×7 कमांड सेंटर लाइव मॉनिटरिंग"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_SERVICE_ID = "snp_live_monitoring_service"
        const val NOTIFICATION_ID = 88001

        fun start(context: Context) {
            try {
                val intent = Intent(context, SnpLiveMonitoringService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }
    }
}

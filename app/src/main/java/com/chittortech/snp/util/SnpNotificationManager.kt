package com.chittortech.snp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.chittortech.snp.MainActivity
import com.chittortech.snp.R

object SnpNotificationManager {

    const val CHANNEL_EMERGENCY_ID = "snp_emergency_alerts"
    const val CHANNEL_SLA_OVERDUE_ID = "snp_sla_overdue_alerts"
    const val CHANNEL_NEW_COMPLAINTS_ID = "snp_new_complaints_alerts"

    private const val PREFS_NAME = "snp_notification_dedup_prefs"
    private const val KEY_NOTIFIED_KEYS = "notified_keys"

    fun isAlreadyNotified(context: Context, key: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_NOTIFIED_KEYS, emptySet()) ?: emptySet()
        return set.contains(key)
    }

    fun markAsNotified(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentSet = prefs.getStringSet(KEY_NOTIFIED_KEYS, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(key)
        prefs.edit().putStringSet(KEY_NOTIFIED_KEYS, currentSet).apply()
    }

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Emergency Channel (Highest Priority, Red LED, Long Vibration)
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY_ID,
                "आपातकालीन एवं दमकल अलर्ट (Emergency & Fire)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "दैनिक आपातकालीन शिकायतें जैसे मृत पशु, आग, खुला नाला एवं तत्काल सहायता"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300, 200, 500)
            }

            // 2. SLA Overdue Channel (High Priority, Amber LED)
            val slaChannel = NotificationChannel(
                CHANNEL_SLA_OVERDUE_ID,
                "समय सीमा उल्लंघन (SLA Overdue Escalations)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "वार्ड शिकायतों की 24h/48h समाधान समय सीमा समाप्त होने पर अलर्ट"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }

            // 3. New Citizen Complaints Channel (Default Priority)
            val complaintsChannel = NotificationChannel(
                CHANNEL_NEW_COMPLAINTS_ID,
                "नई नागरिक शिकायतें (New Citizen Grievances)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "नागरिक द्वारा हेल्पलाइन अथवा ऐप से दर्ज नई शिकायतों का लाइव नोटिफिकेशन"
                enableLights(true)
                lightColor = Color.BLUE
            }

            notificationManager.createNotificationChannels(listOf(emergencyChannel, slaChannel, complaintsChannel))
        }
    }

    fun showNewComplaintAlert(
        context: Context,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        citizenName: String,
        isEmergency: Boolean = false
    ) {
        if (!hasNotificationPermission(context)) return
        if (isAlreadyNotified(context, ticketNumber)) return
        markAsNotified(context, ticketNumber)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ticketNumber.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isEmergency) CHANNEL_EMERGENCY_ID else CHANNEL_NEW_COMPLAINTS_ID
        val title = if (isEmergency) {
            "🚨 आपातकालीन शिकायत दर्ज: वार्ड $wardNumber"
        } else {
            "📝 नई नागरिक शिकायत: वार्ड $wardNumber"
        }
        val body = "$ticketNumber: $categoryTitle | नागरिक: $citizenName"

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$body\nमाननीय सभापति/उपसभापति कमांड सेंटर द्वारा त्वरित संज्ञान हेतु प्रेषित।"))
            .setPriority(if (isEmergency) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setColor(if (isEmergency) Color.RED else 0xFF0D47A1.toInt())
            .build()

        NotificationManagerCompat.from(context).notify(ticketNumber.hashCode(), notification)
    }

    fun showOverdueAlert(
        context: Context,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String
    ) {
        if (!hasNotificationPermission(context)) return
        val dedupKey = "${ticketNumber}_overdue"
        if (isAlreadyNotified(context, dedupKey)) return
        markAsNotified(context, dedupKey)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ticketNumber.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "⚠️ समय सीमा पार (SLA Overdue): वार्ड $wardNumber"
        val body = "$ticketNumber: $categoryTitle की निर्धारित समाधान समय सीमा समाप्त हो चुकी है।"

        val notification = NotificationCompat.Builder(context, CHANNEL_SLA_OVERDUE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$body\nसंबंधित कनिष्ठ अभियंता (JEN) एवं वार्ड प्रभारी को तत्काल फॉलो-अप आवश्यक है।"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFFDC2626.toInt())
            .build()

        NotificationManagerCompat.from(context).notify(ticketNumber.hashCode(), notification)
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}


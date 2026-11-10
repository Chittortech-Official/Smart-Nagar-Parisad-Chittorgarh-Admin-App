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
import android.net.Uri
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
    const val CHANNEL_STATUS_UPDATES_ID = "snp_status_updates_alerts"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Emergency Channel (Highest Priority, Red LED, Long Vibration)
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY_ID,
                "🚨 आपातकालीन अलर्ट (Emergency & Fire)",
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
                "⚠️ समय सीमा उल्लंघन (SLA Escalations)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "वार्ड शिकायतों की 24h/48h समाधान समय सीमा समाप्त होने पर अलर्ट"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }

            // 3. New Citizen Complaints Channel (High Priority)
            val complaintsChannel = NotificationChannel(
                CHANNEL_NEW_COMPLAINTS_ID,
                "📝 नई नागरिक शिकायतें (New Grievances)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "नागरिक द्वारा हेल्पलाइन अथवा ऐप से दर्ज नई शिकायतों का लाइव नोटिफिकेशन"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }

            // 4. Status & Resolution Channel (High Priority)
            val statusChannel = NotificationChannel(
                CHANNEL_STATUS_UPDATES_ID,
                "🔄 शिकायत स्थिति व समाधान (Status & Resolution)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "शिकायत प्रगति एवं सफल समाधान के लाइव नोटिफिकेशन"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
            }

            notificationManager.createNotificationChannels(listOf(emergencyChannel, slaChannel, complaintsChannel, statusChannel))
        }
    }

    private fun getStatusBadge(status: String): Pair<String, Int> {
        return when (status.uppercase().trim()) {
            "IN_PROGRESS" -> Pair("🟡 प्रक्रियाधीन (In Progress)", 0xFFD97706.toInt())
            "PENDING", "REGISTERED" -> Pair("🔵 दर्ज / लंबित (Registered)", 0xFF0D47A1.toInt())
            "ASSIGNED" -> Pair("🟣 वार्ड टीम को आवंटित (Assigned)", 0xFF7C3AED.toInt())
            "RESOLVED", "RESOLVED_AWAITING_CITIZEN" -> Pair("🟢 समाधान सत्यापित (Resolved)", 0xFF16A34A.toInt())
            "CLOSED" -> Pair("✅ सफल निस्तारण पूर्ण (Closed)", 0xFF16A34A.toInt())
            "REJECTED" -> Pair("🔴 निरस्त (Rejected)", 0xFFDC2626.toInt())
            "OVERDUE" -> Pair("⚠️ समय सीमा पार (Overdue)", 0xFFDC2626.toInt())
            else -> Pair("⚪ ${status.replace("_", " ")}", 0xFF475569.toInt())
        }
    }

    private fun buildCallAction(context: Context, ticketId: String, phone: String?): NotificationCompat.Action? {
        val cleanPhone = phone?.trim().orEmpty()
        if (cleanPhone.isBlank()) return null
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanPhone")
        }
        val callPendingIntent = PendingIntent.getActivity(
            context,
            (ticketId + "_dial").hashCode(),
            callIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_call,
            "📞 कॉल करें ($cleanPhone)",
            callPendingIntent
        ).build()
    }

    private fun buildViewAction(context: Context, ticketId: String, ticketNumber: String, label: String = "👁️ विवरण देखें"): NotificationCompat.Action {
        val viewIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val viewPendingIntent = PendingIntent.getActivity(
            context,
            (ticketId + "_view").hashCode(),
            viewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_info_details,
            label,
            viewPendingIntent
        ).build()
    }

    fun showNewComplaintAlert(
        context: Context,
        ticketId: String,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        citizenName: String,
        citizenPhone: String = "",
        isEmergency: Boolean = false
    ) {
        if (!hasNotificationPermission(context)) return

        val notifId = (ticketId + "_new").hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isEmergency) CHANNEL_EMERGENCY_ID else CHANNEL_NEW_COMPLAINTS_ID
        val title = if (isEmergency) {
            "🚨 [वार्ड $wardNumber] आपातकालीन शिकायत दर्ज!"
        } else {
            "📝 [वार्ड $wardNumber] नई नागरिक शिकायत दर्ज"
        }

        val citizenDisplayName = if (citizenName.isNotBlank()) citizenName.trim() else "नागरिक"
        val cleanPhone = citizenPhone.trim()

        val collapsedText = if (cleanPhone.isNotBlank()) {
            "👤 $citizenDisplayName  •  📞 $cleanPhone"
        } else {
            "👤 $citizenDisplayName  •  वार्ड $wardNumber"
        }

        val expandedCardText = buildString {
            append("📋 विषय: ").append(categoryTitle).append("\n")
            append("👤 नागरिक: ").append(citizenDisplayName).append("\n")
            append("📞 संपर्क: ").append(if (cleanPhone.isNotBlank()) cleanPhone else "उपलब्ध नहीं").append("\n")
            append("📍 स्थान: वार्ड नं. ").append(wardNumber).append(" • नगर परिषद चित्तौड़गढ़\n")
            append("⚡ स्थिति: 🔵 दर्ज (REGISTERED) | त्वरित संज्ञान हेतु प्रेषित")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("गढ़-सेवा • $ticketNumber")
            .setContentTitle(title)
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedCardText))
            .setPriority(if (isEmergency) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(if (isEmergency) longArrayOf(0, 300, 200, 300, 200, 500) else longArrayOf(0, 250, 150, 250))
            .setContentIntent(pendingIntent)
            .setColor(if (isEmergency) Color.RED else 0xFFE65100.toInt())

        buildCallAction(context, ticketId, cleanPhone)?.let { builder.addAction(it) }
        builder.addAction(buildViewAction(context, ticketId, ticketNumber, "👁️ शिकायत देखें"))

        NotificationManagerCompat.from(context).notify(notifId, builder.build())
    }

    fun showStatusChangeAlert(
        context: Context,
        ticketId: String,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        newStatus: String,
        citizenName: String = "",
        citizenPhone: String = ""
    ) {
        if (!hasNotificationPermission(context)) return

        val notifId = (ticketId + "_status").hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (badgeLabel, badgeColor) = getStatusBadge(newStatus)
        val citizenDisplayName = if (citizenName.isNotBlank()) citizenName.trim() else "नागरिक"
        val cleanPhone = citizenPhone.trim()

        val title = "🔄 [वार्ड $wardNumber] शिकायत स्थिति अपडेट"
        val collapsedText = if (cleanPhone.isNotBlank()) {
            "$badgeLabel • 👤 $citizenDisplayName"
        } else {
            badgeLabel
        }

        val expandedCardText = buildString {
            append("⚡ अद्यतन स्थिति: ").append(badgeLabel).append("\n")
            append("📋 विषय: ").append(categoryTitle).append("\n")
            append("👤 नागरिक: ").append(citizenDisplayName).append("\n")
            append("📞 संपर्क: ").append(if (cleanPhone.isNotBlank()) cleanPhone else "उपलब्ध नहीं").append("\n")
            append("📍 स्थान: वार्ड नं. ").append(wardNumber).append(" • कमांड सेंटर द्वारा लाइव ट्रैक")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_STATUS_UPDATES_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("गढ़-सेवा • $ticketNumber")
            .setContentTitle(title)
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedCardText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .setContentIntent(pendingIntent)
            .setColor(badgeColor)

        buildCallAction(context, ticketId, cleanPhone)?.let { builder.addAction(it) }
        builder.addAction(buildViewAction(context, ticketId, ticketNumber, "👁️ प्रगति देखें"))

        NotificationManagerCompat.from(context).notify(notifId, builder.build())
    }

    fun showResolvedAlert(
        context: Context,
        ticketId: String,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        citizenName: String = "",
        citizenPhone: String = ""
    ) {
        if (!hasNotificationPermission(context)) return

        val notifId = (ticketId + "_resolved").hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val citizenDisplayName = if (citizenName.isNotBlank()) citizenName.trim() else "नागरिक"
        val cleanPhone = citizenPhone.trim()

        val title = "✅ [वार्ड $wardNumber] शिकायत समाधान पूर्ण!"
        val collapsedText = if (cleanPhone.isNotBlank()) {
            "🎉 समाधान पूर्ण • 👤 $citizenDisplayName ($cleanPhone)"
        } else {
            "🎉 समाधान पूर्ण • 👤 $citizenDisplayName"
        }

        val expandedCardText = buildString {
            append("🎉 समाधान सत्यापित: कार्य गुणवत्तापूर्वक निस्तारित\n")
            append("📋 विषय: ").append(categoryTitle).append("\n")
            append("👤 नागरिक: ").append(citizenDisplayName).append("\n")
            append("📞 संपर्क: ").append(if (cleanPhone.isNotBlank()) cleanPhone else "उपलब्ध नहीं").append("\n")
            append("📍 स्थान: वार्ड नं. ").append(wardNumber).append(" • नागरिक संतुष्टि सत्यापित")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_STATUS_UPDATES_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("गढ़-सेवा • $ticketNumber")
            .setContentTitle(title)
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedCardText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .setContentIntent(pendingIntent)
            .setColor(0xFF16A34A.toInt()) // Vibrant Green

        buildCallAction(context, ticketId, cleanPhone)?.let { builder.addAction(it) }
        builder.addAction(buildViewAction(context, ticketId, ticketNumber, "👁️ निस्तारण देखें"))

        NotificationManagerCompat.from(context).notify(notifId, builder.build())
    }

    fun showRejectedAlert(
        context: Context,
        ticketId: String,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        citizenName: String = "",
        citizenPhone: String = ""
    ) {
        if (!hasNotificationPermission(context)) return

        val notifId = (ticketId + "_rejected").hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val citizenDisplayName = if (citizenName.isNotBlank()) citizenName.trim() else "नागरिक"
        val cleanPhone = citizenPhone.trim()

        val title = "🔴 [वार्ड $wardNumber] शिकायत निरस्त (Rejected)!"
        val collapsedText = if (cleanPhone.isNotBlank()) {
            "🔴 निरस्त (Rejected) • 👤 $citizenDisplayName ($cleanPhone)"
        } else {
            "🔴 शिकायत निरस्त • 👤 $citizenDisplayName"
        }

        val expandedCardText = buildString {
            append("⚡ अद्यतन स्थिति: 🔴 निरस्त (REJECTED)\n")
            append("📋 विषय: ").append(categoryTitle).append("\n")
            append("👤 नागरिक: ").append(citizenDisplayName).append("\n")
            append("📞 संपर्क: ").append(if (cleanPhone.isNotBlank()) cleanPhone else "उपलब्ध नहीं").append("\n")
            append("📍 स्थान: वार्ड नं. ").append(wardNumber).append(" • नगर परिषद चित्तौड़गढ़\n")
            append("ℹ️ स्थिति: नियमानुसार जांच उपरांत निरस्त")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_STATUS_UPDATES_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("गढ़-सेवा • $ticketNumber")
            .setContentTitle(title)
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedCardText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setContentIntent(pendingIntent)
            .setColor(0xFFDC2626.toInt()) // Red

        buildCallAction(context, ticketId, cleanPhone)?.let { builder.addAction(it) }
        builder.addAction(buildViewAction(context, ticketId, ticketNumber, "👁️ कारण देखें"))

        NotificationManagerCompat.from(context).notify(notifId, builder.build())
    }

    fun showOverdueAlert(
        context: Context,
        ticketId: String,
        ticketNumber: String,
        wardNumber: Int,
        categoryTitle: String,
        citizenName: String = "",
        citizenPhone: String = ""
    ) {
        if (!hasNotificationPermission(context)) return

        val notifId = (ticketId + "_overdue").hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TICKET_NUMBER", ticketNumber)
            putExtra("EXTRA_TICKET_ID", ticketId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val citizenDisplayName = if (citizenName.isNotBlank()) citizenName.trim() else "नागरिक"
        val cleanPhone = citizenPhone.trim()

        val title = "⚠️ [वार्ड $wardNumber] समय सीमा पार (SLA Overdue)!"
        val collapsedText = if (cleanPhone.isNotBlank()) {
            "⏳ समाधान सीमा समाप्त • 👤 $citizenDisplayName"
        } else {
            "⏳ निर्धारित समाधान समय सीमा समाप्त"
        }

        val expandedCardText = buildString {
            append("⏳ 24h/48h समाधान समय सीमा समाप्त हो चुकी है!\n")
            append("📋 विषय: ").append(categoryTitle).append("\n")
            append("👤 नागरिक: ").append(citizenDisplayName).append("\n")
            append("📞 संपर्क: ").append(if (cleanPhone.isNotBlank()) cleanPhone else "उपलब्ध नहीं").append("\n")
            append("📍 स्थान: वार्ड नं. ").append(wardNumber).append(" • संबंधित JEN को तत्काल कार्रवाई अपेक्षित")
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_SLA_OVERDUE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSubText("गढ़-सेवा • $ticketNumber")
            .setContentTitle(title)
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedCardText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .setContentIntent(pendingIntent)
            .setColor(0xFFDC2626.toInt()) // Alert Red

        buildCallAction(context, ticketId, cleanPhone)?.let { builder.addAction(it) }
        builder.addAction(buildViewAction(context, ticketId, ticketNumber, "🚨 त्वरित संज्ञान लें"))

        NotificationManagerCompat.from(context).notify(notifId, builder.build())
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

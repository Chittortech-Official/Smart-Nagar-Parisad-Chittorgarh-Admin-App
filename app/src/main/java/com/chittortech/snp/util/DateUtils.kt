package com.chittortech.snp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")
    private val utcTimeZone = TimeZone.getTimeZone("UTC")

    private val displayFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).apply {
        timeZone = istTimeZone
    }

    private val shortDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).apply {
        timeZone = istTimeZone
    }

    fun parseIsoDate(isoString: String?): Date? {
        if (isoString.isNullOrBlank()) return null
        val clean = isoString.replace("Z", "")
        val withoutTz = if (clean.contains("+")) clean.substringBefore("+") else clean
        val patterns = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                    timeZone = utcTimeZone
                }
                val parsed = parser.parse(withoutTz)
                if (parsed != null) return parsed
            } catch (_: Exception) {}
        }
        return null
    }

    fun formatIsoToDisplay(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        val date = parseIsoDate(isoString) ?: return isoString
        return displayFormat.format(date)
    }

    fun formatToShortDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        val date = parseIsoDate(dateString) ?: return ""
        return shortDateFormat.format(date)
    }

    fun isToday(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val itemDate = parseIsoDate(dateString) ?: return false
        val todayCal = Calendar.getInstance(istTimeZone)
        val itemCal = Calendar.getInstance(istTimeZone).apply { time = itemDate }
        return todayCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                todayCal.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val itemDate = parseIsoDate(dateString) ?: return false
        val yestCal = Calendar.getInstance(istTimeZone).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val itemCal = Calendar.getInstance(istTimeZone).apply { time = itemDate }
        return yestCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                yestCal.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)
    }

    fun isLastNDays(dateString: String?, days: Int): Boolean {
        if (dateString.isNullOrBlank()) return false
        val itemDate = parseIsoDate(dateString) ?: return false
        val limitCal = Calendar.getInstance(istTimeZone).apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return itemDate.after(limitCal.time)
    }

    fun isThisMonth(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val itemDate = parseIsoDate(dateString) ?: return false
        val currentCal = Calendar.getInstance(istTimeZone)
        val itemCal = Calendar.getInstance(istTimeZone).apply { time = itemDate }
        return currentCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                currentCal.get(Calendar.MONTH) == itemCal.get(Calendar.MONTH)
    }

    fun isThisYear(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val itemDate = parseIsoDate(dateString) ?: return false
        val currentCal = Calendar.getInstance(istTimeZone)
        val itemCal = Calendar.getInstance(istTimeZone).apply { time = itemDate }
        return currentCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR)
    }

    fun calculateHoursRemaining(deadlineIso: String?): Long {
        if (deadlineIso.isNullOrBlank()) return 0L
        val date = parseIsoDate(deadlineIso) ?: return 0L
        val diff = date.time - System.currentTimeMillis()
        return diff / (1000 * 60 * 60)
    }
}

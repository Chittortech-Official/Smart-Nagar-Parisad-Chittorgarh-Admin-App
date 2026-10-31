package com.chittortech.snp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).apply {
        timeZone = istTimeZone
    }

    private val displayFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).apply {
        timeZone = istTimeZone
    }

    private val shortDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).apply {
        timeZone = istTimeZone
    }

    fun formatIsoToDisplay(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        return try {
            val clean = isoString.replace("Z", "").substringBefore("+")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(clean)
            if (date != null) displayFormat.format(date) else isoString
        } catch (e: Exception) {
            isoString
        }
    }

    fun isToday(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val todayStr = shortDateFormat.format(Date())
        val itemStr = formatToShortDate(dateString)
        return todayStr == itemStr
    }

    fun isThisMonth(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val monthFormat = SimpleDateFormat("yyyy-MM", Locale.ENGLISH).apply { timeZone = istTimeZone }
        val currentMonth = monthFormat.format(Date())
        return try {
            val clean = dateString.replace("Z", "").substringBefore("+")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(clean)
            date != null && monthFormat.format(date) == currentMonth
        } catch (e: Exception) {
            false
        }
    }

    fun isThisYear(dateString: String?): Boolean {
        if (dateString.isNullOrBlank()) return false
        val yearFormat = SimpleDateFormat("yyyy", Locale.ENGLISH).apply { timeZone = istTimeZone }
        val currentYear = yearFormat.format(Date())
        return try {
            val clean = dateString.replace("Z", "").substringBefore("+")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(clean)
            date != null && yearFormat.format(date) == currentYear
        } catch (e: Exception) {
            false
        }
    }

    fun formatToShortDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        return try {
            val clean = dateString.replace("Z", "").substringBefore("+")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(clean)
            if (date != null) shortDateFormat.format(date) else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun calculateHoursRemaining(deadlineIso: String?): Long {
        if (deadlineIso.isNullOrBlank()) return 0L
        return try {
            val clean = deadlineIso.replace("Z", "").substringBefore("+")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(clean)
            if (date != null) {
                val diff = date.time - System.currentTimeMillis()
                diff / (1000 * 60 * 60)
            } else 0L
        } catch (e: Exception) {
            0L
        }
    }
}


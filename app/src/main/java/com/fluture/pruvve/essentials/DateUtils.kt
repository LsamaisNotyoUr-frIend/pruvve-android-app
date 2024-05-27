package com.fluture.pruvve.essentials

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    fun getRelativeTimeString(creationDateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val creationDate: Date? = dateFormat.parse(creationDateString)
        val currentDate = Date()

        if (creationDate == null) {
            return "Invalid date"
        }

        val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentDate.time - creationDate.time)
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = TimeUnit.DAYS.convert(currentDate.time - creationDate.time, TimeUnit.MILLISECONDS)

        return when {
            diffInMinutes < 1 -> "Just now"
            diffInMinutes < 2 -> "1 minute ago"
            diffInMinutes < 60 -> "$diffInMinutes minutes ago"
            diffInHours < 24 -> "$diffInHours hours ago"
            diffInDays == 1L -> "1 day ago"
            diffInDays < 30 -> "$diffInDays days ago"
            else -> creationDateString
        }
    }

    private fun abs(value: Long): Long {
        return if (value < 0) -value else value
    }
}

package com.solostackdev.volumelift.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    fun getWeekStartEnd(weekOffset: Int = 0): Pair<Long, Long> {
        val now = LocalDate.now().plusWeeks(weekOffset.toLong())
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)
        val startMillis = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = sunday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return Pair(startMillis, endMillis)
    }

    fun formatDuration(startTime: Long, endTime: Long?): String {
        val end = endTime ?: System.currentTimeMillis()
        val durationMs = end - startTime
        val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

    fun formatWeekRange(weekOffset: Int = 0): String {
        val (start, end) = getWeekStartEnd(weekOffset)
        val startDate = Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).toLocalDate()
        val endDate = Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).toLocalDate()
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        return "${sdf.format(start)} - ${sdf.format(end)}"
    }

    fun todayStartMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /** "SATURDAY, MAR 14" overline format */
    fun formatDateOverline(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        return sdf.format(timestamp).uppercase(Locale.getDefault())
    }

    /** Friendly duration like "58 min" or "1h 12m" */
    fun formatDurationShort(startTime: Long, endTime: Long?): String {
        val end = endTime ?: System.currentTimeMillis()
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(end - startTime).toInt()
        return if (totalMinutes >= 60) {
            "${totalMinutes / 60}h ${totalMinutes % 60}m"
        } else {
            "$totalMinutes min"
        }
    }

    /** Relative date like "Yesterday", "2 days ago", "Today" */
    fun formatRelativeDate(timestamp: Long): String {
        val today = LocalDate.now()
        val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
        val days = java.time.temporal.ChronoUnit.DAYS.between(date, today).toInt()
        return when (days) {
            0 -> "Today"
            1 -> "Yesterday"
            else -> "$days days ago"
        }
    }

    /** "FRIDAY, MAR 14" date header */
    fun formatDateHeader(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        return sdf.format(timestamp).uppercase(Locale.getDefault())
    }

    /** Day of week short (M, T, W, T, F, S, S) */
    fun getDayOfWeekShort(date: LocalDate): String {
        return when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "M"
            DayOfWeek.TUESDAY -> "T"
            DayOfWeek.WEDNESDAY -> "W"
            DayOfWeek.THURSDAY -> "T"
            DayOfWeek.FRIDAY -> "F"
            DayOfWeek.SATURDAY -> "S"
            DayOfWeek.SUNDAY -> "S"
        }
    }

    /** Get all 7 days of the current week as LocalDate list */
    fun getWeekDays(weekOffset: Int = 0): List<LocalDate> {
        val now = LocalDate.now().plusWeeks(weekOffset.toLong())
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0L..6L).map { monday.plusDays(it) }
    }
}

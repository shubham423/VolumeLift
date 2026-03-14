package com.example.volumelift.util

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
}

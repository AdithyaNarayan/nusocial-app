package com.teamnusocial.nusocial.utils

import android.content.Context

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS

fun getTimeAgo(timeInput: Long): String {
    var time = timeInput

    if (time < 1000000000000L) {
        time *= 1000
    }
    val now: Long = System.currentTimeMillis()

    if (time > now || time <= 0) {
        return ""
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> {
            "just now"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "a minute ago"
        }
        diff < 50 * MINUTE_MILLIS -> {
            (diff / MINUTE_MILLIS).toString() + " minutes ago"
        }
        diff < 90 * MINUTE_MILLIS -> {
            "an hour ago"
        }
        diff < 24 * HOUR_MILLIS -> {
            (diff / HOUR_MILLIS).toString() + " hours ago"
        }
        diff < 48 * HOUR_MILLIS -> {
            "yesterday"
        }
        else -> {
            (diff / DAY_MILLIS).toString() + " days ago"
        }
    }
}
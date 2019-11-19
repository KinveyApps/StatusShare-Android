package com.kinvey.sample.statusshare.utils

import java.util.*

object TimeUtil {

    private const val NOW_STR = "now"
    private const val MINUTE_AGO_STR = "m ago"
    private const val HOUR_AGO_STR = "h ago"
    private const val DAY_AGO_STR = "d ago"

    fun getSince(d: Date, c: Calendar): String {
        val secsSince = (c.time.time - d.time) / 1000L
        return when {
            secsSince < 60 -> NOW_STR
            secsSince < 60 * 60 -> "${secsSince / 60}$MINUTE_AGO_STR"
            secsSince < 60 * 60 * 24 -> "${secsSince / (60 * 60)}$HOUR_AGO_STR"
            else -> "${secsSince / (60 * 60 * 24)}$DAY_AGO_STR"
        }
    }
}
package com.rgcastrof.trustcam.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FormatterUtils {
    private val photoDateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM dd, yyyy\nHH:mm:ss")
        .withZone(ZoneId.systemDefault())

    fun formatTimestamp(timestamp: Long): String {
        return photoDateTimeFormatter.format(Instant.ofEpochMilli(timestamp))
    }
}
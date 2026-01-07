package com.pegai.app.data.data.utils

import java.util.concurrent.TimeUnit

fun formatarTempo(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return ""

    val diff = System.currentTimeMillis() - timestamp.toDate().time

    val dias = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        dias < 1 -> "Hoje"
        dias < 7 -> "$dias dias atrás"
        dias < 30 -> "${dias / 7} semanas atrás"
        dias < 365 -> "${dias / 30} meses atrás"
        else -> "${dias / 365} anos atrás"
    }
}


package com.example.bookkeeper.ui.theme.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReadingProgressBar(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true
) {
    val isDark = isSystemInDarkTheme()
    val barColor = if (isDark) Color(0xFFDDA15E) else Color(0xFFBC6C25)

    val progressFraction = remember(currentPage, totalPages) {
        if (totalPages > 0) {
            (currentPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    val percentageText = remember(progressFraction) {
        "${(progressFraction * 100).toInt()}%"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (showLabels) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Progresso",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = percentageText,
                    style = MaterialTheme.typography.titleLarge,
                    color = barColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        if (showLabels) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$currentPage de $totalPages páginas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
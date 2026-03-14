package com.example.volumelift.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.presentation.theme.VolumeOnTarget
import com.example.volumelift.presentation.theme.VolumeOver
import com.example.volumelift.presentation.theme.VolumeUnder

@Composable
fun VolumeProgressBar(
    muscleVolume: MuscleVolume,
    modifier: Modifier = Modifier,
    useKg: Boolean = true
) {
    // Sets-based progress (primary metric)
    val setsProgress = (muscleVolume.setsProgressPercent / 100f).coerceAtMost(1f)
    val setsColor = when {
        muscleVolume.setsProgressPercent >= 110f -> VolumeOver
        muscleVolume.setsProgressPercent >= 85f -> VolumeOnTarget
        else -> VolumeUnder
    }

    val setsChangeText = if (muscleVolume.previousWeekSets > 0) {
        val change = muscleVolume.setsWeekOverWeekChange
        if (change >= 0) "↑${String.format("%.0f", change)}%" else "↓${String.format("%.0f", -change)}%"
    } else ""

    val unit = if (useKg) "kg" else "lbs"

    Column(modifier = modifier.padding(vertical = 6.dp)) {
        // Muscle name + sets count (primary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = muscleVolume.muscleGroup.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${muscleVolume.currentSets} / ${muscleVolume.targetSets} sets",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = setsColor
            )
            if (setsChangeText.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = setsChangeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (muscleVolume.setsWeekOverWeekChange >= 0) VolumeOnTarget else VolumeOver
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sets progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(setsProgress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(setsColor)
                    .animateContentSize()
            )
        }

        // Volume (secondary metric, smaller text)
        if (muscleVolume.currentVolume > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${String.format("%.0f", muscleVolume.currentVolume)} $unit volume",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

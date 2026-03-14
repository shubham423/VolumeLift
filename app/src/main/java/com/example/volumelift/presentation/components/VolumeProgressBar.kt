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
    val progress = (muscleVolume.progressPercent / 100f).coerceIn(0f, 1.5f)
    val displayProgress = (progress).coerceAtMost(1f)
    val color = when {
        muscleVolume.progressPercent >= 110f -> VolumeOver
        muscleVolume.progressPercent >= 85f -> VolumeOnTarget
        else -> VolumeUnder
    }
    val unit = if (useKg) "kg" else "lbs"
    val changeText = if (muscleVolume.previousWeekVolume > 0) {
        val change = muscleVolume.weekOverWeekChange
        if (change >= 0) "↑${String.format("%.0f", change)}%" else "↓${String.format("%.0f", -change)}%"
    } else ""

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = muscleVolume.muscleGroup.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${String.format("%.0f", muscleVolume.currentVolume)} / ${String.format("%.0f", muscleVolume.targetVolume)} $unit",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (changeText.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = changeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (muscleVolume.weekOverWeekChange >= 0) VolumeOnTarget else VolumeOver
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(displayProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .animateContentSize()
            )
        }
    }
}

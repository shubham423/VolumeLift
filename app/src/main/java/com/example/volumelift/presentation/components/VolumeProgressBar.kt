package com.example.volumelift.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.presentation.theme.GradientEnd
import com.example.volumelift.presentation.theme.GradientStart
import com.example.volumelift.presentation.theme.VolumeOnTarget
import com.example.volumelift.presentation.theme.VolumeOver
import com.example.volumelift.presentation.theme.VolumeUnder

@Composable
fun VolumeProgressBar(
    muscleVolume: MuscleVolume,
    modifier: Modifier = Modifier,
    useKg: Boolean = true
) {
    val setsProgress = (muscleVolume.setsProgressPercent / 100f).coerceAtMost(1f)
    val statusColor = when {
        muscleVolume.setsProgressPercent >= 110f -> VolumeOver
        muscleVolume.setsProgressPercent >= 85f -> VolumeOnTarget
        else -> VolumeUnder
    }

    val progressBrush = when {
        muscleVolume.setsProgressPercent >= 110f -> Brush.horizontalGradient(
            listOf(VolumeOver.copy(alpha = 0.8f), VolumeOver)
        )
        muscleVolume.setsProgressPercent >= 85f -> Brush.horizontalGradient(
            listOf(GradientEnd, VolumeOnTarget)
        )
        else -> Brush.horizontalGradient(
            listOf(VolumeUnder.copy(alpha = 0.7f), VolumeUnder)
        )
    }

    val setsChangeText = if (muscleVolume.previousWeekSets > 0) {
        val change = muscleVolume.setsWeekOverWeekChange
        if (change >= 0) "+${String.format("%.0f", change)}%" else "${String.format("%.0f", change)}%"
    } else ""

    val unit = if (useKg) "kg" else "lbs"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = muscleVolume.muscleGroup.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${muscleVolume.currentSets}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    text = " / ${muscleVolume.targetSets}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (setsChangeText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = setsChangeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (muscleVolume.setsWeekOverWeekChange >= 0) VolumeOnTarget else VolumeOver
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                    .fillMaxWidth(setsProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressBrush)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            )
        }

        if (muscleVolume.currentVolume > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${String.format("%.0f", muscleVolume.currentVolume)} $unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

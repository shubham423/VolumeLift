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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.presentation.theme.OnTarget
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.Primary
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.presentation.theme.UnderTarget

@Composable
fun VolumeProgressBar(
    muscleVolume: MuscleVolume,
    modifier: Modifier = Modifier,
    useKg: Boolean = true
) {
    val volumeProgress = muscleVolume.volumeProgressPercent
    val barFraction = (volumeProgress / 100f).coerceAtMost(1f)
    val statusColor = when {
        volumeProgress >= 110f -> OverTarget
        volumeProgress >= 70f -> OnTarget
        else -> UnderTarget
    }

    val progressBrush = when {
        volumeProgress >= 110f -> Brush.horizontalGradient(
            listOf(Color(0xFFA33030), OverTarget)
        )
        volumeProgress >= 70f -> Brush.horizontalGradient(
            listOf(Primary, OnTarget)
        )
        else -> Brush.horizontalGradient(
            listOf(Color(0xFF8A6520), UnderTarget)
        )
    }

    val unit = if (useKg) "kg" else "lbs"

    // Week-over-week change
    val changeText = if (muscleVolume.previousWeekVolume > 0) {
        val change = muscleVolume.volumeWeekOverWeekChange
        val arrow = if (change >= 0) "\u2191" else "\u2193"
        "$arrow ${String.format("%.0f", kotlin.math.abs(change))}%"
    } else ""

    val changeColor = if (muscleVolume.previousWeekVolume > 0) {
        if (muscleVolume.volumeWeekOverWeekChange >= 0) statusColor else OverTarget
    } else TextSecondary

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Row 1: Muscle name ... Volume value + change arrow
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = muscleVolume.muscleGroup.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val volText = if (muscleVolume.currentVolume >= 1000) {
                    String.format("%,.0f", muscleVolume.currentVolume)
                } else {
                    String.format("%.0f", muscleVolume.currentVolume)
                }
                Text(
                    "$volText $unit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = statusColor
                )
                if (changeText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        changeText,
                        fontSize = 10.sp,
                        color = changeColor
                    )
                }
            }
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barFraction)
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

        // Row 3: Target ... Percentage
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val targetText = if (muscleVolume.targetVolume >= 1000) {
                "Target: ${String.format("%,.0f", muscleVolume.targetVolume)} $unit"
            } else {
                "Target: ${String.format("%.0f", muscleVolume.targetVolume)} $unit"
            }
            Text(targetText, fontSize = 9.sp, color = TextTertiary)

            val percentText = if (volumeProgress > 100f) {
                "${String.format("%.0f", volumeProgress)}%"
            } else {
                "${String.format("%.0f", volumeProgress)}%"
            }
            val percentColor = if (volumeProgress > 110f) OverTarget else TextTertiary
            Text(percentText, fontSize = 9.sp, color = percentColor)
        }
    }
}

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
    val setsProgress = (muscleVolume.setsProgressPercent / 100f).coerceAtMost(1f)
    val statusColor = when {
        muscleVolume.setsProgressPercent >= 110f -> OverTarget
        muscleVolume.setsProgressPercent >= 85f -> OnTarget
        else -> UnderTarget
    }

    // Gradient per design tokens
    val progressBrush = when {
        muscleVolume.setsProgressPercent >= 110f -> Brush.horizontalGradient(
            listOf(Color(0xFFA33030), OverTarget)
        )
        muscleVolume.setsProgressPercent >= 85f -> Brush.horizontalGradient(
            listOf(Primary, OnTarget)
        )
        else -> Brush.horizontalGradient(
            listOf(Color(0xFF8A6520), UnderTarget)
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
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Row 1: Muscle name ... Volume value + arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = muscleVolume.muscleGroup.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${muscleVolume.currentSets}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = statusColor
                )
                Text(
                    text = " / ${muscleVolume.targetSets}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (setsChangeText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = setsChangeText,
                        fontSize = 10.sp,
                        color = statusColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: Progress bar — 8dp height per design tokens
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

        // Row 3: Target label ... Percentage
        if (muscleVolume.currentVolume > 0 || muscleVolume.targetSets > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${String.format("%.0f", muscleVolume.currentVolume)} $unit",
                    fontSize = 9.sp,
                    color = TextTertiary
                )
                Text(
                    text = "${String.format("%.0f", muscleVolume.setsProgressPercent)}%",
                    fontSize = 9.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

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
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.presentation.theme.OnTarget
import com.example.volumelift.presentation.theme.OverTarget
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
    val setsProgress = muscleVolume.setsProgressPercent
    val barFraction = (setsProgress / 100f).coerceAtMost(1f)
    val statusColor = when {
        setsProgress >= 110f -> OverTarget
        setsProgress >= 70f -> OnTarget
        else -> UnderTarget
    }

    // Muscle-group-based bar colors
    val muscleColor = getMuscleBarColor(muscleVolume.muscleGroup)
    val muscleDarkColor = muscleColor.copy(alpha = 0.5f)
    val progressBrush = if (setsProgress >= 110f) {
        Brush.horizontalGradient(listOf(Color(0xFFA33030), OverTarget))
    } else {
        Brush.horizontalGradient(listOf(muscleDarkColor, muscleColor))
    }

    val unit = if (useKg) "kg" else "lbs"

    // Week-over-week change (sets-based)
    val changeText = if (muscleVolume.previousWeekSets > 0) {
        val change = muscleVolume.setsWeekOverWeekChange
        val arrow = if (change >= 0) "\u2191" else "\u2193"
        "$arrow ${String.format("%.0f", kotlin.math.abs(change))}%"
    } else ""

    val changeColor = if (muscleVolume.previousWeekSets > 0) {
        if (muscleVolume.setsWeekOverWeekChange >= 0) statusColor else OverTarget
    } else TextSecondary

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Row 1: Muscle name ... Sets count + change arrow
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
                Text(
                    "${muscleVolume.currentSets} sets",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = statusColor
                )
                // Volume as secondary info
                if (muscleVolume.currentVolume > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    val volText = if (muscleVolume.currentVolume >= 1000) {
                        String.format("%,.0f", muscleVolume.currentVolume)
                    } else {
                        String.format("%.0f", muscleVolume.currentVolume)
                    }
                    Text(
                        "$volText $unit",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
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

        // Row 3: Target sets ... Percentage
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Target: ${muscleVolume.targetSets} sets", fontSize = 9.sp, color = TextTertiary)

            val percentText = "${String.format("%.0f", setsProgress)}%"
            val percentColor = if (setsProgress > 110f) OverTarget else TextTertiary
            Text(percentText, fontSize = 9.sp, color = percentColor)
        }
    }
}

private fun getMuscleBarColor(muscleGroup: MuscleGroup): Color {
    return when (muscleGroup) {
        MuscleGroup.Chest -> Color(0xFFE8A94F)       // Amber
        MuscleGroup.Back -> Color(0xFF6FD4AF)         // Teal
        MuscleGroup.Shoulders -> Color(0xFFD4A06F)    // Warm tan
        MuscleGroup.Biceps -> Color(0xFF6FB8D4)       // Sky blue
        MuscleGroup.Triceps -> Color(0xFFE8C84F)      // Gold
        MuscleGroup.Quads -> Color(0xFFC490D4)        // Purple
        MuscleGroup.Hamstrings -> Color(0xFFD47090)   // Rose
        MuscleGroup.Glutes -> Color(0xFFB490D4)       // Lavender
        MuscleGroup.Calves -> Color(0xFFD490B4)       // Pink
        MuscleGroup.Abs -> Color(0xFF90B4D4)          // Steel blue
        MuscleGroup.Forearms -> Color(0xFF90D4C4)     // Mint
        MuscleGroup.Traps -> Color(0xFFB4D490)        // Lime
        MuscleGroup.Lats -> Color(0xFF4FC4A0)         // Emerald
    }
}

package com.example.volumelift.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.Border
import com.example.volumelift.presentation.theme.MuscleBackBg
import com.example.volumelift.presentation.theme.MuscleBackColor
import com.example.volumelift.presentation.theme.MuscleChestBg
import com.example.volumelift.presentation.theme.MuscleChestColor
import com.example.volumelift.presentation.theme.MuscleLegsColor
import com.example.volumelift.presentation.theme.MuscleLegsBg
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.SurfaceVariant
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.util.DateUtils
import java.time.LocalDate

@Composable
fun HistoryScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWorkout: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(containerColor = Background) { paddingValues ->
        when (val state = uiState) {
            is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryLight)
                }
            }
            is HistoryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = OverTarget)
                }
            }
            is HistoryUiState.Success -> {
                if (state.groupedWorkouts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.History,
                        title = "No Workouts Yet",
                        subtitle = "Complete your first workout to see it here!",
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Title
                        item {
                            Text(
                                "History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W500,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }

                        // Week summary card
                        item {
                            WeekSummaryCard(
                                workoutCount = state.weekWorkoutCount,
                                totalDuration = state.weekTotalDuration,
                                totalVolume = state.weekTotalVolume,
                                weekDays = state.weekDays,
                                workoutDayIndices = state.workoutDayIndices,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        // Date-grouped workout entries
                        state.groupedWorkouts.forEach { (dateHeader, items) ->
                            item {
                                Text(
                                    dateHeader,
                                    fontSize = 11.sp,
                                    color = TextTertiary,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 12.dp,
                                        bottom = 6.dp
                                    )
                                )
                            }
                            items(items, key = { it.session.id }) { item ->
                                HistoryWorkoutCard(
                                    item = item,
                                    onClick = { onNavigateToDetail(item.session.id) },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekSummaryCard(
    workoutCount: Int,
    totalDuration: String,
    totalVolume: Double,
    weekDays: List<LocalDate>,
    workoutDayIndices: Set<Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "THIS WEEK",
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // 3 stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$workoutCount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = TextPrimary
                    )
                    Text("workouts", fontSize = 10.sp, color = TextTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        totalDuration.ifEmpty { "0 min" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = PrimaryLight
                    )
                    Text("total time", fontSize = 10.sp, color = TextTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val volText = if (totalVolume >= 1000) {
                        String.format("%.1fk", totalVolume / 1000)
                    } else {
                        String.format("%.0f", totalVolume)
                    }
                    Text(
                        volText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = TextPrimary
                    )
                    Text("kg lifted", fontSize = 10.sp, color = TextTertiary)
                }
            }

            // Day dots
            if (weekDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val today = LocalDate.now()
                    weekDays.forEachIndexed { index, date ->
                        val hasWorkout = workoutDayIndices.contains(index)
                        val isToday = date == today
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (hasWorkout) PrimaryLight
                                        else SurfaceVariant
                                    )
                                    .then(
                                        if (isToday && !hasWorkout)
                                            Modifier.border(0.5.dp, Border, CircleShape)
                                        else Modifier
                                    )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                DateUtils.getDayOfWeekShort(date),
                                fontSize = 8.sp,
                                color = if (isToday) PrimaryLight else TextTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryWorkoutCard(
    item: HistoryWorkoutItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.workoutName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                // Subtitle: duration · volume · exercise count
                val subtitle = buildString {
                    append(item.durationText)
                    if (item.totalVolume > 0) {
                        append(" · ")
                        append(String.format("%,.0f", item.totalVolume))
                        append(" kg")
                    }
                    if (item.exerciseCount > 0) {
                        append(" · ")
                        append(item.exerciseCount)
                        append(" exercises")
                    }
                }
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                // Muscle group tags
                if (item.muscleGroups.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item.muscleGroups.take(3).forEach { mg ->
                            MuscleGroupTag(mg)
                        }
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp).align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun MuscleGroupTag(muscleGroup: MuscleGroup) {
    val (bgColor, textColor) = getMuscleGroupColors(muscleGroup)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(muscleGroup.name, fontSize = 9.sp, color = textColor)
    }
}

private fun getMuscleGroupColors(muscleGroup: MuscleGroup): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    return when (muscleGroup) {
        MuscleGroup.Chest, MuscleGroup.Shoulders, MuscleGroup.Triceps ->
            MuscleChestBg to MuscleChestColor
        MuscleGroup.Back, MuscleGroup.Biceps, MuscleGroup.Forearms, MuscleGroup.Lats, MuscleGroup.Traps ->
            MuscleBackBg to MuscleBackColor
        MuscleGroup.Quads, MuscleGroup.Hamstrings, MuscleGroup.Glutes, MuscleGroup.Calves ->
            MuscleLegsBg to MuscleLegsColor
        MuscleGroup.Abs ->
            MuscleChestBg to MuscleChestColor
    }
}

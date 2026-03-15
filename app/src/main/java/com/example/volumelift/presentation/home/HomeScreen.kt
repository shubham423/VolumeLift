package com.example.volumelift.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.data.local.entity.MuscleGroup
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutTemplate
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.MuscleBackBg
import com.example.volumelift.presentation.theme.MuscleBackColor
import com.example.volumelift.presentation.theme.MuscleChestBg
import com.example.volumelift.presentation.theme.MuscleChestColor
import com.example.volumelift.presentation.theme.MuscleLegsColor
import com.example.volumelift.presentation.theme.MuscleLegsBg
import com.example.volumelift.presentation.theme.Primary
import com.example.volumelift.presentation.theme.PrimaryContainer
import com.example.volumelift.presentation.theme.PrimaryDark
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.util.DateUtils

@Composable
fun HomeScreen(
    onNavigateToWorkout: (Long) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToWorkoutDetail: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refresh()
        }
    }

    Scaffold(containerColor = Background) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryLight) }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Header: date overline + greeting
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            Text(
                                DateUtils.formatDateOverline(System.currentTimeMillis()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = PrimaryLight.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp
                            )
                            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                            val greeting = when {
                                hour < 12 -> "Good morning"
                                hour < 17 -> "Good afternoon"
                                else -> "Good evening"
                            }
                            Text(
                                greeting,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W500,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // This week card
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Surface)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("THIS WEEK", fontSize = 10.sp, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            "${state.weekWorkoutCount}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.W500,
                                            color = TextPrimary
                                        )
                                        Text(
                                            " workouts",
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                }
                            }
                            // Volume card
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Surface)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("VOLUME", fontSize = 10.sp, color = TextSecondary, letterSpacing = 0.5.sp)
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        val volText = String.format("%,.0f", state.weekTotalVolume)
                                        Text(
                                            volText,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.W500,
                                            color = PrimaryLight
                                        )
                                        Text(
                                            " kg",
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Session Card
                    if (state.activeSession != null) {
                        item {
                            ActiveSessionCard(
                                session = state.activeSession,
                                onClick = { onNavigateToWorkout(state.activeSession.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // CTA Card — Start Workout
                    item {
                        StartWorkoutCTA(
                            onStartEmpty = {
                                viewModel.startEmptyWorkout { sessionId ->
                                    onNavigateToWorkout(sessionId)
                                }
                            },
                            onFromTemplate = onNavigateToTemplates,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Templates
                    if (state.templates.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Templates",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.templates) { template ->
                                    TemplateCard(
                                        template = template,
                                        onClick = {
                                            viewModel.startFromTemplate(template.id) { sessionId ->
                                                onNavigateToWorkout(sessionId)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Recent Workouts
                    if (state.recentWorkouts.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Recent workouts",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(state.recentWorkouts) { session ->
                            RecentWorkoutCard(
                                session = session,
                                muscleGroups = state.sessionMuscleGroups[session.id] ?: emptyList(),
                                onClick = { onNavigateToWorkoutDetail(session.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (state.templates.isEmpty() && state.recentWorkouts.isEmpty() && state.activeSession == null) {
                        item {
                            EmptyState(
                                icon = Icons.Default.FitnessCenter,
                                title = "Welcome to VolumeLift!",
                                subtitle = "Start your first workout to begin tracking your progress.",
                                actionLabel = "Start Workout",
                                onAction = {
                                    viewModel.startEmptyWorkout { sessionId ->
                                        onNavigateToWorkout(sessionId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        fontSize = 13.sp,
        fontWeight = FontWeight.W500,
        color = TextSecondary,
        modifier = modifier.padding(top = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun StartWorkoutCTA(
    onStartEmpty: () -> Unit,
    onFromTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(colors = listOf(Primary, PrimaryDark))
            )
            .clickable { onStartEmpty() }
            .padding(16.dp)
    ) {
        // Decorative circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(PrimaryLight.copy(alpha = 0.08f))
        )
        Column {
            Text("Start workout", fontSize = 16.sp, fontWeight = FontWeight.W500, color = TextPrimary)
            Text(
                "Empty workout or from template",
                fontSize = 12.sp,
                color = PrimaryLight.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { onStartEmpty() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("+ Empty", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.W500)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onFromTemplate() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("From template", fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun ActiveSessionCard(
    session: WorkoutSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() }.animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.FitnessCenter, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Active Workout", fontSize = 14.sp, fontWeight = FontWeight.W500, color = PrimaryLight)
                Text("Started ${DateUtils.formatTime(session.startTime)} - Tap to continue", fontSize = 11.sp, color = TextSecondary)
            }
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun TemplateCard(template: WorkoutTemplate, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(170.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(template.name, fontSize = 14.sp, fontWeight = FontWeight.W500, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text("${template.exerciseIds.size} exercises", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun RecentWorkoutCard(
    session: WorkoutSession,
    muscleGroups: List<MuscleGroup> = emptyList(),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workoutName = deriveWorkoutName(session)
    val totalVolume = session.exerciseLogs.flatMap { it.sets }.filter { it.isCompleted }.sumOf { it.weight * it.reps }

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    workoutName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                val subtitle = buildString {
                    append(DateUtils.formatRelativeDate(session.startTime))
                    append(" · ")
                    append(DateUtils.formatDurationShort(session.startTime, session.endTime))
                    if (totalVolume > 0) {
                        append(" · ")
                        append(String.format("%,.0f", totalVolume))
                        append(" kg")
                    }
                }
                Text(subtitle, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
            }
            if (muscleGroups.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    muscleGroups.take(2).forEach { mg ->
                        val (bgColor, textColor) = getMuscleGroupColors(mg)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgColor)
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(mg.name, fontSize = 10.sp, color = textColor)
                        }
                    }
                }
            }
        }
    }
}

/** Derive a workout name from exercise logs or notes */
private fun deriveWorkoutName(session: WorkoutSession): String {
    if (session.notes.isNotBlank()) return session.notes
    if (session.exerciseLogs.isEmpty()) return "Workout"
    val firstName = session.exerciseLogs.firstOrNull()?.exerciseName ?: ""
    val count = session.exerciseLogs.size
    return if (count <= 1) firstName.ifBlank { "Workout" }
    else "$firstName + ${count - 1} more"
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

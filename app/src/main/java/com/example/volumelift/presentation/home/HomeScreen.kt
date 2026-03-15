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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutTemplate
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.Primary
import com.example.volumelift.presentation.theme.PrimaryContainer
import com.example.volumelift.presentation.theme.PrimaryDark
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWorkout: (Long) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToWorkoutDetail: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryLight)
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Greeting header
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                "Good morning",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.W500
                            )
                        }
                    }

                    // Active Session Card
                    if (state.activeSession != null) {
                        item {
                            ActiveSessionCard(
                                session = state.activeSession,
                                onClick = { onNavigateToWorkout(state.activeSession.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
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
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    // Templates
                    if (state.templates.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Templates",
                                actionLabel = "See All",
                                onAction = onNavigateToTemplates,
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
                                actionLabel = "See All",
                                onAction = onNavigateToHistory,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        items(state.recentWorkouts) { session ->
                            RecentWorkoutCard(
                                session = session,
                                onClick = { onNavigateToWorkoutDetail(session.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
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
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            color = TextPrimary
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    actionLabel,
                    color = PrimaryLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W400
                )
            }
        }
    }
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
                brush = Brush.linearGradient(
                    colors = listOf(Primary, PrimaryDark)
                )
            )
            .clickable { onStartEmpty() }
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Start workout",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // + Empty pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onStartEmpty() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "+ Empty",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
                // From template pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onFromTemplate() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "From template",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
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
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Active Workout",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = PrimaryLight
                )
                Text(
                    "Started ${DateUtils.formatTime(session.startTime)} - Tap to continue",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TemplateCard(template: WorkoutTemplate, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                template.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "${template.exerciseIds.size} exercises",
                fontSize = 12.sp,
                color = TextSecondary
            )
            if (template.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    template.notes,
                    fontSize = 11.sp,
                    color = TextTertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecentWorkoutCard(session: WorkoutSession, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    DateUtils.formatDate(session.startTime),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                Text(
                    DateUtils.formatDuration(session.startTime, session.endTime),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

package com.example.volumelift.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.domain.model.WorkoutTemplate
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWorkout: (Long) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("VolumeLift") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startEmptyWorkout { sessionId ->
                        onNavigateToWorkout(sessionId)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Start workout")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.padding(paddingValues).padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Active Session Card
                    if (state.activeSession != null) {
                        item {
                            ActiveSessionCard(
                                session = state.activeSession,
                                onClick = { onNavigateToWorkout(state.activeSession.id) }
                            )
                        }
                    }

                    // Quick Start Section
                    item {
                        Text(
                            text = "Quick Start",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    item {
                        Button(
                            onClick = {
                                viewModel.startEmptyWorkout { sessionId ->
                                    onNavigateToWorkout(sessionId)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Empty Workout")
                        }
                    }

                    // Templates
                    if (state.templates.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Templates", style = MaterialTheme.typography.headlineSmall)
                                OutlinedButton(onClick = onNavigateToTemplates) {
                                    Text("See All")
                                }
                            }
                        }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Recent Workouts", style = MaterialTheme.typography.headlineSmall)
                                OutlinedButton(onClick = onNavigateToHistory) {
                                    Text("See All")
                                }
                            }
                        }
                        items(state.recentWorkouts) { session ->
                            RecentWorkoutCard(session = session)
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
fun ActiveSessionCard(session: WorkoutSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Active Workout", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Started ${DateUtils.formatTime(session.startTime)} - Tap to continue",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TemplateCard(template: WorkoutTemplate, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(template.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${template.exerciseIds.size} exercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (template.notes.isNotBlank()) {
                Text(
                    template.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun RecentWorkoutCard(session: WorkoutSession) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(DateUtils.formatDate(session.startTime), style = MaterialTheme.typography.titleMedium)
                Text(
                    "Duration: ${DateUtils.formatDuration(session.startTime, session.endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

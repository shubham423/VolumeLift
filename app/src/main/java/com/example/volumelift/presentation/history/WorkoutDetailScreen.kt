package com.example.volumelift.presentation.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is WorkoutDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WorkoutDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is WorkoutDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            DateUtils.formatDateTime(state.session.startTime),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            "Duration: ${DateUtils.formatDuration(state.session.startTime, state.session.endTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (state.session.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(state.session.notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    items(state.session.exerciseLogs) { exerciseLog ->
                        ExerciseDetailCard(exerciseLog = exerciseLog)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailCard(exerciseLog: ExerciseLogWithSets) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                exerciseLog.exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Set", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                Text("Weight", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                Text("Reps", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                Text("Type", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            exerciseLog.sets.forEach { set ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text("${set.setNumber}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("${set.weight}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text("${set.reps}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(set.setType.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }

            val totalVolume = exerciseLog.sets.filter { it.isCompleted }.sumOf { it.weight * it.reps }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Total Volume: ${String.format("%.0f", totalVolume)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.example.volumelift.presentation.workout

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.volumelift.data.local.entity.SetType
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.presentation.components.RepsPicker
import com.example.volumelift.presentation.components.WeightPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onNavigateToExercisePicker: (Long) -> Unit,
    onFinish: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Workout?") },
            text = { Text("This will discard all progress for this workout.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelWorkout { onFinish() }
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Keep") }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?") },
            text = { Text("Complete this workout and save it to your history.") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.completeWorkout { onFinish() }
                }) { Text("Finish") }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Cancel") }
            }
        )
    }

    when (val state = uiState) {
        is ActiveWorkoutUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ActiveWorkoutUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is ActiveWorkoutUiState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text("Workout", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    state.elapsedTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { showCancelDialog = true }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel")
                            }
                        },
                        actions = {
                            Button(onClick = { showFinishDialog = true }) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Finish")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Rest timer bar
                    if (state.isRestTimerRunning) {
                        RestTimerBar(
                            seconds = state.restTimerSeconds,
                            onCancel = { viewModel.cancelRestTimer() }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.exerciseLogs, key = { it.id }) { exerciseLog ->
                            ExerciseLogCard(
                                exerciseLog = exerciseLog,
                                onAddSet = { viewModel.addSet(exerciseLog.id) },
                                onUpdateSet = { set -> viewModel.updateSet(set) },
                                onCompleteSet = { set -> viewModel.completeSet(set) },
                                onDeleteSet = { setId -> viewModel.deleteSet(setId) },
                                onRemoveExercise = { viewModel.removeExercise(exerciseLog.id) }
                            )
                        }

                        item {
                            OutlinedButton(
                                onClick = { onNavigateToExercisePicker(state.session.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Exercise")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestTimerBar(seconds: Int, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Rest: ${seconds}s",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onCancel) { Text("Skip") }
    }
}

@Composable
fun ExerciseLogCard(
    exerciseLog: ExerciseLogWithSets,
    onAddSet: () -> Unit,
    onUpdateSet: (WorkoutSet) -> Unit,
    onCompleteSet: (WorkoutSet) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onRemoveExercise: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseLog.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onRemoveExercise) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove exercise",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Set", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                Text("Type", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(64.dp), textAlign = TextAlign.Center)
                Text("Weight", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Reps", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Done", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(48.dp), textAlign = TextAlign.Center)
            }

            exerciseLog.sets.forEach { set ->
                SetRow(
                    set = set,
                    onUpdateSet = onUpdateSet,
                    onCompleteSet = onCompleteSet,
                    onDeleteSet = { onDeleteSet(set.id) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Set")
            }
        }
    }
}

@Composable
fun SetRow(
    set: WorkoutSet,
    onUpdateSet: (WorkoutSet) -> Unit,
    onCompleteSet: (WorkoutSet) -> Unit,
    onDeleteSet: () -> Unit
) {
    var showTypeMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Box(modifier = Modifier.width(64.dp)) {
            FilterChip(
                selected = false,
                onClick = { showTypeMenu = true },
                label = {
                    Text(
                        when (set.setType) {
                            SetType.Working -> "W"
                            SetType.Warmup -> "WU"
                            SetType.Dropset -> "D"
                            SetType.Failure -> "F"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
            DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                SetType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            onUpdateSet(set.copy(setType = type))
                            showTypeMenu = false
                        }
                    )
                }
            }
        }

        WeightPicker(
            value = set.weight,
            onValueChange = { onUpdateSet(set.copy(weight = it)) },
            modifier = Modifier.weight(1f)
        )

        RepsPicker(
            value = set.reps,
            onValueChange = { onUpdateSet(set.copy(reps = it)) },
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { checked ->
                if (checked) onCompleteSet(set.copy(isCompleted = true))
                else onUpdateSet(set.copy(isCompleted = false))
            },
            modifier = Modifier.width(48.dp)
        )
    }
}

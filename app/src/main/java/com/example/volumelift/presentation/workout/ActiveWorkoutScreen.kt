package com.example.volumelift.presentation.workout

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.data.local.entity.SetType
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.domain.model.WorkoutSet
import com.example.volumelift.presentation.components.RepsPicker
import com.example.volumelift.presentation.components.WeightPicker
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.Border
import com.example.volumelift.presentation.theme.OnTarget
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.Primary
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.RestTimerBg
import com.example.volumelift.presentation.theme.RestTimerButtonBg
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.SurfaceVariant
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.presentation.theme.UnderTarget

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
            title = { Text("Cancel Workout?", fontWeight = FontWeight.W500) },
            text = { Text("This will discard all progress for this workout.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelWorkout { onFinish() }
                }) { Text("Discard", color = OverTarget) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Keep") }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?", fontWeight = FontWeight.W500) },
            text = { Text("Complete this workout and save it to your history.") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishDialog = false
                    viewModel.completeWorkout { onFinish() }
                }) { Text("Finish", color = OnTarget) }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Cancel") }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    when (val state = uiState) {
        is ActiveWorkoutUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLight)
            }
        }
        is ActiveWorkoutUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
                Text(state.message, color = OverTarget)
            }
        }
        is ActiveWorkoutUiState.Success -> {
            val workoutName = deriveWorkoutName(state)

            Scaffold(
                containerColor = Background,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    workoutName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    color = TextPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(OnTarget)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        state.elapsedTime,
                                        fontSize = 12.sp,
                                        color = PrimaryLight,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { showCancelDialog = true }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = TextSecondary)
                            }
                        },
                        actions = {
                            Button(
                                onClick = { showFinishDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Finish", fontSize = 12.sp, fontWeight = FontWeight.W500)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
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
                            onAdjust = { delta -> viewModel.adjustRestTimer(delta) },
                            onCancel = { viewModel.cancelRestTimer() },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(state.exerciseLogs, key = { it.id }) { exerciseLog ->
                            ExerciseLogSection(
                                exerciseLog = exerciseLog,
                                onAddSet = { viewModel.addSet(exerciseLog.id) },
                                onUpdateSet = { set -> viewModel.updateSet(set) },
                                onCompleteSet = { set -> viewModel.completeSet(set) },
                                onDeleteSet = { setId -> viewModel.deleteSet(setId) },
                                onRemoveExercise = { viewModel.removeExercise(exerciseLog.id) }
                            )

                            // Divider between exercises
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .height(0.5.dp)
                                    .background(SurfaceVariant)
                            )
                        }

                        // Add exercise button
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToExercisePicker(state.session.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "+ Add exercise",
                                        fontSize = 13.sp,
                                        color = PrimaryLight,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun deriveWorkoutName(state: ActiveWorkoutUiState.Success): String {
    val session = state.session
    if (session.notes.isNotBlank()) return session.notes
    if (state.exerciseLogs.isEmpty()) return "Workout"
    val firstName = state.exerciseLogs.firstOrNull()?.exerciseName ?: ""
    val count = state.exerciseLogs.size
    return if (count <= 1) firstName.ifBlank { "Workout" }
    else "$firstName + ${count - 1} more"
}

@Composable
fun RestTimerBar(
    seconds: Int,
    onAdjust: (Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = seconds / 60
    val secs = seconds % 60
    val timeText = "$minutes:${String.format("%02d", secs)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RestTimerBg)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "REST TIMER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W500,
                    color = UnderTarget,
                    letterSpacing = 0.5.sp
                )
                Text(
                    timeText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // -15 button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RestTimerButtonBg)
                        .clickable { onAdjust(-15) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-15", fontSize = 12.sp, color = UnderTarget)
                }
                // +15 button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RestTimerButtonBg)
                        .clickable { onAdjust(15) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+15", fontSize = 12.sp, color = UnderTarget)
                }
                // Cancel button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(UnderTarget.copy(alpha = 0.2f))
                        .clickable { onCancel() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u00D7", fontSize = 14.sp, color = UnderTarget)
                }
            }
        }
    }
}

@Composable
fun ExerciseLogSection(
    exerciseLog: ExerciseLogWithSets,
    onAddSet: () -> Unit,
    onUpdateSet: (WorkoutSet) -> Unit,
    onCompleteSet: (WorkoutSet) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onRemoveExercise: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        // Exercise name + remove
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exerciseLog.exerciseName,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = PrimaryLight
            )
            IconButton(
                onClick = onRemoveExercise,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Remove exercise",
                    tint = OverTarget.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Set headers
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Set", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
            Text("Previous", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("kg", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.width(56.dp), textAlign = TextAlign.Center)
            Text("Reps", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(28.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        exerciseLog.sets.forEach { set ->
            SetRow(
                set = set,
                onUpdateSet = onUpdateSet,
                onCompleteSet = onCompleteSet,
                onDeleteSet = { onDeleteSet(set.id) }
            )
        }

        // Add set
        Text(
            "+ Add set",
            fontSize = 12.sp,
            color = PrimaryLight,
            fontWeight = FontWeight.W500,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddSet() }
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
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
            .padding(vertical = 1.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Set number
        Text(
            "${set.setNumber}",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        // Previous (placeholder — shows "—" for now)
        Text(
            "—",
            fontSize = 11.sp,
            color = TextTertiary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Weight input
        WeightPicker(
            value = set.weight,
            onValueChange = { onUpdateSet(set.copy(weight = it)) },
            modifier = Modifier.width(56.dp)
        )

        // Reps input
        RepsPicker(
            value = set.reps,
            onValueChange = { onUpdateSet(set.copy(reps = it)) },
            modifier = Modifier.width(44.dp)
        )

        // Check button
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (set.isCompleted) Primary else Color.Transparent)
                .then(
                    if (!set.isCompleted) Modifier.background(Color.Transparent)
                        .clip(RoundedCornerShape(8.dp))
                    else Modifier
                )
                .let {
                    if (!set.isCompleted) it.background(
                        Color.Transparent
                    ) else it
                }
                .clickable {
                    if (set.isCompleted) onUpdateSet(set.copy(isCompleted = false))
                    else onCompleteSet(set.copy(isCompleted = true))
                },
            contentAlignment = Alignment.Center
        ) {
            if (set.isCompleted) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = "Completed",
                    tint = TextPrimary,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                // Empty bordered box
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent)
                        .then(
                            Modifier.background(Color.Transparent)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Transparent)
                            .padding(0.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(7.5.dp))
                                .background(Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

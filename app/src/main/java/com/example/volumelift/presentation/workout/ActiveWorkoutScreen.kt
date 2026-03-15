package com.example.volumelift.presentation.workout

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
            Scaffold(
                containerColor = Background,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    "Workout",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500,
                                    color = TextPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Pulsing green dot
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(OnTarget)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
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
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Background
                        )
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                            ) {
                                Icon(
                                    Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Exercise", fontWeight = FontWeight.W500)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RestTimerBg)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Rounded.Timer,
                contentDescription = null,
                tint = UnderTarget,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "REST TIMER",
                fontSize = 10.sp,
                fontWeight = FontWeight.W500,
                color = UnderTarget,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${seconds}s",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RestTimerButtonBg)
            ) {
                TextButton(onClick = onCancel) {
                    Text("Skip", fontSize = 12.sp, color = UnderTarget)
                }
            }
        }
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
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

            Spacer(modifier = Modifier.height(8.dp))

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Set", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                Text("Type", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.width(64.dp), textAlign = TextAlign.Center)
                Text("kg", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Reps", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("", fontSize = 10.sp, modifier = Modifier.width(44.dp))
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

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Set", fontSize = 12.sp, color = PrimaryLight, fontWeight = FontWeight.W500)
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

    val rowBackground = if (set.isCompleted) {
        OnTarget.copy(alpha = 0.08f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${set.setNumber}",
            fontSize = 12.sp,
            color = TextSecondary,
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W500
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
            modifier = Modifier.width(44.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = Primary,
                checkmarkColor = TextPrimary,
                uncheckedColor = Border
            )
        )
    }
}

package com.solostackdev.volumelift.presentation.exercises

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.domain.model.Exercise
import com.solostackdev.volumelift.presentation.theme.Background
import com.solostackdev.volumelift.presentation.theme.MuscleSecondaryBg
import com.solostackdev.volumelift.presentation.theme.MuscleSecondaryColor
import com.solostackdev.volumelift.presentation.theme.OverTarget
import com.solostackdev.volumelift.presentation.theme.Primary
import com.solostackdev.volumelift.presentation.theme.PrimaryContainer
import com.solostackdev.volumelift.presentation.theme.PrimaryLight
import com.solostackdev.volumelift.presentation.theme.Surface
import com.solostackdev.volumelift.presentation.theme.TextPrimary
import com.solostackdev.volumelift.presentation.theme.TextSecondary
import com.solostackdev.volumelift.presentation.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExerciseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, primary, secondaries ->
                viewModel.addCustomExercise(name, primary, secondaries)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Primary,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add exercise")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Title
            Text(
                "Exercises",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.search(it)
                },
                onSearch = { viewModel.search(it) },
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search exercises...", fontSize = 13.sp, color = TextTertiary) }
            ) {}

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMuscleGroup == null,
                        onClick = {
                            selectedMuscleGroup = null
                            viewModel.filterByMuscleGroup(null)
                        },
                        label = { Text("All", fontSize = 11.sp, fontWeight = FontWeight.W500) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = TextPrimary,
                            containerColor = Surface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                items(MuscleGroup.entries.toTypedArray()) { muscleGroup ->
                    FilterChip(
                        selected = selectedMuscleGroup == muscleGroup,
                        onClick = {
                            selectedMuscleGroup = muscleGroup
                            viewModel.filterByMuscleGroup(muscleGroup)
                        },
                        label = { Text(muscleGroup.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = TextPrimary,
                            containerColor = Surface,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is ExerciseLibraryUiState.Loading -> {}
                is ExerciseLibraryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message, color = OverTarget)
                    }
                }
                is ExerciseLibraryUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(state.exercises) { exercise ->
                            ExerciseItem(
                                exercise = exercise,
                                prWeight = state.prMap[exercise.id],
                                onClick = { onNavigateToDetail(exercise.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    prWeight: Double? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: name + tags
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            exercise.primaryMuscleGroup.name,
                            fontSize = 9.sp,
                            color = PrimaryLight
                        )
                    }
                    exercise.secondaryMuscleGroups.forEach { mg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MuscleSecondaryBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(mg.name, fontSize = 9.sp, color = MuscleSecondaryColor)
                        }
                    }
                }
            }
            // Right: PR info
            if (prWeight != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "PR: ${String.format("%.0f", prWeight)}kg",
                        fontSize = 11.sp,
                        color = PrimaryLight
                    )
                }
            }
        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, MuscleGroup, List<MuscleGroup>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var primaryMuscle by remember { mutableStateOf(MuscleGroup.Chest) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Exercise", fontWeight = FontWeight.W500, fontSize = 14.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Primary Muscle",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W500,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(MuscleGroup.entries.toTypedArray()) { mg ->
                        FilterChip(
                            selected = primaryMuscle == mg,
                            onClick = { primaryMuscle = mg },
                            label = { Text(mg.name, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = TextPrimary,
                                containerColor = Surface,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, primaryMuscle, emptyList()) },
                enabled = name.isNotBlank()
            ) { Text("Add", fontWeight = FontWeight.W500, color = PrimaryLight) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = Surface,
        shape = RoundedCornerShape(12.dp)
    )
}

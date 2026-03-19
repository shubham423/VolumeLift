package com.solostackdev.volumelift.presentation.workout

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solostackdev.volumelift.data.local.entity.MuscleGroup
import com.solostackdev.volumelift.domain.model.Exercise
import com.solostackdev.volumelift.presentation.exercises.ExerciseLibraryUiState
import com.solostackdev.volumelift.presentation.exercises.ExerciseLibraryViewModel
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
fun ExercisePickerScreen(
    onExerciseSelected: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Exercise", fontSize = 18.sp, fontWeight = FontWeight.W500, color = TextPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary)
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

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMuscleGroup == null,
                        onClick = {
                            selectedMuscleGroup = null
                            viewModel.filterByMuscleGroup(null)
                        },
                        label = { Text("All", fontSize = 11.sp) },
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

            Spacer(modifier = Modifier.height(8.dp))

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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(state.exercises) { exercise ->
                            ExercisePickerItem(
                                exercise = exercise,
                                onClick = { onExerciseSelected(exercise.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExercisePickerItem(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                exercise.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        Text(
                            mg.name,
                            fontSize = 9.sp,
                            color = MuscleSecondaryColor
                        )
                    }
                }
            }
        }
    }
}

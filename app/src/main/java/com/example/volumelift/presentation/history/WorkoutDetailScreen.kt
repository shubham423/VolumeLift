package com.example.volumelift.presentation.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.volumelift.domain.model.ExerciseLogWithSets
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.PrimaryContainer
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.SurfaceVariant
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Workout Detail",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500,
                        color = TextPrimary
                    )
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
        when (val state = uiState) {
            is WorkoutDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLight)
                }
            }
            is WorkoutDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(state.message, color = OverTarget)
                }
            }
            is WorkoutDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
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
                                        .background(PrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Schedule,
                                        contentDescription = null,
                                        tint = PrimaryLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        DateUtils.formatDateTime(state.session.startTime),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W500,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Duration: ${DateUtils.formatDuration(state.session.startTime, state.session.endTime)}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    if (state.session.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            state.session.notes,
                                            fontSize = 11.sp,
                                            color = TextTertiary
                                        )
                                    }
                                }
                            }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                exerciseLog.exerciseName,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = PrimaryLight
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text("Set", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f))
                Text("Weight", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f))
                Text("Reps", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f))
                Text("Type", fontSize = 10.sp, color = TextTertiary, modifier = Modifier.weight(1f))
            }

            exerciseLog.sets.forEach { set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("${set.setNumber}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                    Text("${set.weight}", fontSize = 12.sp, fontWeight = FontWeight.W500, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text("${set.reps}", fontSize = 12.sp, fontWeight = FontWeight.W500, color = TextPrimary, modifier = Modifier.weight(1f))
                    Text(
                        set.setType.name,
                        fontSize = 11.sp,
                        color = TextTertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            val totalVolume = exerciseLog.sets.filter { it.isCompleted }.sumOf { it.weight * it.reps }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(SurfaceVariant)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Total Volume: ${String.format("%.0f", totalVolume)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                color = TextSecondary
            )
        }
    }
}

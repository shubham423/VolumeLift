package com.example.volumelift.presentation.volume

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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.components.VolumeProgressBar
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.SurfaceVariant
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeScreen(
    viewModel: VolumeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        when (val state = uiState) {
            is VolumeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryLight)
                }
            }
            is VolumeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is VolumeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Title
                    Text(
                        "Weekly Volume",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W500,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    // Week navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.previousWeek() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Surface
                            )
                        ) {
                            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous week", tint = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (state.weekOffset == 0) "THIS WEEK" else state.weekLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.W500,
                                color = TextTertiary,
                                letterSpacing = 0.5.sp
                            )
                            if (state.weekOffset != 0) {
                                Text(
                                    state.weekLabel,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.nextWeek() },
                            enabled = state.weekOffset < 0,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Surface
                            )
                        ) {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next week", tint = TextSecondary)
                        }
                    }

                    val hasAnyData = state.muscleVolumes.any { it.currentSets > 0 || it.currentVolume > 0 }

                    if (!hasAnyData) {
                        EmptyState(
                            icon = Icons.AutoMirrored.Filled.ShowChart,
                            title = "No Volume Data",
                            subtitle = "Complete workouts to see your weekly muscle volume breakdown here."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Summary card
                            item {
                                VolumeSummaryCard(state)
                            }

                            items(state.muscleVolumes.sortedByDescending { it.currentSets }) { volume ->
                                VolumeProgressBar(
                                    muscleVolume = volume,
                                    useKg = state.preferences.useKg
                                )
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolumeSummaryCard(state: VolumeUiState.Success) {
    val totalSets = state.muscleVolumes.sumOf { it.currentSets }
    val totalTargetSets = state.muscleVolumes.sumOf { it.targetSets }
    val totalVolume = state.muscleVolumes.sumOf { it.currentVolume }
    val unit = if (state.preferences.useKg) "kg" else "lbs"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Total sets stat
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$totalSets",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                Text(
                    "/ $totalTargetSets sets",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "TOTAL SETS",
                    fontSize = 10.sp,
                    color = TextTertiary,
                    letterSpacing = 0.5.sp
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .height(50.dp)
                    .background(SurfaceVariant)
            )

            // Total volume stat
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    String.format("%.0f", totalVolume),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                Text(
                    unit,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W400,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "TOTAL VOLUME",
                    fontSize = 10.sp,
                    color = TextTertiary,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

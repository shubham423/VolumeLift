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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.domain.model.MuscleVolume
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.components.VolumeProgressBar
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.OnTarget
import com.example.volumelift.presentation.theme.OnTargetBg
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.OverTargetBg
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.presentation.theme.UnderTarget
import com.example.volumelift.presentation.theme.UnderTargetBg

@Composable
fun VolumeScreen(
    viewModel: VolumeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(containerColor = Background) { paddingValues ->
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
                ) {
                    // Title + week nav
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            Text(
                                "Weekly volume",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W500,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    state.weekLabel,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Surface)
                                        .let {
                                            it
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { viewModel.previousWeek() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.ChevronLeft,
                                            contentDescription = "Previous week",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { viewModel.nextWeek() },
                                        enabled = state.weekOffset < 0,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.ChevronRight,
                                            contentDescription = "Next week",
                                            tint = if (state.weekOffset < 0) TextSecondary else TextTertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    val hasAnyData = state.muscleVolumes.any { it.currentSets > 0 || it.currentVolume > 0 }

                    if (!hasAnyData) {
                        item {
                            EmptyState(
                                icon = Icons.AutoMirrored.Filled.ShowChart,
                                title = "No Volume Data",
                                subtitle = "Complete workouts to see your weekly muscle volume breakdown here."
                            )
                        }
                    } else {
                        // Summary pills
                        item {
                            VolumeSummaryPills(
                                muscleVolumes = state.muscleVolumes,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        // Volume bars — sorted by status: under-target first, on-target second, over-target last
                        val sortedVolumes = state.muscleVolumes
                            .filter { it.currentSets > 0 || it.currentVolume > 0 }
                            .sortedWith(compareBy<MuscleVolume> { volume ->
                                when {
                                    volume.volumeProgressPercent < 70f -> 0 // under
                                    volume.volumeProgressPercent <= 110f -> 1 // on target
                                    else -> 2 // over
                                }
                            }.thenBy { volume ->
                                when {
                                    volume.volumeProgressPercent < 70f -> volume.volumeProgressPercent // lowest first for under
                                    volume.volumeProgressPercent > 110f -> -volume.volumeProgressPercent // highest first for over
                                    else -> volume.volumeProgressPercent
                                }
                            }) + state.muscleVolumes.filter { it.currentSets == 0 && it.currentVolume == 0.0 }

                        items(sortedVolumes) { volume ->
                            VolumeProgressBar(
                                muscleVolume = volume,
                                useKg = state.preferences.useKg,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolumeSummaryPills(
    muscleVolumes: List<com.example.volumelift.domain.model.MuscleVolume>,
    modifier: Modifier = Modifier
) {
    val onTargetCount = muscleVolumes.count {
        it.setsProgressPercent in 85f..110f && (it.currentSets > 0 || it.currentVolume > 0)
    }
    val underCount = muscleVolumes.count {
        it.setsProgressPercent < 85f && (it.currentSets > 0 || it.currentVolume > 0)
    }
    val overCount = muscleVolumes.count {
        it.setsProgressPercent > 110f
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (onTargetCount > 0) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(OnTargetBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("$onTargetCount on target", fontSize = 11.sp, color = OnTarget)
                }
            }
        }
        if (underCount > 0) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(UnderTargetBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("$underCount under", fontSize = 11.sp, color = UnderTarget)
                }
            }
        }
        if (overCount > 0) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(OverTargetBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("$overCount over", fontSize = 11.sp, color = OverTarget)
                }
            }
        }
    }
}

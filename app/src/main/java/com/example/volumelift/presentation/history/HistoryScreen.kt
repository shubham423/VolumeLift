package com.example.volumelift.presentation.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.domain.model.WorkoutSession
import com.example.volumelift.presentation.components.EmptyState
import com.example.volumelift.presentation.theme.Background
import com.example.volumelift.presentation.theme.OverTarget
import com.example.volumelift.presentation.theme.PrimaryLight
import com.example.volumelift.presentation.theme.Surface
import com.example.volumelift.presentation.theme.TextPrimary
import com.example.volumelift.presentation.theme.TextSecondary
import com.example.volumelift.presentation.theme.TextTertiary
import com.example.volumelift.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWorkout: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        when (val state = uiState) {
            is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryLight)
                }
            }
            is HistoryUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = OverTarget)
                }
            }
            is HistoryUiState.Success -> {
                if (state.sessions.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.History,
                        title = "No Workouts Yet",
                        subtitle = "Complete your first workout to see it here!",
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(paddingValues),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Title
                        item {
                            Text(
                                "History",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.W500,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(state.sessions) { session ->
                            HistoryItem(
                                session = session,
                                onClick = { onNavigateToDetail(session.id) },
                                onDuplicate = {
                                    viewModel.duplicateWorkout(session.id) { newId ->
                                        onNavigateToWorkout(newId)
                                    }
                                },
                                onDelete = { viewModel.deleteSession(session.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    session: WorkoutSession,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    DateUtils.formatDateTime(session.startTime),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    DateUtils.formatDuration(session.startTime, session.endTime),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (session.notes.isNotBlank()) {
                    Text(
                        session.notes,
                        fontSize = 11.sp,
                        color = TextTertiary,
                        maxLines = 1
                    )
                }
            }
            IconButton(onClick = onDuplicate) {
                Icon(
                    Icons.Rounded.ContentCopy,
                    contentDescription = "Duplicate",
                    tint = PrimaryLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete",
                    tint = OverTarget.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

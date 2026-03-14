package com.example.volumelift.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.volumelift.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToExerciseLibrary: () -> Unit,
    onNavigateToBodyWeight: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            }
            is SettingsUiState.Error -> {
                Text(state.message, modifier = Modifier.padding(paddingValues))
            }
            is SettingsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Text("General", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Unit toggle
                    item {
                        SettingsRow(
                            icon = Icons.Default.Scale,
                            title = "Units",
                            subtitle = if (state.preferences.useKg) "Kilograms (kg)" else "Pounds (lbs)"
                        ) {
                            Switch(
                                checked = state.preferences.useKg,
                                onCheckedChange = { viewModel.toggleUnit() }
                            )
                        }
                    }

                    // Rest timer
                    item {
                        var showMenu by remember { mutableStateOf(false) }
                        SettingsRow(
                            icon = Icons.Default.Timer,
                            title = "Default Rest Timer",
                            subtitle = "${state.preferences.defaultRestTimerSeconds}s",
                            onClick = { showMenu = true }
                        ) {
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                listOf(30, 60, 90, 120, 180, 300).forEach { seconds ->
                                    DropdownMenuItem(
                                        text = { Text("${seconds}s") },
                                        onClick = {
                                            viewModel.updateRestTimer(seconds)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Theme
                    item {
                        var showMenu by remember { mutableStateOf(false) }
                        SettingsRow(
                            icon = Icons.Default.DarkMode,
                            title = "Theme",
                            subtitle = state.preferences.themeMode.name,
                            onClick = { showMenu = true }
                        ) {
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                ThemeMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = { Text(mode.name) },
                                        onClick = {
                                            viewModel.updateThemeMode(mode)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SettingsRow(
                            icon = Icons.Default.FitnessCenter,
                            title = "Exercise Library",
                            subtitle = "View and manage exercises",
                            onClick = onNavigateToExerciseLibrary
                        )
                    }

                    item {
                        SettingsRow(
                            icon = Icons.Default.Scale,
                            title = "Body Weight",
                            subtitle = "Track your body weight",
                            onClick = onNavigateToBodyWeight
                        )
                    }

                    item {
                        SettingsRow(
                            icon = Icons.Default.FitnessCenter,
                            title = "Templates",
                            subtitle = "Manage workout templates",
                            onClick = onNavigateToTemplates
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing?.invoke()
    }
}

package com.example.volumelift.presentation.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            }
            is SettingsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { Text(state.message) }
            }
            is SettingsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // General section
                    item {
                        SectionLabel("GENERAL")
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column {
                                // Unit toggle
                                SettingsRow(
                                    icon = Icons.Rounded.Scale,
                                    title = "Units",
                                    subtitle = if (state.preferences.useKg) "Kilograms (kg)" else "Pounds (lbs)"
                                ) {
                                    Switch(
                                        checked = state.preferences.useKg,
                                        onCheckedChange = { viewModel.toggleUnit() },
                                        colors = SwitchDefaults.colors(
                                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                                            checkedThumbColor = Color.White
                                        )
                                    )
                                }

                                SettingsDivider()

                                // Rest timer
                                var showTimerMenu by remember { mutableStateOf(false) }
                                SettingsRow(
                                    icon = Icons.Rounded.Timer,
                                    title = "Default Rest Timer",
                                    subtitle = "${state.preferences.defaultRestTimerSeconds}s",
                                    onClick = { showTimerMenu = true }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        DropdownMenu(expanded = showTimerMenu, onDismissRequest = { showTimerMenu = false }) {
                                            listOf(30, 60, 90, 120, 180, 300).forEach { seconds ->
                                                DropdownMenuItem(
                                                    text = { Text("${seconds}s") },
                                                    onClick = {
                                                        viewModel.updateRestTimer(seconds)
                                                        showTimerMenu = false
                                                    }
                                                )
                                            }
                                        }
                                        Icon(
                                            Icons.Rounded.ChevronRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                SettingsDivider()

                                // Theme
                                var showThemeMenu by remember { mutableStateOf(false) }
                                SettingsRow(
                                    icon = Icons.Rounded.DarkMode,
                                    title = "Theme",
                                    subtitle = state.preferences.themeMode.name,
                                    onClick = { showThemeMenu = true }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        DropdownMenu(expanded = showThemeMenu, onDismissRequest = { showThemeMenu = false }) {
                                            ThemeMode.entries.forEach { mode ->
                                                DropdownMenuItem(
                                                    text = { Text(mode.name) },
                                                    onClick = {
                                                        viewModel.updateThemeMode(mode)
                                                        showThemeMenu = false
                                                    }
                                                )
                                            }
                                        }
                                        Icon(
                                            Icons.Rounded.ChevronRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Data section
                    item {
                        SectionLabel("DATA")
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column {
                                SettingsRow(
                                    icon = Icons.Rounded.FitnessCenter,
                                    title = "Exercise Library",
                                    subtitle = "View and manage exercises",
                                    onClick = onNavigateToExerciseLibrary,
                                    showArrow = true
                                )

                                SettingsDivider()

                                SettingsRow(
                                    icon = Icons.Rounded.MonitorWeight,
                                    title = "Body Weight",
                                    subtitle = "Track your body weight",
                                    onClick = onNavigateToBodyWeight,
                                    showArrow = true
                                )

                                SettingsDivider()

                                SettingsRow(
                                    icon = Icons.AutoMirrored.Rounded.ViewList,
                                    title = "Templates",
                                    subtitle = "Manage workout templates",
                                    onClick = onNavigateToTemplates,
                                    showArrow = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    showArrow: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (trailing != null) {
            trailing()
        } else if (showArrow) {
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

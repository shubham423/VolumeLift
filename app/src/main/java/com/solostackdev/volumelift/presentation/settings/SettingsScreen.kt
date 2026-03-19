package com.solostackdev.volumelift.presentation.settings

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solostackdev.volumelift.presentation.theme.Background
import com.solostackdev.volumelift.presentation.theme.Primary
import com.solostackdev.volumelift.presentation.theme.PrimaryContainer
import com.solostackdev.volumelift.presentation.theme.PrimaryLight
import com.solostackdev.volumelift.presentation.theme.Surface
import com.solostackdev.volumelift.presentation.theme.SurfaceVariant
import com.solostackdev.volumelift.presentation.theme.TextPrimary
import com.solostackdev.volumelift.presentation.theme.TextSecondary
import com.solostackdev.volumelift.presentation.theme.TextTertiary

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
        containerColor = Background
    ) { paddingValues ->
        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = PrimaryLight) }
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
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Title
                    item {
                        Text(
                            "Settings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W500,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // General section
                    item {
                        SectionLabel("GENERAL")
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                            checkedTrackColor = Primary,
                                            checkedThumbColor = TextPrimary
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
                                            modifier = Modifier.size(16.dp),
                                            tint = TextTertiary
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
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
        fontSize = 11.sp,
        fontWeight = FontWeight.W500,
        color = TextTertiary,
        letterSpacing = 0.5.sp,
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
            .background(SurfaceVariant)
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
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryLight,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                color = TextPrimary
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        if (trailing != null) {
            trailing()
        } else if (showArrow) {
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextTertiary
            )
        }
    }
}

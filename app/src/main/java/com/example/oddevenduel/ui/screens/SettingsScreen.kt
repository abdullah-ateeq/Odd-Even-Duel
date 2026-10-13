package com.example.oddevenduel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oddevenduel.domain.model.AppTheme
import com.example.oddevenduel.domain.model.CpuDifficulty
import com.example.oddevenduel.domain.model.TurnTimerOption
import com.example.oddevenduel.ui.OddEvenViewModel

@Composable
fun SettingsScreen(
    viewModel: OddEvenViewModel
) {
    val settings by viewModel.settings.collectAsState()

    var showResetStatsDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    if (showResetStatsDialog) {
        AlertDialog(
            onDismissRequest = { showResetStatsDialog = false },
            title = { Text(text = "Reset Statistics?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "This will erase all recorded games, win streaks, and records.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetStatistics()
                        showResetStatsDialog = false
                    }
                ) {
                    Text(text = "Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetStatsDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(text = "Reset Settings?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "This will reset all preferences back to factory defaults.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text(text = "Reset Defaults", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "SETTINGS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Gameplay Section
        SectionTitle(title = "GAMEPLAY & OPPONENT")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CPU Difficulty",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CpuDifficulty.values().forEach { diff ->
                        val selected = settings.cpuDifficulty == diff
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.updateSettings(settings.copy(cpuDifficulty = diff)) },
                            label = { Text(text = diff.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "Turn Timer",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TurnTimerOption.values().forEach { timerOpt ->
                        val selected = settings.turnTimer == timerOpt
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.updateSettings(settings.copy(turnTimer = timerOpt)) },
                            label = { Text(text = timerOpt.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance Section
        SectionTitle(title = "APPEARANCE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Theme",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTheme.values().forEach { themeOpt ->
                        val selected = settings.theme == themeOpt
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.updateSettings(settings.copy(theme = themeOpt)) },
                            label = { Text(text = themeOpt.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SettingSwitchRow(
                    title = "Dynamic Color",
                    subtitle = "Material You accent color matching",
                    checked = settings.dynamicColor,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(dynamicColor = it)) }
                )

                SettingSwitchRow(
                    title = "Animations",
                    subtitle = "Smooth scale, shake, and score popups",
                    checked = settings.animationsEnabled,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(animationsEnabled = it)) }
                )

                SettingSwitchRow(
                    title = "Show Coordinates",
                    subtitle = "Display subtle A1..F6 labels on cells",
                    checked = settings.showCoordinates,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showCoordinates = it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Feedback & Controls Section
        SectionTitle(title = "AUDIO & FEEDBACK")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingSwitchRow(
                    title = "Sound Effects",
                    subtitle = "Tones on selections, claims, and turns",
                    checked = settings.soundEnabled,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(soundEnabled = it)) }
                )

                SettingSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Tactile vibration responses",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(hapticsEnabled = it)) }
                )

                SettingSwitchRow(
                    title = "Show Math Feedback",
                    subtitle = "Explain parity after pick (e.g. 17 IS ODD)",
                    checked = settings.showMathFeedback,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showMathFeedback = it)) }
                )

                SettingSwitchRow(
                    title = "Confirm Restart",
                    subtitle = "Prompt before abandoning an ongoing duel",
                    checked = settings.confirmRestart,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(confirmRestart = it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Buttons
        OutlinedButton(
            onClick = { showResetStatsDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Reset Statistics", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { showResetSettingsDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Reset Settings", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

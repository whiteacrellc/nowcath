package org.tomw.cathnow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.tomw.cathnow.R
import org.tomw.cathnow.audio.CathAudioManager
import org.tomw.cathnow.data.PreferencesManager
import org.tomw.cathnow.data.SoundOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val audioManager = remember { CathAudioManager(context) }

    // Clean up audio resources when screen is disposed
    DisposableEffect(audioManager) {
        onDispose {
            audioManager.cleanup()
        }
    }

    var selectedSoundOption by remember { mutableStateOf(preferencesManager.selectedSound) }
    var hasAudioPermission by remember { mutableStateOf(audioManager.hasAudioPermission()) }
    var showingAudioPermissionAlert by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text(stringResource(R.string.sound_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.sound_settings),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.configure_alert_sounds),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sound Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.alert_sound),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = stringResource(R.string.choose_alarm_sound),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Sound picker
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedSoundOption,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                SoundOption.getAllDisplayNames().forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedSoundOption = option
                                            preferencesManager.selectedSound = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Test/Enable button
                        Button(
                            onClick = {
                                if (hasAudioPermission) {
                                    audioManager.playAlertSound(SoundOption.fromDisplayName(selectedSoundOption))
                                } else {
                                    val success = audioManager.setupAudioSession()
                                    hasAudioPermission = success
                                    preferencesManager.hasAudioPermission = success
                                    if (!success) {
                                        showingAudioPermissionAlert = true
                                    } else {
                                        audioManager.playAlertSound(SoundOption.fromDisplayName(selectedSoundOption))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (hasAudioPermission) {
                                ButtonDefaults.buttonColors()
                            } else {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            }
                        ) {
                            Icon(
                                if (hasAudioPermission) Icons.Default.PlayArrow else Icons.AutoMirrored.Filled.VolumeOff,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (hasAudioPermission) stringResource(R.string.test_sound)
                                else stringResource(R.string.enable_audio)
                            )
                        }

                        if (!hasAudioPermission) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.audio_setup_message),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Audio Status
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (hasAudioPermission) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = if (hasAudioPermission)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.audio_status),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row {
                                Text(
                                    text = "Status:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(120.dp)
                                )
                                Text(
                                    text = if (hasAudioPermission) stringResource(R.string.status_ready)
                                    else stringResource(R.string.status_disabled),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (hasAudioPermission)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }

                            Row {
                                Text(
                                    text = "${stringResource(R.string.selected_sound)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(120.dp)
                                )
                                Text(
                                    text = selectedSoundOption,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row {
                                Text(
                                    text = "${stringResource(R.string.fallback)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(120.dp)
                                )
                                Text(
                                    text = stringResource(R.string.haptic_feedback),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Audio permission dialog
    if (showingAudioPermissionAlert) {
        AlertDialog(
            onDismissRequest = { showingAudioPermissionAlert = false },
            title = { Text(stringResource(R.string.audio_setup_required)) },
            text = { Text(stringResource(R.string.audio_setup_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showingAudioPermissionAlert = false
                        val success = audioManager.setupAudioSession()
                        hasAudioPermission = success
                        preferencesManager.hasAudioPermission = success
                    }
                ) {
                    Text(stringResource(R.string.try_again))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showingAudioPermissionAlert = false
                        hasAudioPermission = false
                        preferencesManager.hasAudioPermission = false
                    }
                ) {
                    Text(stringResource(R.string.continue_without_sound))
                }
            }
        )
    }
}
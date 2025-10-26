package org.tomw.cathrmdr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.tomw.cathrmdr.R
import org.tomw.cathrmdr.audio.CathAudioManager
import org.tomw.cathrmdr.data.PreferencesManager
import org.tomw.cathrmdr.data.SoundOption
import org.tomw.cathrmdr.notifications.CathNotificationManager
import org.tomw.cathrmdr.ui.theme.AppTheme
import org.tomw.cathrmdr.ui.theme.ThemeManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    themeManager: ThemeManager,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val preferencesManager = remember { PreferencesManager(context) }
    val audioManager = remember { CathAudioManager(context) }
    val notificationManager = remember { CathNotificationManager(context) }

    // Clean up audio resources when screen is disposed
    DisposableEffect(audioManager) {
        onDispose {
            audioManager.cleanup()
        }
    }

    var intervalText by remember { mutableStateOf(preferencesManager.intervalText) }
    var nextAlertDate by remember { mutableStateOf<Date?>(null) }
    var intervalSeconds by remember { mutableLongStateOf(0L) }
    var countdownText by remember { mutableStateOf("No alarm set") }
    var statusText by remember { mutableStateOf("Ready to set alarm") }
    var showingErrorAlert by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedSoundOption by remember { mutableStateOf(preferencesManager.selectedSound) }
    var hasAudioPermission by remember { mutableStateOf(audioManager.hasAudioPermission()) }

    // Timer to update countdown
    LaunchedEffect(Unit) {
        while (true) {
            updateCountdown(
                nextAlertDate = nextAlertDate,
                intervalSeconds = intervalSeconds,
                onCountdownUpdate = { text -> countdownText = text },
                onNextAlertUpdate = { date -> nextAlertDate = date },
                audioManager = audioManager,
                hasAudioPermission = hasAudioPermission
            )
            delay(1000)
        }
    }

    // Load initial state
    LaunchedEffect(Unit) {
        if (preferencesManager.isAlarmActive) {
            intervalSeconds = preferencesManager.intervalSeconds
            nextAlertDate = if (preferencesManager.nextAlertTime > 0) {
                Date(preferencesManager.nextAlertTime)
            } else null
            statusText = "Alarm active - repeats every $intervalText"
        }
    }

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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Section with Hamburger Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Input Section
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
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.alarm_interval),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = stringResource(R.string.interval_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = intervalText,
                            onValueChange = { intervalText = it },
                            placeholder = { Text(stringResource(R.string.interval_hint)) },
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.width(120.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    Button(
                        onClick = {
                            // Hide keyboard when button is pressed
                            focusManager.clearFocus()
                            keyboardController?.hide()

                            startButtonTapped(
                                intervalText = intervalText,
                                onError = { message ->
                                    errorMessage = message
                                    showingErrorAlert = true
                                },
                                onSuccess = { interval ->
                                    intervalSeconds = interval
                                    val now = System.currentTimeMillis()
                                    nextAlertDate = Date(now + interval * 1000)
                                    statusText = "Alarm active - repeats every $intervalText"

                                    // Schedule notifications
                                    notificationManager.scheduleRepeatingAlarm((interval / 60).toInt())

                                    // Save preferences
                                    preferencesManager.intervalText = intervalText
                                    preferencesManager.nextAlertTime = nextAlertDate?.time ?: 0
                                    preferencesManager.intervalSeconds = interval

                                    // Play confirmation sound
                                    if (hasAudioPermission) {
                                        audioManager.playAlertSound(SoundOption.fromDisplayName(selectedSoundOption))
                                    }
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            if (statusText.contains("active")) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (statusText.contains("active")) stringResource(R.string.update_alarm)
                            else stringResource(R.string.start_alarm)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Status Section
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
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.next_alarm),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = countdownText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (countdownText == "No alarm set")
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (statusText.contains("active")) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (statusText.contains("active"))
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (statusText.contains("active")) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (hasAudioPermission) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = null,
                                tint = if (hasAudioPermission)
                                    MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasAudioPermission) "Sound: $selectedSoundOption" else "Sound: Disabled",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Error dialog
    if (showingErrorAlert) {
        AlertDialog(
            onDismissRequest = { showingErrorAlert = false },
            title = { Text(stringResource(R.string.invalid_input)) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showingErrorAlert = false }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun parseTimeInterval(timeString: String): Long? {
    val components = timeString.split(":")
    if (components.size != 2) return null

    val hours = components[0].toIntOrNull() ?: return null
    val minutes = components[1].toIntOrNull() ?: return null

    if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) return null

    return (hours * 3600 + minutes * 60).toLong()
}

private fun startButtonTapped(
    intervalText: String,
    onError: (String) -> Unit,
    onSuccess: (Long) -> Unit
) {
    if (intervalText.isEmpty()) {
        onError("Please enter a valid time interval.")
        return
    }

    var interval = parseTimeInterval(intervalText)
    if (interval == null) {
        onError("Please enter time in HH:MM format (e.g., 4:00).")
        return
    }

    // Check if interval is less than 15 minutes (900 seconds)
    if (interval < 900) {
        onError("The minimum alert time is 15 minutes")
        interval = 900 // Set to 15 minutes
    }

    onSuccess(interval)
}

private fun updateCountdown(
    nextAlertDate: Date?,
    intervalSeconds: Long,
    onCountdownUpdate: (String) -> Unit,
    onNextAlertUpdate: (Date?) -> Unit,
    @Suppress("UNUSED_PARAMETER") audioManager: CathAudioManager,
    hasAudioPermission: Boolean
) {
    val nextAlert = nextAlertDate
    if (nextAlert == null || intervalSeconds <= 0) {
        onCountdownUpdate("No alarm set")
        return
    }

    val now = System.currentTimeMillis()
    val timeRemaining = nextAlert.time - now

    if (timeRemaining <= 0) {
        // Time to reschedule - move to next interval
        val newNextAlert = Date(now + intervalSeconds * 1000)
        onNextAlertUpdate(newNextAlert)

        // Play alert sound when countdown reaches zero
        if (hasAudioPermission) {
            // This would trigger the sound in a real implementation
        }
        return
    }

    val hours = (timeRemaining / 1000) / 3600
    val minutes = ((timeRemaining / 1000) % 3600) / 60
    val seconds = (timeRemaining / 1000) % 60

    onCountdownUpdate(String.format("%02d:%02d:%02d", hours, minutes, seconds))
}
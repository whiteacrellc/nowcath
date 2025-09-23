package org.tomw.cathnow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.tomw.cathnow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    onNavigateBack: () -> Unit
) {
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
                title = { Text(stringResource(R.string.privacy_policy)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.privacy_policy),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.white_acre_software),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Privacy Commitment
                PrivacyCard(
                    icon = Icons.Default.CheckCircle,
                    title = stringResource(R.string.privacy_commitment),
                    iconTint = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "White Acre Software LLC is committed to protecting your privacy and ensuring the security of your personal information. This privacy policy explains our data practices for the Cath Now application.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.effective_date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }

                // Data Collection
                PrivacyCard(
                    icon = Icons.Default.VisibilityOff,
                    title = stringResource(R.string.data_collection),
                    iconTint = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "White Acre Software LLC does not collect, store, or transmit any personal data or usage information from the Cath Now application.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPoint("No personal health information is collected")
                    BulletPoint("No usage analytics or tracking data is gathered")
                    BulletPoint("No device identifiers or personal identifiers are accessed")
                    BulletPoint("No network communications for data collection purposes")
                }

                // Local Storage
                PrivacyCard(
                    icon = Icons.Default.Storage,
                    title = stringResource(R.string.local_data_storage),
                    iconTint = MaterialTheme.colorScheme.tertiary
                ) {
                    Text(
                        text = "All application settings and preferences are stored locally on your device using Android standard secure storage mechanisms.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPoint("Alarm intervals and settings remain on your device only")
                    BulletPoint("Sound preferences are stored locally")
                    BulletPoint("No data synchronization or cloud storage")
                    BulletPoint("Data is removed when the application is deleted")
                }

                // System Permissions
                PrivacyCard(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.system_permissions),
                    iconTint = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = "The application requests only essential system permissions required for core functionality.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPoint("Notification permission: Required for medical reminders")
                    BulletPoint("Audio playback: Used only for local alarm sounds")
                    BulletPoint("No microphone access: Audio is playback only")
                    BulletPoint("No location, camera, or contact access requested")
                }

                // Third Party Services
                PrivacyCard(
                    icon = Icons.Default.NetworkWifi,
                    title = stringResource(R.string.third_party_services),
                    iconTint = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "The Cath Now application does not integrate with any third-party services, analytics platforms, or advertising networks.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPoint("No third-party SDKs or frameworks that collect data")
                    BulletPoint("No advertising networks or tracking pixels")
                    BulletPoint("No social media integrations")
                    BulletPoint("Fully offline operation capability")
                }

                // Medical Disclaimer
                PrivacyCard(
                    icon = Icons.Default.MedicalServices,
                    title = stringResource(R.string.medical_disclaimer),
                    iconTint = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = "The Cath Now application is designed as a reminder tool to assist with medical routine management.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BulletPoint("This application is not a medical device")
                    BulletPoint("Always consult healthcare professionals for medical advice")
                    BulletPoint("Application functionality depends on device operation")
                    BulletPoint("Users are responsible for following medical guidance")
                }

                // Contact Information
                PrivacyCard(
                    icon = Icons.Default.Email,
                    title = stringResource(R.string.contact_information),
                    iconTint = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "If you have questions about this privacy policy or our data practices, please contact White Acre Software at support@cathnow.com.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Company: White Acre Software LLC",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This privacy policy may be updated to reflect changes in our practices or legal requirements. The effective date will be updated accordingly.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }

                // Footer
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "© 2025 White Acre Software LLC",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Last Updated: September 2025",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This privacy policy governs the use of the Cath Now application",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun PrivacyCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconTint: androidx.compose.ui.graphics.Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            content()
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
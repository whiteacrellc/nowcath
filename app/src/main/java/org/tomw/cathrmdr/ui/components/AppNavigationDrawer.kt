package org.tomw.cathrmdr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.tomw.cathrmdr.R
import org.tomw.cathrmdr.ui.theme.AppTheme
import org.tomw.cathrmdr.ui.theme.ThemeManager

@Composable
fun AppNavigationDrawer(
    themeManager: ThemeManager,
    onNavigateToSounds: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Catheterization Reminders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Items
        DrawerNavigationItem(
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            label = stringResource(R.string.sound_settings),
            onClick = onNavigateToSounds
        )

        DrawerNavigationItem(
            icon = Icons.Default.Security,
            label = stringResource(R.string.privacy_policy),
            onClick = onNavigateToPrivacy
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        // Theme Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Theme Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            when (themeManager.currentTheme) {
                                AppTheme.LIGHT -> Icons.Default.LightMode
                                AppTheme.DARK -> Icons.Default.DarkMode
                                AppTheme.AUTO -> Icons.Default.Settings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Theme",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = themeManager.currentTheme.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { themeManager.cycleTheme() },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "White Acre Software LLC",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "© 2025 • Version 1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DrawerNavigationItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.surface,
            unselectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

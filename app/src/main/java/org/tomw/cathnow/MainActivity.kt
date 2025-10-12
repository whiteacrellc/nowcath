package org.tomw.cathnow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.tomw.cathnow.ui.components.MainScreen
import org.tomw.cathnow.ui.components.PrivacyScreen
import org.tomw.cathnow.ui.components.SoundSettingsScreen
import org.tomw.cathnow.ui.theme.AppTheme
import org.tomw.cathnow.ui.theme.CathNowTheme
import org.tomw.cathnow.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {

    private lateinit var themeManager: ThemeManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeManager = ThemeManager(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {

            // Observe theme changes
            LaunchedEffect(themeManager.currentTheme) {
                // This will recompose when theme changes
            }

            CathNowTheme(appTheme = themeManager.currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CathNowApp(themeManager)
                }
            }
        }
    }
}

@Composable
fun CathNowApp(themeManager: ThemeManager) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                themeManager = themeManager,
                onNavigateToSounds = {
                    navController.navigate("sounds")
                },
                onNavigateToPrivacy = {
                    navController.navigate("privacy")
                }
            )
        }

        composable("sounds") {
            SoundSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("privacy") {
            PrivacyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
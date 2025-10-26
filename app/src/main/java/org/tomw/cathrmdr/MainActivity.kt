package org.tomw.cathrmdr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.tomw.cathrmdr.ui.components.AppNavigationDrawer
import org.tomw.cathrmdr.ui.components.MainScreen
import org.tomw.cathrmdr.ui.components.PrivacyScreen
import org.tomw.cathrmdr.ui.components.SoundSettingsScreen
import org.tomw.cathrmdr.ui.theme.CathRmdrTheme
import org.tomw.cathrmdr.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {

    private lateinit var themeManager: ThemeManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
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

            CathRmdrTheme(appTheme = themeManager.currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CathRmdrApp(themeManager)
                }
            }
        }
    }
}

@Composable
fun CathRmdrApp(themeManager: ThemeManager) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                themeManager = themeManager,
                onNavigateToSounds = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("sounds")
                    }
                },
                onNavigateToPrivacy = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("privacy")
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                MainScreen(
                    themeManager = themeManager,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
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
}
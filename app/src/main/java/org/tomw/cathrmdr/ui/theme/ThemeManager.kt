package org.tomw.cathrmdr.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class AppTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    AUTO("Auto")
}

class ThemeManager(context: Context) : ViewModel() {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)

    var currentTheme by mutableStateOf(
        AppTheme.values().find { it.name == prefs.getString("theme", AppTheme.DARK.name) } ?: AppTheme.DARK
    )
        private set

    fun setTheme(theme: AppTheme) {
        currentTheme = theme
        prefs.edit().putString("theme", theme.name).apply()
    }

    fun cycleTheme() {
        val themes = AppTheme.values()
        val currentIndex = themes.indexOf(currentTheme)
        val nextIndex = (currentIndex + 1) % themes.size
        setTheme(themes[nextIndex])
    }
}
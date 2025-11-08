package org.tomw.cathrmdr.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.tomw.cathrmdr.data.PreferencesManager

enum class AppTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    AUTO("Auto")
}

class ThemeManager(context: Context) {
    private val preferencesManager = PreferencesManager(context)

    var currentTheme by mutableStateOf(preferencesManager.theme)
        private set

    fun setTheme(theme: AppTheme) {
        currentTheme = theme
        preferencesManager.theme = theme
    }

    fun cycleTheme() {
        val themes = AppTheme.values()
        val currentIndex = themes.indexOf(currentTheme)
        val nextIndex = (currentIndex + 1) % themes.size
        setTheme(themes[nextIndex])
    }
}
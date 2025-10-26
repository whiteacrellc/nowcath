package org.tomw.cathrmdr.data

import android.content.Context
import android.content.SharedPreferences
import org.tomw.cathrmdr.ui.theme.AppTheme

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cath_now_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_INTERVAL_TEXT = "interval_text"
        private const val KEY_SELECTED_SOUND = "selected_sound"
        private const val KEY_HAS_AUDIO_PERMISSION = "has_audio_permission"
        private const val KEY_ALARM_ACTIVE = "alarm_active"
        private const val KEY_NEXT_ALERT_TIME = "next_alert_time"
        private const val KEY_INTERVAL_SECONDS = "interval_seconds"
        private const val KEY_THEME = "theme"
    }

    var intervalText: String
        get() = prefs.getString(KEY_INTERVAL_TEXT, "4:00") ?: "4:00"
        set(value) = prefs.edit().putString(KEY_INTERVAL_TEXT, value).apply()

    var selectedSound: String
        get() = prefs.getString(KEY_SELECTED_SOUND, SoundOption.ALARM_1.displayName) ?: SoundOption.ALARM_1.displayName
        set(value) = prefs.edit().putString(KEY_SELECTED_SOUND, value).apply()

    var hasAudioPermission: Boolean
        get() = prefs.getBoolean(KEY_HAS_AUDIO_PERMISSION, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_AUDIO_PERMISSION, value).apply()

    var isAlarmActive: Boolean
        get() = prefs.getBoolean(KEY_ALARM_ACTIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_ALARM_ACTIVE, value).apply()

    var nextAlertTime: Long
        get() = prefs.getLong(KEY_NEXT_ALERT_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_NEXT_ALERT_TIME, value).apply()

    var intervalSeconds: Long
        get() = prefs.getLong(KEY_INTERVAL_SECONDS, 0L)
        set(value) = prefs.edit().putLong(KEY_INTERVAL_SECONDS, value).apply()

    var theme: AppTheme
        get() = AppTheme.values().find { it.name == prefs.getString(KEY_THEME, AppTheme.DARK.name) } ?: AppTheme.DARK
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    fun clearAlarmData() {
        prefs.edit()
            .putBoolean(KEY_ALARM_ACTIVE, false)
            .putLong(KEY_NEXT_ALERT_TIME, 0L)
            .putLong(KEY_INTERVAL_SECONDS, 0L)
            .apply()
    }
}
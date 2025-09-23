package org.tomw.cathnow.data

import org.tomw.cathnow.R

enum class SoundOption(val displayName: String, val soundId: Int? = null) {
    ALARM_1("Alarm 1", R.raw.alarm1),
    ALARM_2("Alarm 2", R.raw.alarm2),
    ALARM_3("Alarm 3", R.raw.alarm3),
    ALARM_4("Alarm 4", R.raw.alarm4);

    companion object {
        fun fromDisplayName(name: String): SoundOption {
            return entries.find { ent -> ent.displayName == name } ?: ALARM_1
        }

        fun getAllDisplayNames(): List<String> {
            return entries.map { ent -> ent.displayName }
        }
    }
}
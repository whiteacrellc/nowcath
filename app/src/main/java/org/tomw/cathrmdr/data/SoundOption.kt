package org.tomw.cathrmdr.data

import org.tomw.cathrmdr.R

enum class SoundOption(val displayName: String, val soundId: Int? = null) {
    ALARM_1("The Tick", R.raw.alarm1),
    ALARM_2("Alert Sound", R.raw.alarm2),
    ALARM_3("Klaxon", R.raw.alarm3),
    ALARM_4("Retro Alarm", R.raw.alarm4);

    companion object {
        fun fromDisplayName(name: String): SoundOption {
            return entries.find { ent -> ent.displayName == name } ?: ALARM_1
        }

        fun getAllDisplayNames(): List<String> {
            return entries.map { ent -> ent.displayName }
        }
    }
}
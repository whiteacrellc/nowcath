package org.tomw.cathrmdr.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.tomw.cathrmdr.data.PreferencesManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                restartAlarms(context)
            }
        }
    }

    private fun restartAlarms(context: Context) {
        val preferencesManager = PreferencesManager(context)

        // Check if alarms were active before reboot
        if (preferencesManager.isAlarmActive && preferencesManager.intervalSeconds > 0) {
            val notificationManager = CathNotificationManager(context)
            val intervalMinutes = (preferencesManager.intervalSeconds / 60).toInt()

            // Reschedule the alarm
            notificationManager.scheduleRepeatingAlarm(intervalMinutes)
        }
    }
}
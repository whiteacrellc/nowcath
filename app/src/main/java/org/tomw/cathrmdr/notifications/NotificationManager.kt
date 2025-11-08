package org.tomw.cathrmdr.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.style.StyleSpan
import android.graphics.Typeface
import androidx.core.app.NotificationCompat
import androidx.work.*
import org.tomw.cathrmdr.MainActivity
import org.tomw.cathrmdr.R
import org.tomw.cathrmdr.data.PreferencesManager
import org.tomw.cathrmdr.data.SoundOption
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri
import android.content.BroadcastReceiver

class CathNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "cath_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_TAG = "cath_reminder_work"
        const val ALARM_ACTION = "org.tomw.cathrmdr.ALARM_TRIGGERED"
        const val ALARM_REQUEST_CODE = 1002
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val preferencesManager = PreferencesManager(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_description)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
                setSound(getNotificationSound(), AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationSound(): Uri? {
        val selectedSound = SoundOption.fromDisplayName(preferencesManager.selectedSound)
        return selectedSound.soundId?.let { soundResourceId ->
            "android.resource://${context.packageName}/$soundResourceId".toUri()
        } ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }

    fun scheduleRepeatingAlarm(intervalMinutes: Int) {
        cancelAllAlarms()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis = intervalMinutes * 60 * 1000L
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis

        // Use setRepeating for exact repeating alarms (requires SCHEDULE_EXACT_ALARM permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12+, check if we can schedule exact alarms
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    intervalMillis,
                    pendingIntent
                )
            } else {
                // Fallback to inexact repeating
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    intervalMillis,
                    pendingIntent
                )
            }
        } else {
            // For older Android versions, use setRepeating
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                intervalMillis,
                pendingIntent
            )
        }

        // Update preferences
        preferencesManager.isAlarmActive = true
        preferencesManager.intervalSeconds = (intervalMinutes * 60).toLong()
        preferencesManager.nextAlertTime = triggerAtMillis
    }

    fun cancelAllAlarms() {
        // Cancel AlarmManager alarms
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        // Cancel WorkManager tasks (for backwards compatibility)
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)

        // Cancel any shown notifications
        notificationManager.cancel(NOTIFICATION_ID)

        // Clear preferences
        preferencesManager.clearAlarmData()
    }

    fun showTestNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Make title and text bold
        val boldTitle = SpannableString(context.getString(R.string.cath_now_notification_title)).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
        }
        val boldText = SpannableString(context.getString(R.string.cath_now_notification_body)).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cross_red)
            .setContentTitle(boldTitle)
            .setContentText(boldText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(false)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, false)
            .setSound(getNotificationSound())
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CathNotificationManager.ALARM_ACTION) {
            val notificationManager = CathNotificationManager(context)
            val preferencesManager = PreferencesManager(context)

            // Show the notification
            notificationManager.showTestNotification()

            // Update next alert time for UI
            val intervalMs = preferencesManager.intervalSeconds * 1000
            preferencesManager.nextAlertTime = System.currentTimeMillis() + intervalMs
        }
    }
}

// Keep AlarmWorker for backwards compatibility with existing WorkManager tasks
class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val notificationManager = CathNotificationManager(applicationContext)
            val preferencesManager = PreferencesManager(applicationContext)

            // Show the notification
            notificationManager.showTestNotification()

            // Update next alert time
            val intervalMs = preferencesManager.intervalSeconds * 1000
            preferencesManager.nextAlertTime = System.currentTimeMillis() + intervalMs

            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            Result.retry()
        }
    }
}
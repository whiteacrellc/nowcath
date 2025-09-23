package org.tomw.cathnow.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import org.tomw.cathnow.MainActivity
import org.tomw.cathnow.R
import org.tomw.cathnow.data.PreferencesManager
import org.tomw.cathnow.data.SoundOption
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

class CathNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "cath_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_TAG = "cath_reminder_work"
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
        return when (selectedSound) {
            SoundOption.ALARM_1, SoundOption.ALARM_2 -> {
                // Try to use custom sound from assets or raw folder
                "android.resource://${context.packageName}/${R.raw.alarm1}".toUri()
            }
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
    }

    fun scheduleRepeatingAlarm(intervalMinutes: Int) {
        cancelAllAlarms()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val alarmWork = PeriodicWorkRequestBuilder<AlarmWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            alarmWork
        )

        // Update preferences
        preferencesManager.isAlarmActive = true
        preferencesManager.intervalSeconds = (intervalMinutes * 60).toLong()
        preferencesManager.nextAlertTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000)
    }

    fun cancelAllAlarms() {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
        notificationManager.cancel(NOTIFICATION_ID)
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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.cath_now_notification_title))
            .setContentText(context.getString(R.string.cath_now_notification_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(getNotificationSound())
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

class AlarmWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val notificationManager = CathNotificationManager(applicationContext)
        notificationManager.showTestNotification()
        return Result.success()
    }
}
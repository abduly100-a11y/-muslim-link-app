package com.mosque.community.util.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mosque.community.MainActivity
import com.mosque.community.R

object NotificationHelper {

    const val CHANNEL_PRAYER = "channel_prayer_alerts"
    const val CHANNEL_EVENTS = "channel_event_reminders"

    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_TARGET_URL = "extra_target_url"

    fun createNotificationChannels(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

                val prayerChannel = NotificationChannel(
                    CHANNEL_PRAYER,
                    "Prayer Times & Iqamah Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifies you prior to Adhan and congregational Iqamah times"
                    enableVibration(true)
                }

                val eventChannel = NotificationChannel(
                    CHANNEL_EVENTS,
                    "Community Event Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders for lectures, halaqas, and programs you registered for"
                }

                notificationManager.createNotificationChannel(prayerChannel)
                notificationManager.createNotificationChannel(eventChannel)
            }
        } catch (t: Throwable) {
            // Non-fatal, notification channel creation can fail silently if restricted by OEM
        }
    }

    /**
     * Schedules an exact alarm to notify the user prior to an event.
     */
    fun scheduleEventReminder(
        context: Context,
        eventId: String,
        eventTitle: String,
        mosqueName: String,
        eventTimeEpochMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 15 minutes before the event start
        val triggerTime = (eventTimeEpochMillis - 15 * 60 * 1000).coerceAtLeast(System.currentTimeMillis() + 5000)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, eventId.hashCode())
            putExtra(EXTRA_TITLE, "Upcoming Event Reminder: $eventTitle")
            putExtra(EXTRA_MESSAGE, "Starting in 15 minutes at $mosqueName. Tap to view directions and details.")
            putExtra(EXTRA_TARGET_URL, "https://muslimlink.app/event/$eventId")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            // If SCHEDULE_EXACT_ALARM is restricted, fallback to standard alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    /**
     * Cancels an existing scheduled reminder by eventId.
     */
    fun cancelEventReminder(context: Context, eventId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}

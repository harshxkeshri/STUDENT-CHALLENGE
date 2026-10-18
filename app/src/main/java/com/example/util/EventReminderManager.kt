package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.*

class EventReminderReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "campus_event_reminders"
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_TITLE = "extra_event_title"
        const val EXTRA_EVENT_VENUE = "extra_event_venue"
        const val EXTRA_EVENT_TIME = "extra_event_time"
        private const val TAG = "EventReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return
        val title = intent.getStringExtra(EXTRA_EVENT_TITLE) ?: "Upcoming Event"
        val venue = intent.getStringExtra(EXTRA_EVENT_VENUE) ?: "Campus"
        val time = intent.getStringExtra(EXTRA_EVENT_TIME) ?: "Soon"

        Log.i(TAG, "Triggering event notification for: $title at $venue")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "events")
            putExtra("event_id", eventId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mirai_logo)
            .setContentTitle("Event Reminder: $title")
            .setContentText("Starting at $time in $venue. Have your pass ready!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Campus event '$title' starts at $time at $venue. Check-in and duty leave eligibility are active.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(eventId.hashCode(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Reminders"
            val descriptionText = "Notifications alerting you before campus club events start"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}

class EventReminderManager(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "event_reminders_prefs"
        private const val TAG = "EventReminderMgr"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isReminderSet(eventId: String): Boolean {
        return prefs.getBoolean("reminder_enabled_$eventId", false)
    }

    fun getSavedReminderTime(eventId: String): Long {
        return prefs.getLong("reminder_time_$eventId", 0L)
    }

    fun getSavedReminderLabel(eventId: String): String {
        return prefs.getString("reminder_label_$eventId", "") ?: ""
    }

    /**
     * Schedules a local notification alert for the event and persists reminder time locally
     */
    fun scheduleReminder(
        eventId: String,
        eventTitle: String,
        venue: String,
        eventDate: String,
        eventTime: String,
        minutesBefore: Int = 30
    ): Long {
        val targetMillis = calculateAlertTimeMillis(eventDate, eventTime, minutesBefore)

        val intent = Intent(context, EventReminderReceiver::class.java).apply {
            putExtra(EventReminderReceiver.EXTRA_EVENT_ID, eventId)
            putExtra(EventReminderReceiver.EXTRA_EVENT_TITLE, eventTitle)
            putExtra(EventReminderReceiver.EXTRA_EVENT_VENUE, venue)
            putExtra(EventReminderReceiver.EXTRA_EVENT_TIME, eventTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        try {
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        targetMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm permission restricted, falling back to standard alarm: ${e.message}")
            alarmManager?.set(AlarmManager.RTC_WAKEUP, targetMillis, pendingIntent)
        }

        val label = if (minutesBefore >= 60) "${minutesBefore / 60}h before" else "${minutesBefore}m before"
        prefs.edit()
            .putBoolean("reminder_enabled_$eventId", true)
            .putLong("reminder_time_$eventId", targetMillis)
            .putString("reminder_label_$eventId", label)
            .apply()

        Log.i(TAG, "Scheduled reminder for $eventTitle at $targetMillis ($label)")
        return targetMillis
    }

    /**
     * Cancels the scheduled notification and clears local saved reminder time
     */
    fun cancelReminder(eventId: String) {
        val intent = Intent(context, EventReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.cancel(pendingIntent)

        prefs.edit()
            .remove("reminder_enabled_$eventId")
            .remove("reminder_time_$eventId")
            .remove("reminder_label_$eventId")
            .apply()

        Log.i(TAG, "Cancelled reminder for event $eventId")
    }

    private fun calculateAlertTimeMillis(dateStr: String, timeStr: String, minutesBefore: Int): Long {
        // Try parsing various date formats
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        val parsed = parseDateTime(dateStr, timeStr)
        if (parsed != null && parsed.timeInMillis > now) {
            val alertTime = parsed.timeInMillis - (minutesBefore * 60 * 1000L)
            return if (alertTime > now) alertTime else (now + 5000L)
        }

        // Fallback: Default to 15 minutes from now if event date cannot be parsed or is in past
        return now + (15 * 60 * 1000L)
    }

    private fun parseDateTime(dateStr: String, timeStr: String): Calendar? {
        val patterns = listOf(
            "yyyy-MM-dd hh:mm a",
            "yyyy-MM-dd HH:mm",
            "EEEE, MMM d, yyyy hh:mm a",
            "MMM d, yyyy hh:mm a"
        )

        // Clean time string (e.g. "10:00 AM - 1:00 PM" -> "10:00 AM")
        val cleanTime = timeStr.split("-").first().trim()
        val combined = "$dateStr $cleanTime"

        for (pat in patterns) {
            try {
                val sdf = SimpleDateFormat(pat, Locale.getDefault())
                val d = sdf.parse(combined)
                if (d != null) {
                    val cal = Calendar.getInstance()
                    cal.time = d
                    return cal
                }
            } catch (e: Exception) {
                // Try next pattern
            }
        }
        return null
    }
}

package com.mosque.community.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import com.mosque.community.data.models.Event

object CalendarUtils {

    /**
     * Dispatches an Intent.ACTION_INSERT to the device's calendar application
     * (e.g., Google Calendar, Samsung Calendar, Outlook) with prefilled event details.
     * Requires no sensitive runtime calendar permissions.
     */
    fun addToDeviceCalendar(context: Context, event: Event) {
        val fullLocation = if (event.address.isNotBlank()) {
            "${event.location}, ${event.address}"
        } else {
            "${event.location}, ${event.mosqueName}"
        }

        val descriptionText = buildString {
            append(event.description)
            append("\n\n")
            append("Host: ").append(event.mosqueName)
            if (event.speakerNames.isNotEmpty()) {
                append("\nSpeaker: ").append(event.speakerNames.joinToString(", "))
            }
            append("\nDetails & RSVP: ").append(event.shareUrl)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, descriptionText)
            putExtra(CalendarContract.Events.EVENT_LOCATION, fullLocation)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.timestampEpochMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTimestampEpochMillis)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No calendar application installed", Toast.LENGTH_SHORT).show()
        }
    }
}

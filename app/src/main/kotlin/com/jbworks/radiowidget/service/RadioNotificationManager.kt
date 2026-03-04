package com.jbworks.radiowidget.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jbworks.radiowidget.R
import com.jbworks.radiowidget.model.RadioStation

/**
 * Manages notifications for radio playback
 */
class RadioNotificationManager(private val context: Context) {
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "radio_playback_channel"
        private const val CHANNEL_NAME = "Radio Playback"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Controls for radio playback"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Build a notification for the playing station
     */
    fun buildNotification(station: RadioStation): android.app.Notification {
        val stopIntent = Intent(context, RadioPlaybackService::class.java).apply {
            action = RadioPlaybackService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(station.name)
            .setContentText(station.frequency)
            .setSmallIcon(R.drawable.ic_radio)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_stop, context.getString(R.string.stop), stopPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0))
            .build()
    }

    /**
     * Cancel the notification
     */
    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}

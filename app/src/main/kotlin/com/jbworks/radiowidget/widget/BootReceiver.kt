package com.jbworks.radiowidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jbworks.radiowidget.model.RadioPreferences
import com.jbworks.radiowidget.service.RadioPlaybackService

/**
 * Handles device boot to restore playback state if needed
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val autoPlay = RadioPreferences.getAutoPlay(context)
            val playingStationId = RadioPreferences.getPlayingStation(context)

            if (autoPlay && playingStationId != null) {
                // Resume playback after boot
                val serviceIntent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = RadioPlaybackService.ACTION_PLAY
                    putExtra(RadioPlaybackService.EXTRA_STATION_ID, playingStationId)
                }
                context.startForegroundService(serviceIntent)
            }
        }
    }
}

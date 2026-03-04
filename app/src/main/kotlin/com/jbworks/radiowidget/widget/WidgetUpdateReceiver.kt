package com.jbworks.radiowidget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jbworks.radiowidget.service.RadioPlaybackService

/**
 * Receives broadcasts from RadioPlaybackService to update widgets
 */
class WidgetUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == RadioPlaybackService.BROADCAST_PLAYBACK_STATE) {
            // Update all widgets when playback state changes
            RadioWidget.updateAllWidgets(context)
        }
    }
}

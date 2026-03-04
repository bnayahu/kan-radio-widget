package com.jbworks.radiowidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.jbworks.radiowidget.R
import com.jbworks.radiowidget.model.PlaybackState
import com.jbworks.radiowidget.model.RadioPreferences
import com.jbworks.radiowidget.model.StationManager
import com.jbworks.radiowidget.service.RadioPlaybackService

/**
 * Main widget provider for radio widget
 */
class RadioWidget : AppWidgetProvider() {
    companion object {
        private const val ACTION_PLAY_STOP = "com.jbworks.radiowidget.widget.PLAY_STOP"
        private const val EXTRA_WIDGET_ID = "widget_id"

        /**
         * Update all widget instances
         */
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, RadioWidget::class.java)
            )
            widgetIds.forEach { widgetId ->
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }

        /**
         * Update a single widget instance
         */
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetStationId = RadioPreferences.getWidgetStation(context, appWidgetId)
            val station = StationManager.getStationById(widgetStationId)
                ?: StationManager.getDefaultStation()

            val playingStationId = RadioPreferences.getPlayingStation(context)
            val playbackState = RadioPreferences.getPlaybackState(context)

            val isThisStationPlaying = widgetStationId == playingStationId &&
                    (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.BUFFERING)

            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                // Set station info
                setTextViewText(R.id.station_name, station.name)
                setTextViewText(R.id.station_frequency, station.frequency)

                // Set play/stop button icon
                if (isThisStationPlaying) {
                    setImageViewResource(R.id.play_stop_button, R.drawable.ic_stop)
                    setContentDescription(R.id.play_stop_button, context.getString(R.string.stop))
                } else {
                    setImageViewResource(R.id.play_stop_button, R.drawable.ic_play)
                    setContentDescription(R.id.play_stop_button, context.getString(R.string.play))
                }

                // Set up button intent
                val playStopIntent = Intent(context, RadioWidget::class.java).apply {
                    action = ACTION_PLAY_STOP
                    putExtra(EXTRA_WIDGET_ID, appWidgetId)
                }
                val playStopPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    playStopIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                setOnClickPendingIntent(R.id.play_stop_button, playStopPendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_PLAY_STOP -> {
                val widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                handlePlayStop(context, widgetId)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            RadioPreferences.removeWidgetPrefs(context, widgetId)
        }
    }

    private fun handlePlayStop(context: Context, widgetId: Int) {
        val widgetStationId = RadioPreferences.getWidgetStation(context, widgetId)
        val playingStationId = RadioPreferences.getPlayingStation(context)
        val playbackState = RadioPreferences.getPlaybackState(context)

        // If this widget's station is currently playing, stop it
        if (widgetStationId == playingStationId &&
            (playbackState == PlaybackState.PLAYING || playbackState == PlaybackState.BUFFERING)) {
            val intent = Intent(context, RadioPlaybackService::class.java).apply {
                action = RadioPlaybackService.ACTION_STOP
            }
            context.startForegroundService(intent)

            // Optimistically update state
            RadioPreferences.setPlayingStation(context, null)
            RadioPreferences.setPlaybackState(context, PlaybackState.STOPPED)
            updateAllWidgets(context)
        } else {
            // Play this widget's station
            widgetStationId?.let { stationId ->
                val intent = Intent(context, RadioPlaybackService::class.java).apply {
                    action = RadioPlaybackService.ACTION_PLAY
                    putExtra(RadioPlaybackService.EXTRA_STATION_ID, stationId)
                }
                context.startForegroundService(intent)

                // Optimistically update state
                RadioPreferences.setPlayingStation(context, stationId)
                RadioPreferences.setPlaybackState(context, PlaybackState.BUFFERING)
                updateAllWidgets(context)
            }
        }
    }
}

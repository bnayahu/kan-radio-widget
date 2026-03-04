package com.jbworks.radiowidget.model

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages SharedPreferences for widget and playback state
 */
object RadioPreferences {
    private const val PREFS_NAME = "RadioWidgetPrefs"
    private const val KEY_WIDGET_STATION_PREFIX = "widget_station_"
    private const val KEY_GLOBAL_PLAYING_STATION = "global_playing_station"
    private const val KEY_GLOBAL_PLAYBACK_STATE = "global_playback_state"
    private const val KEY_AUTO_PLAY = "auto_play"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save the station ID for a specific widget instance
     */
    fun setWidgetStation(context: Context, widgetId: Int, stationId: String) {
        getPrefs(context).edit()
            .putString(KEY_WIDGET_STATION_PREFIX + widgetId, stationId)
            .apply()
    }

    /**
     * Get the station ID for a specific widget instance
     */
    fun getWidgetStation(context: Context, widgetId: Int): String? {
        return getPrefs(context).getString(KEY_WIDGET_STATION_PREFIX + widgetId, null)
    }

    /**
     * Remove preferences for a specific widget instance
     */
    fun removeWidgetPrefs(context: Context, widgetId: Int) {
        getPrefs(context).edit()
            .remove(KEY_WIDGET_STATION_PREFIX + widgetId)
            .apply()
    }

    /**
     * Set the currently playing station (global state)
     */
    fun setPlayingStation(context: Context, stationId: String?) {
        getPrefs(context).edit()
            .putString(KEY_GLOBAL_PLAYING_STATION, stationId)
            .apply()
    }

    /**
     * Get the currently playing station (global state)
     */
    fun getPlayingStation(context: Context): String? {
        return getPrefs(context).getString(KEY_GLOBAL_PLAYING_STATION, null)
    }

    /**
     * Set the global playback state
     */
    fun setPlaybackState(context: Context, state: PlaybackState) {
        getPrefs(context).edit()
            .putString(KEY_GLOBAL_PLAYBACK_STATE, state.name)
            .apply()
    }

    /**
     * Get the global playback state
     */
    fun getPlaybackState(context: Context): PlaybackState {
        val stateName = getPrefs(context).getString(KEY_GLOBAL_PLAYBACK_STATE, PlaybackState.STOPPED.name)
        return try {
            PlaybackState.valueOf(stateName ?: PlaybackState.STOPPED.name)
        } catch (e: IllegalArgumentException) {
            PlaybackState.STOPPED
        }
    }

    /**
     * Set auto-play preference
     */
    fun setAutoPlay(context: Context, enabled: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_AUTO_PLAY, enabled)
            .apply()
    }

    /**
     * Get auto-play preference
     */
    fun getAutoPlay(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_PLAY, false)
    }
}

package com.jbworks.radiowidget.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.jbworks.radiowidget.model.PlaybackState
import com.jbworks.radiowidget.model.RadioPreferences
import com.jbworks.radiowidget.model.RadioStation
import com.jbworks.radiowidget.model.StationManager

/**
 * Foreground service for radio playback
 */
class RadioPlaybackService : LifecycleService(), RadioPlayer.PlayerCallback {
    companion object {
        const val ACTION_PLAY = "com.jbworks.radiowidget.action.PLAY"
        const val ACTION_STOP = "com.jbworks.radiowidget.action.STOP"
        const val EXTRA_STATION_ID = "station_id"

        const val BROADCAST_PLAYBACK_STATE = "com.jbworks.radiowidget.PLAYBACK_STATE"
        const val EXTRA_STATE = "state"
        const val EXTRA_PLAYING_STATION_ID = "playing_station_id"
    }

    private lateinit var radioPlayer: RadioPlayer
    private lateinit var notificationManager: RadioNotificationManager
    private var currentStation: RadioStation? = null
    private var currentState: PlaybackState = PlaybackState.STOPPED

    override fun onCreate() {
        super.onCreate()
        radioPlayer = RadioPlayer(this, this)
        notificationManager = RadioNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_PLAY -> {
                val stationId = intent.getStringExtra(EXTRA_STATION_ID)
                val station = StationManager.getStationById(stationId)
                if (station != null) {
                    playStation(station)
                }
            }
            ACTION_STOP -> {
                stopPlayback()
            }
        }

        return START_STICKY
    }

    private fun playStation(station: RadioStation) {
        currentStation = station
        radioPlayer.play(station)
        RadioPreferences.setPlayingStation(this, station.id)
        startForeground(RadioNotificationManager.NOTIFICATION_ID,
            notificationManager.buildNotification(station))
    }

    private fun stopPlayback() {
        radioPlayer.stop()
        RadioPreferences.setPlayingStation(this, null)
        currentStation = null
        notificationManager.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onStateChanged(state: PlaybackState) {
        currentState = state
        RadioPreferences.setPlaybackState(this, state)

        // Update notification based on state
        currentStation?.let { station ->
            when (state) {
                PlaybackState.PLAYING, PlaybackState.BUFFERING -> {
                    startForeground(RadioNotificationManager.NOTIFICATION_ID,
                        notificationManager.buildNotification(station))
                }
                PlaybackState.ERROR, PlaybackState.STOPPED -> {
                    // Handled by stopPlayback()
                }
            }
        }

        // Broadcast state change to widgets
        broadcastStateChange()
    }

    private fun broadcastStateChange() {
        val intent = Intent(BROADCAST_PLAYBACK_STATE).apply {
            putExtra(EXTRA_STATE, currentState.name)
            putExtra(EXTRA_PLAYING_STATION_ID, currentStation?.id)
            // Make the broadcast explicit for Android 8.0+
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        radioPlayer.release()
        notificationManager.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}

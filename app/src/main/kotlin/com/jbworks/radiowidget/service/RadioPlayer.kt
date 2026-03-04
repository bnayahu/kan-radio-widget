package com.jbworks.radiowidget.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import com.jbworks.radiowidget.model.PlaybackState
import com.jbworks.radiowidget.model.RadioStation

/**
 * Wrapper around ExoPlayer for HLS radio streaming
 */
class RadioPlayer(
    private val context: Context,
    private val callback: PlayerCallback
) {
    private var exoPlayer: ExoPlayer? = null
    private var currentStation: RadioStation? = null

    interface PlayerCallback {
        fun onStateChanged(state: PlaybackState)
    }

    /**
     * Initialize the player
     */
    fun initialize() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> callback.onStateChanged(PlaybackState.BUFFERING)
                            Player.STATE_READY -> {
                                if (isPlaying) {
                                    callback.onStateChanged(PlaybackState.PLAYING)
                                }
                            }
                            Player.STATE_ENDED -> callback.onStateChanged(PlaybackState.STOPPED)
                            Player.STATE_IDLE -> callback.onStateChanged(PlaybackState.STOPPED)
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            callback.onStateChanged(PlaybackState.PLAYING)
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        callback.onStateChanged(PlaybackState.ERROR)
                    }
                })
            }
        }
    }

    /**
     * Play a radio station
     */
    fun play(station: RadioStation) {
        initialize()
        currentStation = station

        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(station.streamUrl))

        exoPlayer?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    /**
     * Pause playback
     */
    fun pause() {
        exoPlayer?.playWhenReady = false
    }

    /**
     * Resume playback
     */
    fun resume() {
        exoPlayer?.playWhenReady = true
    }

    /**
     * Stop playback
     */
    fun stop() {
        exoPlayer?.apply {
            stop()
            clearMediaItems()
        }
        currentStation = null
        callback.onStateChanged(PlaybackState.STOPPED)
    }

    /**
     * Check if player is currently playing
     */
    fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    /**
     * Get the current station
     */
    fun getCurrentStation(): RadioStation? = currentStation

    /**
     * Release the player resources
     */
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        currentStation = null
    }
}

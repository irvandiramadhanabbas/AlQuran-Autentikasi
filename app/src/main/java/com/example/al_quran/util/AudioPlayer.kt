package com.example.al_quran.util

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

object AudioPlayer {
    private const val TAG = "AudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private var _playbackState: Boolean = false
    private var _lastKnownPosition: Int = 0

    private var completionCallback: (() -> Unit)? = null

    val isPlaying: Boolean get() = _playbackState
    val currentPosition: Int
        get() = mediaPlayer?.currentPosition ?: _lastKnownPosition

    fun play(
        url: String,
        onPrepared: (() -> Unit)? = null,
        onCompletion: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            stop()
            completionCallback = onCompletion

            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState = true
                    _lastKnownPosition = 0
                    onPrepared?.invoke()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Playback error - what: $what, extra: $extra")
                    onError?.invoke("Failed to play audio")
                    stop()
                    true
                }

                setOnCompletionListener {
                    _playbackState = false
                    completionCallback?.invoke()
                }
            }
        } catch (e: IllegalStateException) {
            handleError("Invalid player state", e, onError)
        } catch (e: IOException) {
            handleError("Audio file not found", e, onError)
        } catch (e: Exception) {
            handleError("Unknown error occurred", e, onError)
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                _lastKnownPosition = player.currentPosition
                player.pause()
                _playbackState = false
            }
        }
    }

    fun resume() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.seekTo(_lastKnownPosition)
                player.start()
                _playbackState = true
            }
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error stopping player: ${e.message}")
            } finally {
                mediaPlayer = null
                _playbackState = false
                _lastKnownPosition = 0
            }
        }
    }

    private fun handleError(message: String, e: Exception, onError: ((String) -> Unit)?) {
        Log.e(TAG, "$message: ${e.message}")
        onError?.invoke(message)
        stop()
    }
}

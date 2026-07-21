package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val speed: Float = 1.0f,
    val currentFilePath: String? = null
)

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    fun playFile(file: File, speed: Float = 1.0f) {
        playPath(file.absolutePath, speed)
    }

    fun playPath(filePath: String, speed: Float = 1.0f) {
        try {
            if (_playbackState.value.currentFilePath == filePath && mediaPlayer != null) {
                if (mediaPlayer?.isPlaying == true) {
                    pause()
                    return
                } else {
                    mediaPlayer?.start()
                    setPlaybackSpeed(speed)
                    _playbackState.value = _playbackState.value.copy(isPlaying = true)
                    return
                }
            }

            stop()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(File(filePath)))
                prepare()
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPositionMs = duration
                    )
                }
                start()
            }

            setPlaybackSpeed(speed)

            _playbackState.value = PlaybackState(
                isPlaying = true,
                currentPositionMs = 0,
                durationMs = mediaPlayer?.duration ?: 0,
                speed = speed,
                currentFilePath = filePath
            )
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error playing audio path: $filePath", e)
            stop()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error pausing", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error resuming", e)
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error seeking", e)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null) {
                val params = mediaPlayer?.playbackParams ?: PlaybackParams()
                params.speed = speed
                mediaPlayer?.playbackParams = params
            }
            _playbackState.value = _playbackState.value.copy(speed = speed)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error setting playback speed", e)
        }
    }

    fun updatePosition() {
        try {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                _playbackState.value = _playbackState.value.copy(
                    currentPositionMs = mediaPlayer?.currentPosition ?: 0,
                    durationMs = mediaPlayer?.duration ?: 0
                )
            }
        } catch (e: Exception) {
            // Ignore position update errors when player is resetting
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
            _playbackState.value = PlaybackState()
        }
    }
}

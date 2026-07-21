package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    private var startTimeMs = 0L

    fun startRecording(): Boolean {
        return try {
            val cacheDir = context.cacheDir
            outputFile = File(cacheDir, "recorded_voice_sample_${System.currentTimeMillis()}.m4a")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            startTimeMs = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Failed to start recording", e)
            isRecording = false
            false
        }
    }

    fun stopRecording(): File? {
        if (!isRecording) return outputFile
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            outputFile
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Failed to stop recording", e)
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            outputFile
        }
    }

    fun getMaxAmplitude(): Float {
        return try {
            if (isRecording) {
                val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                (maxAmp / 32767f).coerceIn(0f, 1f)
            } else {
                0f
            }
        } catch (e: Exception) {
            0f
        }
    }

    fun getDurationSeconds(): Int {
        return if (isRecording) {
            ((System.currentTimeMillis() - startTimeMs) / 1000).toInt()
        } else {
            0
        }
    }

    fun isRecording(): Boolean = isRecording
}

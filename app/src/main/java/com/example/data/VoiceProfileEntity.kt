package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_profiles")
data class VoiceProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val baseVoice: String, // Gemini TTS voice name: Kore, Puck, Fenrir, Aoede, Charon
    val promptDirective: String, // Custom prompt instructions for voice style/accent/pitch
    val pitch: Float = 0f, // -5.0 to +5.0
    val speed: Float = 1.0f, // 0.5 to 2.0
    val genderTag: String = "عام", // رجل, امرأة, طفل, آلي
    val styleTag: String = "احترافي", // إخباري, حماسي, هادئ, درامي, احترافي
    val isCustom: Boolean = false,
    val sampleAudioPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

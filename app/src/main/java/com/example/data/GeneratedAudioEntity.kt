package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_audios")
data class GeneratedAudioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voiceProfileId: Long,
    val voiceName: String,
    val textSnippet: String,
    val fullText: String,
    val audioFilePath: String,
    val durationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

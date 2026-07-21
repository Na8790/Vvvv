package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    // Voice Profiles Queries
    @Query("SELECT * FROM voice_profiles ORDER BY isCustom DESC, id ASC")
    fun getAllVoiceProfiles(): Flow<List<VoiceProfileEntity>>

    @Query("SELECT * FROM voice_profiles WHERE id = :id")
    suspend fun getVoiceProfileById(id: Long): VoiceProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceProfile(profile: VoiceProfileEntity): Long

    @Query("DELETE FROM voice_profiles WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomVoiceProfile(id: Long)

    // Generated Audio Queries
    @Query("SELECT * FROM generated_audios ORDER BY createdAt DESC")
    fun getAllGeneratedAudios(): Flow<List<GeneratedAudioEntity>>

    @Query("SELECT * FROM generated_audios WHERE id = :id")
    suspend fun getGeneratedAudioById(id: Long): GeneratedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedAudio(audio: GeneratedAudioEntity): Long

    @Query("DELETE FROM generated_audios WHERE id = :id")
    suspend fun deleteGeneratedAudioById(id: Long)
}

package com.example.data

import android.content.Context
import com.example.network.GeminiAudioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class VoiceRepository(
    private val context: Context,
    private val voiceDao: VoiceDao,
    private val geminiAudioService: GeminiAudioService
) {

    val allVoices: Flow<List<VoiceProfileEntity>> = voiceDao.getAllVoiceProfiles()
    val allGeneratedAudios: Flow<List<GeneratedAudioEntity>> = voiceDao.getAllGeneratedAudios()

    suspend fun ensureDefaultVoicesExist() = withContext(Dispatchers.IO) {
        val existing = voiceDao.getAllVoiceProfiles().first()
        if (existing.isEmpty()) {
            val defaultProfiles = listOf(
                VoiceProfileEntity(
                    name = "أحمد - إخباري رزين",
                    description = "صوت إخباري رجالي وقور ورزين مع إلقاء ممتاز للمقالات والنشرات",
                    baseVoice = "Kore",
                    promptDirective = "تحدث بنبرة إخبارية فصيحة، رزينة وواضحة، مع الالتزام بالوقوف الصحيح على الفواصل والسرعة المتزنة.",
                    pitch = 0.0f,
                    speed = 1.0f,
                    genderTag = "رجل",
                    styleTag = "إخباري",
                    isCustom = false
                ),
                VoiceProfileEntity(
                    name = "سارة - هادئ ولطيف",
                    description = "صوت نسائي دافئ ولطيف، مناسب للبودكاست والتأمل والكتب الصوتية",
                    baseVoice = "Aoede",
                    promptDirective = "تحدثي بنبرة نسائية هادئة، دافئة ومريحة جداً للأذن، مع إلقاء ناعم وبطيء نسبياً.",
                    pitch = 0.2f,
                    speed = 0.95f,
                    genderTag = "امرأة",
                    styleTag = "هادئ",
                    isCustom = false
                ),
                VoiceProfileEntity(
                    name = "عمر - حماسي ورياضي",
                    description = "صوت رجالي قوي وحماسي، ممتاز للإعلانات والتعليق الرياضي",
                    baseVoice = "Fenrir",
                    promptDirective = "تحدث بطاقة عالية، ونبرة حماسية ملهمة ومحفزة، مع نطق جلي وقوي للكلمات.",
                    pitch = -0.1f,
                    speed = 1.1f,
                    genderTag = "رجل",
                    styleTag = "حماسي",
                    isCustom = false
                ),
                VoiceProfileEntity(
                    name = "ليلى - دافئ وسردي",
                    description = "صوت نسائي حكواتي دافئ لإلقاء القصص والحكايات والوصف الشاعري",
                    baseVoice = "Puck",
                    promptDirective = "تحدثي بنبرة قصصية تعبيرية، مفعمة بالمشاعر، تناسب سرد الحكايات والقصص الممتعة.",
                    pitch = 0.1f,
                    speed = 1.0f,
                    genderTag = "امرأة",
                    styleTag = "درامي",
                    isCustom = false
                ),
                VoiceProfileEntity(
                    name = "راشد - عميق وبودكاست",
                    description = "صوت عميق وفخم، مناسب للحوارات الثقافية وبودكاست الصوتيات",
                    baseVoice = "Charon",
                    promptDirective = "تحدث بنبرة رجالية عميقة وفخمة، مع رنين قوي ومخارج حروف واضحة جداً.",
                    pitch = -0.4f,
                    speed = 0.95f,
                    genderTag = "رجل",
                    styleTag = "احترافي",
                    isCustom = false
                )
            )

            defaultProfiles.forEach { profile ->
                voiceDao.insertVoiceProfile(profile)
            }
        }
    }

    suspend fun synthesizeSpeech(text: String, profile: VoiceProfileEntity): GeneratedAudioEntity? = withContext(Dispatchers.IO) {
        val audioFile = geminiAudioService.synthesizeSpeech(text, profile) ?: return@withContext null

        val snippet = if (text.length > 40) text.take(40) + "..." else text
        val entity = GeneratedAudioEntity(
            voiceProfileId = profile.id,
            voiceName = profile.name,
            textSnippet = snippet,
            fullText = text,
            audioFilePath = audioFile.absolutePath,
            durationMs = 3000L,
            createdAt = System.currentTimeMillis()
        )

        val newId = voiceDao.insertGeneratedAudio(entity)
        entity.copy(id = newId)
    }

    suspend fun analyzeAndCreateCustomVoice(audioSampleFile: File, name: String): VoiceProfileEntity = withContext(Dispatchers.IO) {
        val analysis = geminiAudioService.analyzeAudioSample(audioSampleFile, name)

        val profile = VoiceProfileEntity(
            name = analysis.name,
            description = analysis.description,
            baseVoice = analysis.baseVoice,
            promptDirective = analysis.promptDirective,
            pitch = analysis.pitch,
            speed = analysis.speed,
            genderTag = analysis.genderTag,
            styleTag = analysis.styleTag,
            isCustom = true,
            sampleAudioPath = audioSampleFile.absolutePath,
            createdAt = System.currentTimeMillis()
        )

        val id = voiceDao.insertVoiceProfile(profile)
        profile.copy(id = id)
    }

    suspend fun deleteCustomVoice(id: Long) = withContext(Dispatchers.IO) {
        voiceDao.deleteCustomVoiceProfile(id)
    }

    suspend fun deleteGeneratedAudio(id: Long) = withContext(Dispatchers.IO) {
        voiceDao.deleteGeneratedAudioById(id)
    }
}

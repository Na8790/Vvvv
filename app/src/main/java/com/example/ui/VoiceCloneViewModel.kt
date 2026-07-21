package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayer
import com.example.audio.AudioRecorder
import com.example.audio.PlaybackState
import com.example.data.AppDatabase
import com.example.data.GeneratedAudioEntity
import com.example.data.VoiceProfileEntity
import com.example.data.VoiceRepository
import com.example.network.GeminiAudioService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class VoiceCloneViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val geminiService = GeminiAudioService(application)
    private val repository = VoiceRepository(application, db.voiceDao(), geminiService)

    val audioRecorder = AudioRecorder(application)
    val audioPlayer = AudioPlayer(application)

    val voicesList: StateFlow<List<VoiceProfileEntity>> = repository.allVoices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyList: StateFlow<List<GeneratedAudioEntity>> = repository.allGeneratedAudios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playbackState: StateFlow<PlaybackState> = audioPlayer.playbackState

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _selectedVoice = MutableStateFlow<VoiceProfileEntity?>(null)
    val selectedVoice: StateFlow<VoiceProfileEntity?> = _selectedVoice.asStateFlow()

    private val _isSynthesizing = MutableStateFlow(false)
    val isSynthesizing: StateFlow<Boolean> = _isSynthesizing.asStateFlow()

    private val _currentSynthesizedAudio = MutableStateFlow<GeneratedAudioEntity?>(null)
    val currentSynthesizedAudio: StateFlow<GeneratedAudioEntity?> = _currentSynthesizedAudio.asStateFlow()

    // Recording & Voice Cloning State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSec = MutableStateFlow(0)
    val recordingDurationSec: StateFlow<Int> = _recordingDurationSec.asStateFlow()

    private val _recordingAmplitude = MutableStateFlow(0f)
    val recordingAmplitude: StateFlow<Float> = _recordingAmplitude.asStateFlow()

    private val _recordedAudioFile = MutableStateFlow<File?>(null)
    val recordedAudioFile: StateFlow<File?> = _recordedAudioFile.asStateFlow()

    private val _isAnalyzingVoice = MutableStateFlow(false)
    val isAnalyzingVoice: StateFlow<Boolean> = _isAnalyzingVoice.asStateFlow()

    private val _newVoiceName = MutableStateFlow("")
    val newVoiceName: StateFlow<String> = _newVoiceName.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var recordingTimerJob: Job? = null
    private var playbackTickerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureDefaultVoicesExist()
        }

        viewModelScope.launch {
            voicesList.collect { list ->
                if (_selectedVoice.value == null && list.isNotEmpty()) {
                    _selectedVoice.value = list.first()
                }
            }
        }

        // Ticker for updating playback UI progress bar
        playbackTickerJob = viewModelScope.launch {
            while (true) {
                delay(200)
                audioPlayer.updatePosition()
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun selectVoice(profile: VoiceProfileEntity) {
        _selectedVoice.value = profile
    }

    fun updateNewVoiceName(name: String) {
        _newVoiceName.value = name
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun synthesizeTextToSpeech() {
        val text = _inputText.value.trim()
        val voice = _selectedVoice.value

        if (text.isBlank()) {
            _errorMessage.value = "الرجاء كتابة نص لتحويله إلى صوت"
            return
        }

        if (voice == null) {
            _errorMessage.value = "الرجاء اختيار صوت مستنسخ"
            return
        }

        viewModelScope.launch {
            _isSynthesizing.value = true
            _errorMessage.value = null
            try {
                val generated = repository.synthesizeSpeech(text, voice)
                if (generated != null) {
                    _currentSynthesizedAudio.value = generated
                    _toastMessage.value = "تم استنساخ الصوت بنجاح! 🎙️"
                    // Play synthesized audio automatically
                    audioPlayer.playPath(generated.audioFilePath, voice.speed)
                } else {
                    _errorMessage.value = "حدث خطأ أثناء معالجة الصوت، حاول مرة أخرى"
                }
            } catch (e: Exception) {
                _errorMessage.value = "فشل تحويل النص: ${e.localizedMessage}"
            } finally {
                _isSynthesizing.value = false
            }
        }
    }

    fun startRecording() {
        audioPlayer.stop()
        val started = audioRecorder.startRecording()
        if (started) {
            _isRecording.value = true
            _recordedAudioFile.value = null
            _recordingDurationSec.value = 0

            recordingTimerJob?.cancel()
            recordingTimerJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(100)
                    _recordingDurationSec.value = audioRecorder.getDurationSeconds()
                    _recordingAmplitude.value = audioRecorder.getMaxAmplitude()
                }
            }
        } else {
            _errorMessage.value = "فشل بدء التسجيل، يرجى التأكد من صلاحية المايكروفون"
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return
        val file = audioRecorder.stopRecording()
        _isRecording.value = false
        recordingTimerJob?.cancel()

        if (file != null && file.exists()) {
            _recordedAudioFile.value = file
            _toastMessage.value = "تم تسجيل العينة بنجاح، يمكنك استنساخ الصوت الآن"
        } else {
            _errorMessage.value = "لم يتم حفظ التسجيل"
        }
    }

    fun playRecordedSample() {
        val file = _recordedAudioFile.value
        if (file != null && file.exists()) {
            audioPlayer.playFile(file)
        }
    }

    fun analyzeAndCloneVoice() {
        val audioFile = _recordedAudioFile.value
        val name = _newVoiceName.value.ifBlank { "صوتي المستنسخ" }

        if (audioFile == null || !audioFile.exists()) {
            _errorMessage.value = "الرجاء تسجيل عينة صوتية أولاً لمطابقتها واستنساخها"
            return
        }

        viewModelScope.launch {
            _isAnalyzingVoice.value = true
            _errorMessage.value = null
            try {
                val newProfile = repository.analyzeAndCreateCustomVoice(audioFile, name)
                _selectedVoice.value = newProfile
                _toastMessage.value = "تم إنشاء واستنساخ الصوت (${newProfile.name}) بنجاح! ✨"
                _newVoiceName.value = ""
                _recordedAudioFile.value = null
                // Switch to TTS Studio Tab
                _selectedTab.value = 0
            } catch (e: Exception) {
                _errorMessage.value = "فشل استنساخ الصوت: ${e.localizedMessage}"
            } finally {
                _isAnalyzingVoice.value = false
            }
        }
    }

    fun playGeneratedAudio(item: GeneratedAudioEntity) {
        _currentSynthesizedAudio.value = item
        val file = File(item.audioFilePath)
        if (file.exists()) {
            audioPlayer.playFile(file)
        } else {
            _errorMessage.value = "الملف الصوتي لم يعد متوفراً"
        }
    }

    fun pausePlayback() {
        audioPlayer.pause()
    }

    fun resumePlayback() {
        audioPlayer.resume()
    }

    fun seekPlayback(positionMs: Int) {
        audioPlayer.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }

    fun deleteVoiceProfile(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomVoice(id)
            _toastMessage.value = "تم حذف الصوت المستنسخ"
            if (_selectedVoice.value?.id == id) {
                _selectedVoice.value = voicesList.value.firstOrNull()
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteGeneratedAudio(id)
            if (_currentSynthesizedAudio.value?.id == id) {
                audioPlayer.stop()
                _currentSynthesizedAudio.value = null
            }
            _toastMessage.value = "تم حذف التسجيل"
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stopRecording()
        audioPlayer.stop()
        recordingTimerJob?.cancel()
        playbackTickerJob?.cancel()
    }
}

package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.VoiceCloneScreen
import com.example.ui.screens.VoicesLibraryScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(viewModel: VoiceCloneViewModel) {
    val context = LocalContext.current

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val voices by viewModel.voicesList.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()
    val isSynthesizing by viewModel.isSynthesizing.collectAsStateWithLifecycle()
    val synthesisProgress by viewModel.synthesisProgress.collectAsStateWithLifecycle()
    val synthesisStepText by viewModel.synthesisStepText.collectAsStateWithLifecycle()
    val currentSynthesizedAudio by viewModel.currentSynthesizedAudio.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingDurationSec by viewModel.recordingDurationSec.collectAsStateWithLifecycle()
    val recordingAmplitude by viewModel.recordingAmplitude.collectAsStateWithLifecycle()
    val recordedAudioFile by viewModel.recordedAudioFile.collectAsStateWithLifecycle()
    val newVoiceName by viewModel.newVoiceName.collectAsStateWithLifecycle()
    val isAnalyzingVoice by viewModel.isAnalyzingVoice.collectAsStateWithLifecycle()
    val cloningProgress by viewModel.cloningProgress.collectAsStateWithLifecycle()
    val cloningStepText by viewModel.cloningStepText.collectAsStateWithLifecycle()

    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "استنساخ الأصوات AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "تحويل النصوص بكلام مستنسخ دقيق",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioBackground,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    Surface(
                        color = StudioSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gemini TTS Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StudioSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val navItems = listOf(
                    NavItem("تحويل النص", Icons.Default.GraphicEq, 0),
                    NavItem("استنساخ صوت", Icons.Default.Mic, 1),
                    NavItem("الأصوات", Icons.Default.RecordVoiceOver, 2),
                    NavItem("السجل", Icons.Default.History, 3)
                )

                navItems.forEach { item ->
                    val isSelected = selectedTab == item.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) PrimaryViolet else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (isSelected) PrimaryViolet else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PrimaryViolet.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = StudioBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() with fadeOut() },
                label = "tab_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> StudioScreen(
                        inputText = inputText,
                        onInputTextChange = viewModel::updateInputText,
                        voices = voices,
                        selectedVoice = selectedVoice,
                        onSelectVoice = viewModel::selectVoice,
                        isSynthesizing = isSynthesizing,
                        synthesisProgress = synthesisProgress,
                        synthesisStepText = synthesisStepText,
                        onSynthesize = viewModel::synthesizeTextToSpeech,
                        currentSynthesizedAudio = currentSynthesizedAudio,
                        playbackState = playbackState,
                        onPlayPauseToggle = {
                            if (playbackState.isPlaying) viewModel.pausePlayback() else viewModel.resumePlayback()
                        },
                        onSeek = viewModel::seekPlayback,
                        onSpeedChange = viewModel::setPlaybackSpeed,
                        onNavigateToCloneTab = { viewModel.selectTab(1) }
                    )

                    1 -> VoiceCloneScreen(
                        isRecording = isRecording,
                        recordingDurationSec = recordingDurationSec,
                        recordingAmplitude = recordingAmplitude,
                        recordedAudioFile = recordedAudioFile,
                        onStartRecording = viewModel::startRecording,
                        onStopRecording = viewModel::stopRecording,
                        onPlaySample = viewModel::playRecordedSample,
                        newVoiceName = newVoiceName,
                        onVoiceNameChange = viewModel::updateNewVoiceName,
                        isAnalyzing = isAnalyzingVoice,
                        cloningProgress = cloningProgress,
                        cloningStepText = cloningStepText,
                        onAnalyzeAndClone = viewModel::analyzeAndCloneVoice
                    )

                    2 -> VoicesLibraryScreen(
                        voices = voices,
                        selectedVoice = selectedVoice,
                        onSelectVoice = viewModel::selectVoice,
                        onDeleteVoice = viewModel::deleteVoiceProfile,
                        onNavigateToCloneTab = { viewModel.selectTab(1) }
                    )

                    3 -> HistoryScreen(
                        historyList = historyList,
                        playbackState = playbackState,
                        onPlayAudio = viewModel::playGeneratedAudio,
                        onPlayPauseToggle = {
                            if (playbackState.isPlaying) viewModel.pausePlayback() else viewModel.resumePlayback()
                        },
                        onSeek = viewModel::seekPlayback,
                        onSpeedChange = viewModel::setPlaybackSpeed,
                        onDeleteHistoryItem = viewModel::deleteHistoryItem
                    )
                }
            }
        }
    }
}

private data class NavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val index: Int)

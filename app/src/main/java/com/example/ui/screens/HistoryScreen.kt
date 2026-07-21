package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.PlaybackState
import com.example.data.GeneratedAudioEntity
import com.example.ui.components.AudioPlayerCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HistoryScreen(
    historyList: List<GeneratedAudioEntity>,
    playbackState: PlaybackState,
    onPlayAudio: (GeneratedAudioEntity) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onDeleteHistoryItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "سجل التسجيلات الصوتية 🎧",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "استمع للأنشطة والتسجيلات السابقة التي تم استنساخها",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد تسجيلات سابقة",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "قم بنطق أول نص من الشاشة الرئيسية لحفظه هنا",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(historyList) { audioItem ->
                    AudioPlayerCard(
                        audio = audioItem,
                        playbackState = playbackState,
                        onPlayPauseToggle = {
                            if (playbackState.currentFilePath == audioItem.audioFilePath) {
                                onPlayPauseToggle()
                            } else {
                                onPlayAudio(audioItem)
                            }
                        },
                        onSeek = onSeek,
                        onSpeedChange = onSpeedChange,
                        onDelete = { onDeleteHistoryItem(audioItem.id) }
                    )
                }
            }
        }
    }
}

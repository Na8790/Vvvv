package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.audio.PlaybackState
import com.example.data.GeneratedAudioEntity
import com.example.ui.theme.*
import java.io.File
import java.util.Locale

@Composable
fun AudioPlayerCard(
    audio: GeneratedAudioEntity,
    playbackState: PlaybackState,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isCurrentAudio = playbackState.currentFilePath == audio.audioFilePath
    val isPlaying = isCurrentAudio && playbackState.isPlaying

    val durationMs = if (isCurrentAudio && playbackState.durationMs > 0) playbackState.durationMs else (audio.durationMs.coerceAtLeast(3000L)).toInt()
    val currentMs = if (isCurrentAudio) playbackState.currentPositionMs else 0
    val progress = if (durationMs > 0) (currentMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    val formattedCurrent = formatTimeMs(currentMs)
    val formattedDuration = formatTimeMs(durationMs)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_player_card")
            .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioSurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // Header: Voice Name & Action menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Voice Icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = audio.voiceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "صوت مستنسخ بالذكاء الاصطناعي",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = {
                            val file = File(audio.audioFilePath)
                            if (file.exists()) {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "audio/*"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة الصوت المستنسخ"))
                                } catch (e: Exception) {
                                    // Fallback text share if file provider not fully initialized
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "استمع إلى الصوتي المستنسخ: ${audio.fullText}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "مشاركة"))
                                }
                            }
                        },
                        modifier = Modifier.testTag("share_audio_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = TextSecondary
                        )
                    }

                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "حذف",
                                tint = AccentPink
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text Snippet display
            Text(
                text = "« ${audio.fullText} »",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudioSurfaceVariant)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Waveform Visualizer
            WaveformVisualizer(
                isPlaying = isPlaying,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPlayPauseToggle()
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Slider & Timers
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    onSeek((newProgress * durationMs).toInt())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_progress_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = SecondaryCyan,
                    activeTrackColor = PrimaryViolet,
                    inactiveTrackColor = StudioCardBorder
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedCurrent,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = formattedDuration,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playback controls row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed selection pill
                val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)
                val currentSpeed = playbackState.speed
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(StudioSurfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    speeds.forEach { speed ->
                        val isSelected = currentSpeed == speed
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryViolet else Color.Transparent)
                                .clickable { onSpeedChange(speed) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                // Play / Pause FAB
                FloatingActionButton(
                    onClick = onPlayPauseToggle,
                    containerColor = PrimaryViolet,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimeMs(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.audio.PlaybackState
import com.example.data.GeneratedAudioEntity
import com.example.data.VoiceProfileEntity
import com.example.ui.components.AudioPlayerCard
import com.example.ui.theme.*

@Composable
fun StudioScreen(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    voices: List<VoiceProfileEntity>,
    selectedVoice: VoiceProfileEntity?,
    onSelectVoice: (VoiceProfileEntity) -> Unit,
    isSynthesizing: Boolean,
    onSynthesize: () -> Unit,
    currentSynthesizedAudio: GeneratedAudioEntity?,
    playbackState: PlaybackState,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onNavigateToCloneTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val arabicPresets = listOf(
        "مرحباً بكم في استديو الذكاء الاصطناعي لاستنساخ الأصوات وتحويل النصوص إلى كلام فصيح عالي الدقة.",
        "يسرنا تقديم هذا التقرير الإخباري اليومي حول أحدث مستجدات التقنية والابتكار الرقمي في العالم.",
        "كان يا ما كان في قديم الزمان، قرية صغار تحيط بها الأشجار الزرقاء والبحيرات الصافية.",
        "استمع الآن إلى نبرة صوتك المستنسخة بدقة عالية، حيث يمكنك استخدامها في صنع المحتوى والبودكاست."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Voice Selection Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "اختر الصوت المستنسخ 🎙️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "اختر الصوت المناسب لنوع النص والإلقاء",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            TextButton(onClick = onNavigateToCloneTab) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = PrimaryViolet,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "استنساخ صوتك",
                    color = PrimaryViolet,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Carousel of Voices
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(voices) { profile ->
                val isSelected = selectedVoice?.id == profile.id
                VoicePillCard(
                    profile = profile,
                    isSelected = isSelected,
                    onClick = { onSelectVoice(profile) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Text Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النص المراد تحويله لكلام",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip
                                if (clip != null && clip.itemCount > 0) {
                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                    onInputTextChange(inputText + text)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "لصق",
                                tint = TextSecondary
                            )
                        }

                        if (inputText.isNotEmpty()) {
                            IconButton(onClick = { onInputTextChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح",
                                    tint = AccentPink
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 220.dp)
                        .testTag("text_to_speech_input"),
                    placeholder = {
                        Text(
                            text = "اكتب النص هنا ليقوم الذكاء الاصطناعي بنطقه بالصوت المستنسخ...",
                            color = TextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedContainerColor = StudioSurfaceVariant,
                        unfocusedContainerColor = StudioSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "عدد الحروف: ${inputText.length}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "تدعم العربية والإنجليزية",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryCyan
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Arabic Presets
                Text(
                    text = "نماذج نصوص جاهزة لللتجربة:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(arabicPresets) { preset ->
                        AssistChip(
                            onClick = { onInputTextChange(preset) },
                            label = {
                                Text(
                                    text = if (preset.length > 22) preset.take(22) + "..." else preset,
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = StudioSurfaceVariant
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor = StudioCardBorder
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Synthesize Button
        Button(
            onClick = onSynthesize,
            enabled = !isSynthesizing && inputText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("synthesize_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (!isSynthesizing && inputText.isNotBlank()) {
                            Brush.horizontalGradient(listOf(PrimaryViolet, SecondaryCyan))
                        } else {
                            Brush.horizontalGradient(listOf(StudioCardBorder, StudioCardBorder))
                        },
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSynthesizing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جاري تحويل النص بالصوت المستنسخ...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تحدث بالصوت المستنسخ 🎙️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Synthesized Audio Player Result
        if (currentSynthesizedAudio != null) {
            Text(
                text = "النتيجة الصوتية 🎧",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            AudioPlayerCard(
                audio = currentSynthesizedAudio,
                playbackState = playbackState,
                onPlayPauseToggle = onPlayPauseToggle,
                onSeek = onSeek,
                onSpeedChange = onSpeedChange
            )
        }
    }
}

@Composable
fun VoicePillCard(
    profile: VoiceProfileEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryViolet else StudioCardBorder
    val containerColor = if (isSelected) StudioSurfaceVariant else StudioSurface

    Card(
        modifier = Modifier
            .width(160.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (profile.isCustom) listOf(AccentPink, PrimaryViolet) else listOf(PrimaryViolet, SecondaryCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (profile.isCustom) Icons.Default.RecordVoiceOver else Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "محدد",
                        tint = PrimaryViolet,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = profile.styleTag,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

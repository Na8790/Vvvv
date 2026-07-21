package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import java.io.File
import java.util.Locale

@Composable
fun VoiceCloneScreen(
    isRecording: Boolean,
    recordingDurationSec: Int,
    recordingAmplitude: Float,
    recordedAudioFile: File?,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlaySample: () -> Unit,
    newVoiceName: String,
    onVoiceNameChange: (String) -> Unit,
    isAnalyzing: Boolean,
    cloningProgress: Float = 0f,
    cloningStepText: String = "",
    onAnalyzeAndClone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Permission state
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            onStartRecording()
        }
    }

    // Pulse animation for recording microphone button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "مختبر استنساخ الصوت 🎙️",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "قم بتسجيل عينة صوتية واضحة لمدة 5-10 ثوان ليقوم الذكاء الاصطناعي ببناء نموذج صوتك",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sample Text Card to read aloud
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryViolet.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = StudioSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = PrimaryViolet
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "النص المقترح قراءته بصوتك:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "« أَهْلًا بِكُم فِي مُسْتَقْبَلِ التَّقْنِيَةِ وَالذَّكَاءِ الِاصْطِنَاعِيِّ، هَذِهِ عَيِّنَةُ صَوْتِي الَّتِي أُسَجِّلُهَا الآنَ لِاسْتِنْسَاخِهَا بِدِقَّةٍ عَالِيَةٍ بِلُغَةٍ عَرَبِيَّةٍ فَصِيحَةٍ. »",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryCyan,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioSurfaceVariant)
                        .padding(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Big Animated Microphone Button
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing background rings when recording
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(AccentPink.copy(alpha = 0.25f))
                )
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale * 0.9f)
                        .clip(CircleShape)
                        .background(PrimaryViolet.copy(alpha = 0.35f))
                )
            }

            // Main Mic Button
            FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        if (hasMicPermission) {
                            onStartRecording()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier
                    .size(100.dp)
                    .testTag("microphone_record_button"),
                containerColor = if (isRecording) AccentPink else PrimaryViolet,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "إيقاف التسجيل" else "بدء التسجيل",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer & Recording Status
        Text(
            text = if (isRecording) {
                val sec = recordingDurationSec
                val formattedTime = String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60)
                "جاري التسجيل... 🔴 $formattedTime"
            } else if (recordedAudioFile != null) {
                "تم تسجيل العينة بنجاح! 🟢"
            } else {
                "اضغط على المايك لبدء التسجيل"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isRecording) AccentPink else if (recordedAudioFile != null) AccentEmerald else TextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Recorded Audio Sample Review
        if (recordedAudioFile != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StudioSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = SecondaryCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "معاينة العينة المسجلة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = onPlaySample,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "استماع",
                        tint = StudioBackground
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "استماع", color = StudioBackground, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Voice Profile Name Input
            OutlinedTextField(
                value = newVoiceName,
                onValueChange = onVoiceNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_name_input"),
                label = { Text("اسم الصوت المستنسخ (مثال: صوتي الشخصي)") },
                placeholder = { Text("أدخل اسماً يميز صوتك المستنسخ") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryViolet,
                    unfocusedBorderColor = StudioCardBorder,
                    focusedContainerColor = StudioSurface,
                    unfocusedContainerColor = StudioSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isAnalyzing) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AccentPink, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AccentPink,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (cloningStepText.isNotBlank()) cloningStepText else "جاري تحليل الخصائص واستنساخ الصوت...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${(cloningProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentPink
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { cloningProgress.coerceIn(0.05f, 1.0f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("voice_cloning_progress_bar"),
                            color = AccentPink,
                            trackColor = StudioCardBorder,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Clone Action Button
            Button(
                onClick = onAnalyzeAndClone,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("clone_voice_action_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(AccentPink, PrimaryViolet)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAnalyzing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "جاري معالجة الصوت...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "تحليل واستنساخ الصوت بالذكاء الاصطناعي ✨",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

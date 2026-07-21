package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.VoiceProfileEntity
import com.example.ui.components.VoiceProfileCard
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VoicesLibraryScreen(
    voices: List<VoiceProfileEntity>,
    selectedVoice: VoiceProfileEntity?,
    onSelectVoice: (VoiceProfileEntity) -> Unit,
    onDeleteVoice: (Long) -> Unit,
    onNavigateToCloneTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مكتبة الأصوات 🎙️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "الأصوات المستنسخة والقوالب الصوتية الجاهزة",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            FloatingActionButton(
                onClick = onNavigateToCloneTab,
                containerColor = PrimaryViolet,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة صوت جديد"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (voices.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد أصوات متوفرة حالياً",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(voices) { profile ->
                    VoiceProfileCard(
                        profile = profile,
                        isSelected = selectedVoice?.id == profile.id,
                        onSelect = { onSelectVoice(profile) },
                        onDelete = if (profile.isCustom) {
                            { onDeleteVoice(profile.id) }
                        } else null
                    )
                }
            }
        }
    }
}

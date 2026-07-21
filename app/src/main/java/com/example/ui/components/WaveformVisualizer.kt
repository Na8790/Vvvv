package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    activeColor: Color = PrimaryViolet,
    inactiveColor: Color = Color(0xFF334155),
    barCount: Int = 32,
    height: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val canvasHeight = size.height
        val barWidth = (width / (barCount * 1.5f)).coerceAtLeast(3.dp.toPx())
        val gap = barWidth * 0.5f

        for (i in 0 until barCount) {
            val barFraction = i.toFloat() / barCount
            val isPlayed = barFraction <= progress

            // Generate dynamic waveform heights based on sine waves + position
            val waveAmplitude = if (isPlaying) {
                0.3f + 0.6f * sin(phase + i * 0.3f).let { (it + 1) / 2 }
            } else {
                0.2f + 0.5f * sin(i * 0.4f).let { (it + 1) / 2 }
            }

            val barHeight = (canvasHeight * waveAmplitude).coerceAtLeast(6.dp.toPx())
            val x = i * (barWidth + gap)
            val y = (canvasHeight - barHeight) / 2f

            val brush = if (isPlayed) {
                Brush.verticalGradient(
                    colors = listOf(PrimaryViolet, SecondaryCyan),
                    startY = y,
                    endY = y + barHeight
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(inactiveColor, inactiveColor)
                )
            }

            drawRoundRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

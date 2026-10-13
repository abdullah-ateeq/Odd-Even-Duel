package com.example.oddevenduel.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oddevenduel.domain.model.CellState
import com.example.oddevenduel.domain.model.NumberCell

@Composable
fun BoardCellView(
    cell: NumberCell,
    showCoordinates: Boolean,
    animationsEnabled: Boolean,
    isAnimatingSuccess: Boolean,
    isAnimatingShake: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(1f) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isAnimatingSuccess) {
        if (isAnimatingSuccess && animationsEnabled) {
            scaleAnim.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
        }
    }

    LaunchedEffect(isAnimatingShake) {
        if (isAnimatingShake && animationsEnabled) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    -12f at 50
                    12f at 100
                    -8f at 170
                    8f at 240
                    -4f at 300
                    0f at 350
                }
            )
        }
    }

    val coordLabel = cell.coordinate.toLabel()
    val cellDescription = when (cell.state) {
        CellState.ACTIVE -> "Number ${cell.value} at $coordLabel"
        CellState.PLAYER_CLAIMED -> "Player claimed number ${cell.value} at $coordLabel"
        CellState.CPU_CLAIMED -> "CPU claimed number ${cell.value} at $coordLabel"
    }

    val backgroundColor = when (cell.state) {
        CellState.ACTIVE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        CellState.PLAYER_CLAIMED -> MaterialTheme.colorScheme.primaryContainer
        CellState.CPU_CLAIMED -> MaterialTheme.colorScheme.secondaryContainer
    }

    val borderStroke = when (cell.state) {
        CellState.ACTIVE -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        CellState.PLAYER_CLAIMED -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        CellState.CPU_CLAIMED -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    }

    val textColor = when (cell.state) {
        CellState.ACTIVE -> MaterialTheme.colorScheme.onSurface
        CellState.PLAYER_CLAIMED -> MaterialTheme.colorScheme.onPrimaryContainer
        CellState.CPU_CLAIMED -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scaleAnim.value)
            .graphicsLayer { translationX = shakeOffset.value }
            .semantics { contentDescription = cellDescription }
            .clickable(enabled = cell.isActive) { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (cell.isActive) 1.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Coordinate label (top-left) if enabled
            if (showCoordinates) {
                Text(
                    text = coordLabel,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 3.dp, top = 2.dp)
                )
            }

            // Ownership Badge (top-right) if claimed
            when (cell.state) {
                CellState.PLAYER_CLAIMED -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 3.dp, top = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "YOU",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                CellState.CPU_CLAIMED -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 3.dp, top = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "CPU",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                CellState.ACTIVE -> Unit
            }

            // Cell Value (Center)
            Text(
                text = "${cell.value}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

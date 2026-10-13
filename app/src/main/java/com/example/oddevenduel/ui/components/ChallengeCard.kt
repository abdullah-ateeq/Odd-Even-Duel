package com.example.oddevenduel.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oddevenduel.domain.model.GamePhase
import com.example.oddevenduel.domain.model.ParityChallenge
import com.example.oddevenduel.domain.model.TurnTimerOption

@Composable
fun ChallengeCard(
    challenge: ParityChallenge,
    phase: GamePhase,
    timerOption: TurnTimerOption,
    timeRemaining: Int?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Current challenge: ${challenge.displayName()}" },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "CURRENT CHALLENGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                AnimatedContent(
                    targetState = challenge,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.85f)) togetherWith (fadeOut() + scaleOut(targetScale = 1.15f))
                    },
                    label = "ChallengeTextAnimation"
                ) { targetChallenge ->
                    Text(
                        text = targetChallenge.displayName(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = challenge.promptText(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
                )
            }

            // Optional turn timer circular countdown
            if (timerOption.isEnabled && phase == GamePhase.PLAYER_TURN && timeRemaining != null) {
                val totalSec = timerOption.seconds.toFloat()
                val progress = (timeRemaining / totalSec).coerceIn(0f, 1f)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(46.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(44.dp),
                        strokeWidth = 3.5.dp,
                        color = if (timeRemaining <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "$timeRemaining",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeRemaining <= 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

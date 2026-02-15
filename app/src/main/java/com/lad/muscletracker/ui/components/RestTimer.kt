package com.lad.muscletracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.ui.theme.*

@Composable
fun RestTimer(
    seconds: Int,
    isRunning: Boolean,
    onStart: (Int) -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isRunning) Blue500.copy(alpha = 0.15f) else DarkCard,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Repos",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                if (isRunning) {
                    val minutes = seconds / 60
                    val secs = seconds % 60
                    Text(
                        String.format("%d:%02d", minutes, secs),
                        color = if (seconds <= 10) Red500 else Blue400,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Pret",
                        color = TextMuted,
                        fontSize = 16.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!isRunning) {
                    listOf(60 to "1:00", 90 to "1:30", 120 to "2:00", 150 to "2:30").forEach { (sec, label) ->
                        FilledTonalButton(
                            onClick = { onStart(sec) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Blue500.copy(alpha = 0.2f)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(label, fontSize = 11.sp, color = Blue400)
                        }
                    }
                } else {
                    IconButton(onClick = onStop) {
                        Icon(Icons.Default.Close, "Stop", tint = Red500)
                    }
                }
            }
        }
    }
}

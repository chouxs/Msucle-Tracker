package com.lad.muscletracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.entity.Exercise
import com.lad.muscletracker.ui.theme.*

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: ((Long) -> Unit)? = null,
    onShowDemo: ((String) -> Unit)? = null
) {
    val groupColor = when (exercise.muscleGroup) {
        "Pecs" -> Red500
        "Dos" -> Blue500
        "Jambes" -> Green500
        "Epaules" -> Orange500
        "Bras" -> Purple500
        "Abdos" -> Blue400
        else -> TextSecondary
    }

    val typeLabel = if (exercise.exerciseType == "compound") "Compose" else "Isolation"
    val typeColor = if (exercise.exerciseType == "compound") Orange500 else Blue400

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkCard,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = groupColor,
                modifier = Modifier.size(width = 4.dp, height = 32.dp)
            ) {}

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        exercise.muscleGroup,
                        color = groupColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        typeLabel,
                        color = typeColor,
                        fontSize = 10.sp
                    )
                }
            }

            // Sets x Reps info
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${exercise.targetSets}x${exercise.targetRepsMin}-${exercise.targetRepsMax}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "${exercise.restSeconds}s",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            if (onShowDemo != null) {
                IconButton(
                    onClick = { onShowDemo(exercise.name) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Voir le mouvement",
                        tint = Blue400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (onToggleFavorite != null) {
                IconButton(
                    onClick = { onToggleFavorite(exercise.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (exercise.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favori",
                        tint = if (exercise.isFavorite) Orange500 else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

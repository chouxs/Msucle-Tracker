package com.lad.muscletracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.WeeklyMuscleVolume
import com.lad.muscletracker.ui.theme.*

/**
 * Volume landmarks per muscle group based on RP recommendations.
 * MEV = Minimum Effective Volume
 * MAV = Maximum Adaptive Volume
 * MRV = Maximum Recoverable Volume
 */
private data class VolumeLandmark(
    val muscleGroup: String,
    val mev: Int,
    val mav: Int,
    val mrv: Int
)

private val volumeLandmarks = listOf(
    VolumeLandmark("Pecs", 8, 14, 20),
    VolumeLandmark("Dos", 8, 14, 20),
    VolumeLandmark("Epaules", 6, 12, 18),
    VolumeLandmark("Bras", 4, 10, 16),
    VolumeLandmark("Jambes", 6, 12, 20),
    VolumeLandmark("Abdos", 0, 8, 16)
)

@Composable
fun VolumeScreen(
    weeklyVolume: List<WeeklyMuscleVolume>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Retour", tint = TextPrimary)
            }
            Text(
                "Volume Hebdomadaire",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Series par groupe musculaire cette semaine",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(16.dp))

        // Volume bars
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(volumeLandmarks) { landmark ->
                val volumeEntry = weeklyVolume.find {
                    it.muscleGroup.equals(landmark.muscleGroup, ignoreCase = true)
                }
                val currentSets = volumeEntry?.totalSets ?: 0
                val tonnage = volumeEntry?.totalTonnage ?: 0f

                VolumeBar(
                    muscleGroup = landmark.muscleGroup,
                    currentSets = currentSets,
                    tonnage = tonnage,
                    mev = landmark.mev,
                    mav = landmark.mav,
                    mrv = landmark.mrv
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Legend
        VolumeLegend()
    }
}

@Composable
private fun VolumeBar(
    muscleGroup: String,
    currentSets: Int,
    tonnage: Float,
    mev: Int,
    mav: Int,
    mrv: Int
) {
    val barColor = when {
        currentSets == 0 -> TextMuted
        currentSets < mev -> Red500
        currentSets < mav -> Orange500
        currentSets <= mrv -> Green500
        else -> Red500
    }

    val statusLabel = when {
        currentSets == 0 -> "Aucun"
        currentSets < mev -> "Sous MEV"
        currentSets < mav -> "MEV-MAV"
        currentSets <= mrv -> "Optimal"
        else -> "Au-dessus MRV"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    muscleGroup,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        statusLabel,
                        color = barColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "$currentSets / $mav sets",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Horizontal bar with landmarks
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val width = size.width
                    val height = size.height
                    // Display range: 0 to mrv + 4 (to show overflow)
                    val maxDisplay = (mrv + 4).toFloat()

                    // Background track
                    drawRoundRect(
                        color = DarkBorder,
                        topLeft = Offset.Zero,
                        size = Size(width, height),
                        cornerRadius = CornerRadius(height / 2, height / 2)
                    )

                    // MEV zone marker (vertical line)
                    val mevX = (mev.toFloat() / maxDisplay) * width
                    drawLine(
                        color = Orange500.copy(alpha = 0.5f),
                        start = Offset(mevX, 0f),
                        end = Offset(mevX, height),
                        strokeWidth = 2f
                    )

                    // MAV zone marker (vertical line)
                    val mavX = (mav.toFloat() / maxDisplay) * width
                    drawLine(
                        color = Green500.copy(alpha = 0.5f),
                        start = Offset(mavX, 0f),
                        end = Offset(mavX, height),
                        strokeWidth = 2f
                    )

                    // MRV zone marker (vertical line)
                    val mrvX = (mrv.toFloat() / maxDisplay) * width
                    drawLine(
                        color = Red500.copy(alpha = 0.5f),
                        start = Offset(mrvX, 0f),
                        end = Offset(mrvX, height),
                        strokeWidth = 2f
                    )

                    // Current volume bar
                    val barWidth = (currentSets.toFloat() / maxDisplay).coerceIn(0f, 1f) * width
                    if (barWidth > 0f) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset.Zero,
                            size = Size(barWidth, height),
                            cornerRadius = CornerRadius(height / 2, height / 2)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Tonnage info
            if (tonnage > 0f) {
                Text(
                    "Tonnage: ${String.format("%.0f", tonnage)} kg",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun VolumeLegend() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "Reperes de volume (RP)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))

            LegendRow(
                color = Orange500,
                label = "MEV",
                description = "Volume minimum efficace"
            )
            Spacer(Modifier.height(4.dp))
            LegendRow(
                color = Green500,
                label = "MAV",
                description = "Volume adaptatif maximal (zone optimale)"
            )
            Spacer(Modifier.height(4.dp))
            LegendRow(
                color = Red500,
                label = "MRV",
                description = "Volume maximal recuperable"
            )
        }
    }
}

@Composable
private fun LegendRow(
    color: Color,
    label: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Text(
            "$label — $description",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

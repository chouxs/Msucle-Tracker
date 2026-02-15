package com.lad.muscletracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.ExerciseProgress
import com.lad.muscletracker.data.entity.Exercise
import com.lad.muscletracker.ui.components.ExerciseCard
import com.lad.muscletracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    exercises: List<Exercise>,
    progressData: List<ExerciseProgress>,
    selectedExerciseId: Long?,
    onSelectExercise: (Long) -> Unit,
    onBack: () -> Unit
) {
    val selectedExercise = exercises.find { it.id == selectedExerciseId }

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
            Text("Progression", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))

        if (selectedExercise != null && progressData.isNotEmpty()) {
            // Chart section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        selectedExercise.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Charge maximale (kg)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Simple line chart
                    ProgressChart(
                        data = progressData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val maxWeight = progressData.maxByOrNull { it.maxWeight }?.maxWeight ?: 0f
                        val lastWeight = progressData.lastOrNull()?.maxWeight ?: 0f
                        val firstWeight = progressData.firstOrNull()?.maxWeight ?: 0f
                        val progression = if (firstWeight > 0) ((lastWeight - firstWeight) / firstWeight * 100) else 0f

                        MiniStat("Max", "${maxWeight.toInt()} kg", Orange500)
                        MiniStat("Actuel", "${lastWeight.toInt()} kg", Blue500)
                        MiniStat(
                            "Progres",
                            "${if (progression >= 0) "+" else ""}${String.format("%.0f", progression)}%",
                            if (progression >= 0) Green500 else Red500
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Detailed sessions
            Text("Historique", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(progressData.reversed()) { entry ->
                    val dateFormat = SimpleDateFormat("dd/MM", Locale.FRANCE)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(dateFormat.format(Date(entry.date)), color = TextMuted, fontSize = 12.sp)
                            Text("${entry.maxWeight.toInt()} kg", color = Orange500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Vol: ${entry.totalVolume.toInt()} kg", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            // Exercise picker
            Text(
                "Choisis un exercice pour voir ta progression",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(exercises) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = { onSelectExercise(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressChart(
    data: List<ExerciseProgress>,
    modifier: Modifier = Modifier
) {
    if (data.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Pas assez de donnees", color = TextMuted, fontSize = 12.sp)
        }
        return
    }

    val maxWeight = data.maxOf { it.maxWeight }
    val minWeight = data.minOf { it.maxWeight }
    val range = (maxWeight - minWeight).coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 8f

        val stepX = (width - padding * 2) / (data.size - 1)

        val path = Path()
        data.forEachIndexed { index, entry ->
            val x = padding + index * stepX
            val y = height - padding - ((entry.maxWeight - minWeight) / range) * (height - padding * 2)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            // Draw points
            drawCircle(
                color = Blue500,
                radius = 4f,
                center = Offset(x, y)
            )
        }

        // Draw line
        drawPath(
            path = path,
            color = Blue500,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}

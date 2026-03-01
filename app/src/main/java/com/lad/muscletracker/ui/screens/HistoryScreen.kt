package com.lad.muscletracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.SetWithExerciseName
import com.lad.muscletracker.data.entity.Workout
import com.lad.muscletracker.ui.components.SetRow
import com.lad.muscletracker.ui.theme.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    workouts: List<Workout>,
    getSetsForWorkout: (Long) -> Flow<List<SetWithExerciseName>>,
    onDeleteWorkout: (Workout) -> Unit,
    onEditWorkout: (Workout) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var expandedWorkoutId by remember { mutableStateOf<Long?>(null) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

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
                "Historique",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${workouts.size} seances",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        if (workouts.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        null,
                        tint = TextMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Aucune seance", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workouts, key = { it.id }) { workout ->
                    WorkoutHistoryCard(
                        workout = workout,
                        isExpanded = expandedWorkoutId == workout.id,
                        getSetsForWorkout = getSetsForWorkout,
                        onToggle = {
                            expandedWorkoutId = if (expandedWorkoutId == workout.id) null else workout.id
                        },
                        onDelete = { workoutToDelete = workout },
                        onEdit = { onEditWorkout(workout) }
                    )
                }
            }
        }
    }

    workoutToDelete?.let { workout ->
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            title = { Text("Supprimer la seance?", color = TextPrimary) },
            text = { Text("Cette action est irreversible.", color = TextSecondary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    onDeleteWorkout(workout)
                    workoutToDelete = null
                }) { Text("Supprimer", color = Red500) }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) { Text("Annuler", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun WorkoutHistoryCard(
    workout: Workout,
    isExpanded: Boolean,
    getSetsForWorkout: (Long) -> Flow<List<SetWithExerciseName>>,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    val duration = if (workout.durationSeconds > 0) "${workout.durationSeconds / 60} min" else ""

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        workout.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            dateFormat.format(Date(workout.date)),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        if (duration.isNotEmpty()) {
                            Text(duration, color = Blue400, fontSize = 11.sp)
                        }
                    }
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, "Modifier", tint = Blue400, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Supprimer", tint = Red500.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                }

                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "Expand",
                    tint = TextMuted
                )
            }

            if (isExpanded) {
                val sets by getSetsForWorkout(workout.id).collectAsState(initial = emptyList())

                Spacer(Modifier.height(10.dp))
                @Suppress("DEPRECATION")
                Divider(color = DarkBorder, thickness = 1.dp)
                Spacer(Modifier.height(10.dp))

                if (sets.isEmpty()) {
                    Text("Aucune serie", color = TextMuted, fontSize = 12.sp)
                } else {
                    val grouped = sets.groupBy { it.exerciseName }
                    grouped.forEach { (name, exerciseSets) ->
                        Text(
                            name,
                            color = Blue400,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        exerciseSets.forEach { set ->
                            SetRow(
                                setNumber = set.setNumber,
                                weight = set.weight,
                                reps = set.reps,
                                setType = set.setType
                            )
                            Spacer(Modifier.height(3.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

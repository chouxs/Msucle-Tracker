package com.lad.muscletracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lad.muscletracker.data.entity.Exercise
import com.lad.muscletracker.data.entity.Goal
import com.lad.muscletracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalsScreen(
    goals: List<Goal>,
    exercises: List<Exercise>,
    onAddGoal: (title: String, exerciseId: Long?, targetWeight: Float?, targetReps: Int?) -> Unit,
    onMarkAchieved: (Goal) -> Unit,
    onDeleteGoal: (Goal) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showAchieved by remember { mutableStateOf(false) }

    val activeGoals = goals.filter { !it.isAchieved }
    val achievedGoals = goals.filter { it.isAchieved }

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
                "Objectifs",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${activeGoals.size} en cours",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Active goals section
            if (activeGoals.isNotEmpty()) {
                item {
                    Text(
                        "En cours",
                        color = Blue400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }

                items(activeGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        exercises = exercises,
                        isAchieved = false,
                        onMarkAchieved = { onMarkAchieved(goal) },
                        onDelete = { onDeleteGoal(goal) }
                    )
                }
            } else {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Flag,
                                    null,
                                    tint = TextMuted.copy(alpha = 0.5f),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Aucun objectif en cours",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Ajoute un objectif pour suivre ta progression",
                                    color = TextMuted.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Achieved goals section (collapsible)
            if (achievedGoals.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAchieved = !showAchieved },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Accomplis",
                            color = Green500,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "(${achievedGoals.size})",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            if (showAchieved) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }

                if (showAchieved) {
                    items(achievedGoals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            exercises = exercises,
                            isAchieved = true,
                            onMarkAchieved = {},
                            onDelete = { onDeleteGoal(goal) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Add goal button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Nouvel objectif", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    // Add goal dialog
    if (showAddDialog) {
        AddGoalDialog(
            exercises = exercises,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, exerciseId, weight, reps ->
                onAddGoal(title, exerciseId, weight, reps)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    exercises: List<Exercise>,
    isAchieved: Boolean,
    onMarkAchieved: () -> Unit,
    onDelete: () -> Unit
) {
    val linkedExercise = goal.exerciseId?.let { exId ->
        exercises.find { it.id == exId }
    }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox (only for active goals)
            if (!isAchieved) {
                Checkbox(
                    checked = false,
                    onCheckedChange = { onMarkAchieved() },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = DarkBorder,
                        checkedColor = Green500,
                        checkmarkColor = TextPrimary
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
            } else {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Accompli",
                    tint = Green500,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
            }

            // Goal content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    goal.title,
                    color = if (isAchieved) TextSecondary else TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textDecoration = if (isAchieved) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(Modifier.height(4.dp))

                // Target details
                val targetParts = mutableListOf<String>()
                goal.targetWeight?.let { targetParts.add("${it.toInt()} kg") }
                goal.targetReps?.let { targetParts.add("$it reps") }
                if (targetParts.isNotEmpty()) {
                    Text(
                        "Cible: ${targetParts.joinToString(" x ")}",
                        color = Orange500,
                        fontSize = 12.sp
                    )
                }

                // Linked exercise
                linkedExercise?.let {
                    Text(
                        it.name,
                        color = Blue400,
                        fontSize = 11.sp
                    )
                }

                // Dates
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        "Cree le ${dateFormat.format(Date(goal.createdAt))}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    goal.achievedAt?.let {
                        Text(
                            "Atteint le ${dateFormat.format(Date(it))}",
                            color = Green500.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    "Supprimer",
                    tint = Red500.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, exerciseId: Long?, targetWeight: Float?, targetReps: Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedExerciseId by remember { mutableStateOf<Long?>(null) }
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var exerciseDropdownExpanded by remember { mutableStateOf(false) }

    val selectedExerciseName = selectedExerciseId?.let { exId ->
        exercises.find { it.id == exId }?.name
    } ?: "Aucun (optionnel)"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Nouvel objectif",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de l'objectif") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Blue500,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = Blue400,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Exercise dropdown
                Text(
                    "Exercice lie",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(4.dp))

                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { exerciseDropdownExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedExerciseName,
                                color = if (selectedExerciseId != null) TextPrimary else TextMuted,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = exerciseDropdownExpanded,
                        onDismissRequest = { exerciseDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Aucun (optionnel)") },
                            onClick = {
                                selectedExerciseId = null
                                exerciseDropdownExpanded = false
                            }
                        )
                        exercises.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise.name) },
                                onClick = {
                                    selectedExerciseId = exercise.id
                                    exerciseDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Weight and reps (optional)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Poids (kg)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Blue500,
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = Blue400,
                            unfocusedLabelColor = TextMuted
                        )
                    )
                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Blue500,
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = Blue400,
                            unfocusedLabelColor = TextMuted
                        )
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    "Poids et reps sont optionnels",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val weight = weightText.toFloatOrNull()
                                val reps = repsText.toIntOrNull()
                                onConfirm(title.trim(), selectedExerciseId, weight, reps)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

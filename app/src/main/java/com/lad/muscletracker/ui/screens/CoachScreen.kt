package com.lad.muscletracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.PersonalRecord
import com.lad.muscletracker.ui.theme.*
import com.lad.muscletracker.viewmodel.WorkoutViewModel.CoachExerciseTarget
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CoachScreen(
    coachTargets: List<CoachExerciseTarget>,
    personalRecords: List<PersonalRecord>,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var showCalculator by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCalculator = true },
                containerColor = Blue600
            ) {
                Icon(Icons.Default.Calculate, "Calculateur 1RM")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                    "Coach",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Blue500.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${coachTargets.size}", color = Blue500, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Exercices suivis", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Orange500.copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${personalRecords.size}", color = Orange500, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Records personnels", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (coachTargets.isEmpty()) {
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
                        Text("Fais ta premiere seance", color = TextMuted, fontSize = 14.sp)
                        Text("Le coach analysera tes performances", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                // Group by muscle group
                val grouped = coachTargets.groupBy { it.muscleGroup }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    grouped.forEach { (muscleGroup, exercises) ->
                        item(key = "header_$muscleGroup") {
                            val groupColor = when (muscleGroup) {
                                "Pecs" -> Red500
                                "Dos" -> Blue500
                                "Jambes" -> Green500
                                "Epaules" -> Orange500
                                "Bras" -> Purple500
                                "Avant-bras" -> Teal500
                                "Abdos" -> Blue400
                                else -> TextSecondary
                            }
                            Text(
                                muscleGroup,
                                color = groupColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(exercises, key = { it.exerciseId }) { target ->
                            CoachExerciseCard(target)
                        }
                    }
                }
            }
        }
    }

    if (showCalculator) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showCalculator = false }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    var weightText by remember { mutableStateOf("") }
                    var repsText by remember { mutableStateOf("") }

                    Text(
                        "Calculateur 1RM",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "Formule d'Epley",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Poids (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it },
                        label = { Text("Repetitions") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    val weight = weightText.replace(",", ".").toFloatOrNull()
                    val reps = repsText.toIntOrNull()
                    val oneRM = if (weight != null && reps != null && reps > 0) {
                        weight * (1 + reps / 30f)
                    } else null

                    if (oneRM != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Blue600.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("1RM estime", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    "%.1f kg".format(oneRM),
                                    color = Blue400,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text("Tableau de charges", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))

                        listOf(
                            1 to 100, 3 to 93, 5 to 87, 8 to 80, 10 to 75, 12 to 70, 15 to 65
                        ).forEach { (rep, pct) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${rep}RM (${pct}%)", color = TextMuted, fontSize = 13.sp)
                                Text("%.1f kg".format(oneRM * pct / 100f), fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    TextButton(
                        onClick = { showCalculator = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Fermer", color = Blue400)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachExerciseCard(target: CoachExerciseTarget) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.FRANCE)
    val groupColor = when (target.muscleGroup) {
        "Pecs" -> Red500
        "Dos" -> Blue500
        "Jambes" -> Green500
        "Epaules" -> Orange500
        "Bras" -> Purple500
        "Avant-bras" -> Teal500
        "Abdos" -> Blue400
        else -> TextSecondary
    }

    // Determine if user should increase weight (all sets at ceiling)
    val isWeightIncrease = target.nextWeight > target.lastWeight
    val instructionColor = if (isWeightIncrease) Green500 else Orange500
    val instructionBg = if (isWeightIncrease) Green500.copy(alpha = 0.1f) else Orange500.copy(alpha = 0.1f)
    val instructionIcon = if (isWeightIncrease) Icons.Default.FitnessCenter else Icons.Default.TrendingUp

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = groupColor,
                    modifier = Modifier.size(4.dp, 20.dp)
                ) {}
                Spacer(Modifier.width(8.dp))
                Text(
                    target.exerciseName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                // Rep range badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Blue500.copy(alpha = 0.1f)
                ) {
                    Text(
                        "${target.targetRepsMin}-${target.targetRepsMax} reps",
                        color = Blue400,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (target.isNewPR) {
                    Spacer(Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Green500.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Green500, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("PR!", color = Green500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Last performance
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Derniere perf: ${target.lastWeight}kg x ${target.lastReps}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    dateFormat.format(Date(target.lastDate)),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            // Next target - green for weight increase, orange for rep progression
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = instructionBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(instructionIcon, null, tint = instructionColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(target.nextInstruction, color = instructionColor, fontSize = 12.sp)
                }
            }

            // PR display
            if (target.personalRecord != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Record: ${target.personalRecord}kg",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

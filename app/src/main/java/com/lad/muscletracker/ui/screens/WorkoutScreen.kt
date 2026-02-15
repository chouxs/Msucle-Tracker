package com.lad.muscletracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.SetWithExerciseName
import com.lad.muscletracker.data.dao.TemplateExerciseWithDetails
import com.lad.muscletracker.data.entity.Exercise
import com.lad.muscletracker.ui.components.RestTimer
import com.lad.muscletracker.ui.components.SetRow
import com.lad.muscletracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    sets: List<SetWithExerciseName>,
    exercises: List<Exercise>,
    templateExercises: List<TemplateExerciseWithDetails>,
    restTimerSeconds: Int,
    isRestTimerRunning: Boolean,
    progressionHints: Map<Long, String>,
    isEditing: Boolean = false,
    workoutElapsedSeconds: Int = 0,
    onAddSet: (exerciseId: Long, weight: Float, reps: Int, setType: String) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onStartTimer: (Int) -> Unit,
    onStopTimer: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    var showExercisePicker by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedSetType by remember { mutableStateOf("working") }
    var exerciseSearchQuery by remember { mutableStateOf("") }

    // Group sets by exercise
    val groupedSets = sets.groupBy { it.exerciseId }

    // If template exercises exist, use them as the checklist
    val isTemplateWorkout = templateExercises.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Retour", tint = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (isEditing) "Modifier seance" else "Seance en cours",
                    color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                if (!isEditing && workoutElapsedSeconds > 0) {
                    val hours = workoutElapsedSeconds / 3600
                    val minutes = (workoutElapsedSeconds % 3600) / 60
                    val secs = workoutElapsedSeconds % 60
                    val timeText = if (hours > 0) "${hours}h ${minutes}m"
                        else "${minutes}:${String.format("%02d", secs)}"
                    Text(timeText, color = Blue400, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            TextButton(
                onClick = onFinish,
                colors = ButtonDefaults.textButtonColors(contentColor = Green500)
            ) {
                Text(if (isEditing) "Sauvegarder" else "Terminer", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Rest timer
        RestTimer(
            seconds = restTimerSeconds,
            isRunning = isRestTimerRunning,
            onStart = onStartTimer,
            onStop = onStopTimer
        )

        Spacer(Modifier.height(12.dp))

        // Exercise list with sets
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isTemplateWorkout) {
                // Template-based workout: show each exercise from template
                items(templateExercises, key = { it.id }) { templateEx ->
                    val exerciseSets = groupedSets[templateEx.exerciseId] ?: emptyList()
                    val completedSets = exerciseSets.size
                    val isDone = completedSets >= templateEx.targetSets
                    val hint = progressionHints[templateEx.exerciseId]

                    TemplateExerciseCard(
                        templateExercise = templateEx,
                        completedSets = completedSets,
                        sets = exerciseSets,
                        isDone = isDone,
                        progressionHint = hint,
                        onSelectForAdd = {
                            selectedExercise = exercises.find { it.id == templateEx.exerciseId }
                            // Auto-start rest timer based on exercise type
                            if (completedSets > 0 && !isRestTimerRunning) {
                                onStartTimer(templateEx.restSeconds)
                            }
                        },
                        onDeleteSet = onDeleteSet
                    )
                }
            } else {
                // Free workout: show grouped sets as before
                groupedSets.forEach { (exerciseId, exerciseSets) ->
                    val exerciseName = exerciseSets.firstOrNull()?.exerciseName ?: "?"
                    val muscleGroup = exerciseSets.firstOrNull()?.muscleGroup ?: ""

                    item(key = "header_$exerciseId") {
                        FreeExerciseCard(
                            exerciseName = exerciseName,
                            muscleGroup = muscleGroup,
                            sets = exerciseSets,
                            onDeleteSet = onDeleteSet
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Quick add section
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Exercise selector
                Surface(
                    onClick = { showExercisePicker = true },
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FitnessCenter, null, tint = Blue400, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            selectedExercise?.name ?: "Choisir un exercice",
                            color = if (selectedExercise != null) TextPrimary else TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Progression hint
                selectedExercise?.let { ex ->
                    val hint = progressionHints[ex.id]
                    if (hint != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Orange500.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TrendingUp, null, tint = Orange500, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(hint, color = Orange500, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Set type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "working" to "Normal",
                        "warmup" to "Echauf.",
                        "dropset" to "Drop",
                        "failure" to "Echec"
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = selectedSetType == type,
                            onClick = { selectedSetType = type },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.height(28.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (type) {
                                    "warmup" -> Blue400.copy(alpha = 0.3f)
                                    "dropset" -> Orange500.copy(alpha = 0.3f)
                                    "failure" -> Red500.copy(alpha = 0.3f)
                                    else -> Blue600.copy(alpha = 0.3f)
                                }
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Weight + Reps
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Poids (kg)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Orange500,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it },
                        label = { Text("Reps", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Add button
                Button(
                    onClick = {
                        val exercise = selectedExercise ?: return@Button
                        val weight = weightText.replace(",", ".").toFloatOrNull() ?: return@Button
                        val reps = repsText.toIntOrNull() ?: return@Button
                        onAddSet(exercise.id, weight, reps, selectedSetType)
                        weightText = ""
                        repsText = ""
                        selectedSetType = "working"
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = selectedExercise != null && weightText.isNotBlank() && repsText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Ajouter serie", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Exercise picker bottom sheet
    if (showExercisePicker) {
        val pickerExercises = if (isTemplateWorkout) {
            val templateExIds = templateExercises.map { it.exerciseId }.toSet()
            val templateList = exercises.filter { it.id in templateExIds }
            val otherList = exercises.filter { it.id !in templateExIds }
            templateList + otherList
        } else {
            exercises
        }
        val groups = pickerExercises.map { it.muscleGroup }.distinct().sorted()

        ModalBottomSheet(
            onDismissRequest = {
                showExercisePicker = false
                selectedGroup = null
                exerciseSearchQuery = ""
            },
            containerColor = DarkSurface
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.6f)) {
                Text("Choisir un exercice", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = exerciseSearchQuery,
                    onValueChange = { exerciseSearchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Rechercher...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedGroup == null,
                        onClick = { selectedGroup = null },
                        label = { Text("Tous", fontSize = 11.sp) }
                    )
                    groups.take(5).forEach { group ->
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = if (selectedGroup == group) null else group },
                            label = { Text(group, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                val filteredExercises = pickerExercises.filter { exercise ->
                    val matchesGroup = selectedGroup == null || exercise.muscleGroup == selectedGroup
                    val matchesSearch = exerciseSearchQuery.isBlank() ||
                        exercise.name.contains(exerciseSearchQuery, ignoreCase = true)
                    matchesGroup && matchesSearch
                }

                LazyColumn {
                    items(filteredExercises) { exercise ->
                        com.lad.muscletracker.ui.components.ExerciseCard(
                            exercise = exercise,
                            onClick = {
                                selectedExercise = exercise
                                showExercisePicker = false
                                selectedGroup = null
                                exerciseSearchQuery = ""
                            }
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateExerciseCard(
    templateExercise: TemplateExerciseWithDetails,
    completedSets: Int,
    sets: List<SetWithExerciseName>,
    isDone: Boolean,
    progressionHint: String?,
    onSelectForAdd: () -> Unit,
    onDeleteSet: (Long) -> Unit
) {
    val groupColor = when (templateExercise.muscleGroup) {
        "Pecs" -> Red500
        "Dos" -> Blue500
        "Jambes" -> Green500
        "Epaules" -> Orange500
        "Bras" -> Purple500
        "Abdos" -> Blue400
        else -> TextSecondary
    }

    val typeLabel = if (templateExercise.exerciseType == "compound") "Compose" else "Isolation"

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDone) Green500.copy(alpha = 0.08f) else DarkCard,
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        templateExercise.exerciseName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            templateExercise.muscleGroup,
                            color = groupColor,
                            fontSize = 10.sp
                        )
                        Text(
                            typeLabel,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                Text(
                    "$completedSets / ${templateExercise.targetSets}",
                    color = if (isDone) Green500 else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (isDone) {
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.CheckCircle, null, tint = Green500, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Objectif: ${templateExercise.targetSets}x${templateExercise.targetRepsMin}-${templateExercise.targetRepsMax} | Repos: ${templateExercise.restSeconds}s",
                color = TextMuted,
                fontSize = 11.sp
            )

            if (progressionHint != null) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Orange500.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingUp, null, tint = Orange500, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(progressionHint, color = Orange500, fontSize = 11.sp)
                    }
                }
            }

            if (sets.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                sets.forEach { set ->
                    SetRow(
                        setNumber = set.setNumber,
                        weight = set.weight,
                        reps = set.reps,
                        setType = set.setType,
                        onDelete = { onDeleteSet(set.id) }
                    )
                    Spacer(Modifier.height(3.dp))
                }
            }

            if (!isDone) {
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = onSelectForAdd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ajouter serie", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FreeExerciseCard(
    exerciseName: String,
    muscleGroup: String,
    sets: List<SetWithExerciseName>,
    onDeleteSet: (Long) -> Unit
) {
    val groupColor = when (muscleGroup) {
        "Pecs" -> Red500
        "Dos" -> Blue500
        "Jambes" -> Green500
        "Epaules" -> Orange500
        "Bras" -> Purple500
        "Abdos" -> Blue400
        else -> TextSecondary
    }

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
                    exerciseName,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    muscleGroup,
                    color = groupColor,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            sets.forEach { set ->
                SetRow(
                    setNumber = set.setNumber,
                    weight = set.weight,
                    reps = set.reps,
                    setType = set.setType,
                    onDelete = { onDeleteSet(set.id) }
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

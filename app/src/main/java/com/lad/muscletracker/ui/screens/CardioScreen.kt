package com.lad.muscletracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.entity.CardioSession
import com.lad.muscletracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardioScreen(
    sessions: List<CardioSession>,
    weeklyCalories: Int,
    weeklyDistance: Float,
    userWeightKg: Float,
    onAddSession: (type: String, speedKmh: Float, durationMinutes: Int, inclinePercent: Float) -> Unit,
    onUpdateSession: (session: CardioSession, speedKmh: Float, durationMinutes: Int, inclinePercent: Float) -> Unit = { _, _, _, _ -> },
    onDeleteSession: (CardioSession) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var sessionToDelete by remember { mutableStateOf<CardioSession?>(null) }
    var editingSession by remember { mutableStateOf<CardioSession?>(null) }

    var selectedType by remember { mutableStateOf("walk") }
    var speedText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var inclineText by remember { mutableStateOf("") }

    // Pre-fill form when editing
    LaunchedEffect(editingSession) {
        editingSession?.let { session ->
            selectedType = session.type
            speedText = "%.1f".format(session.avgSpeedKmh)
            durationText = session.durationMinutes.toString()
            inclineText = if (session.inclinePercent > 0) "%.1f".format(session.inclinePercent) else ""
        }
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE) }

    val liveCalories by remember(selectedType, speedText, durationText, inclineText, userWeightKg) {
        derivedStateOf {
            val speed = speedText.replace(",", ".").toFloatOrNull() ?: 0f
            val duration = durationText.toIntOrNull() ?: 0
            val incline = inclineText.replace(",", ".").toFloatOrNull() ?: 0f

            if (duration <= 0 || speed <= 0f) {
                0
            } else {
                val met = if (selectedType == "walk") {
                    3.5f + 0.18f * incline
                } else {
                    when {
                        speed <= 8f -> 6f
                        speed <= 9.5f -> 8.3f
                        speed <= 11f -> 9.8f
                        speed <= 12.5f -> 11f
                        speed <= 14f -> 12.8f
                        speed <= 16f -> 14.5f
                        else -> 16f
                    }
                }
                ((met * userWeightKg * duration * 3.5f) / 200f).toInt()
            }
        }
    }

    val liveDistance by remember(speedText, durationText) {
        derivedStateOf {
            val speed = speedText.replace(",", ".").toFloatOrNull() ?: 0f
            val duration = durationText.toIntOrNull() ?: 0
            speed * (duration / 60f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            Text("Cardio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        Spacer(Modifier.height(16.dp))

        // Weekly stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Orange500.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$weeklyCalories kcal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Orange500, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text("Brulees cette semaine", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Green500.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("%.1f km".format(weeklyDistance), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Green500, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text("Distance semaine", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Add/Edit session card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (editingSession != null) "Modifier session" else "Nouvelle session",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                    )
                    if (editingSession != null) {
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = {
                            editingSession = null
                            selectedType = "walk"
                            speedText = ""
                            durationText = ""
                            inclineText = ""
                        }) {
                            Text("Annuler", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Type selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == "walk",
                        onClick = { selectedType = "walk" },
                        label = { Text("Marche") },
                        enabled = editingSession == null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue600,
                            selectedLabelColor = TextPrimary,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = selectedType == "run",
                        onClick = { selectedType = "run" },
                        label = { Text("Course") },
                        enabled = editingSession == null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue600,
                            selectedLabelColor = TextPrimary,
                            labelColor = TextSecondary
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Speed
                OutlinedTextField(
                    value = speedText,
                    onValueChange = { speedText = it },
                    label = { Text("Vitesse (km/h)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500, unfocusedBorderColor = DarkBorder, cursorColor = Blue400
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Duration
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duree (min)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500, unfocusedBorderColor = DarkBorder, cursorColor = Blue400
                    ),
                    singleLine = true
                )

                // Incline (only for walk)
                if (selectedType == "walk") {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inclineText,
                        onValueChange = { inclineText = it },
                        label = { Text("Inclinaison (%)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Blue500, unfocusedBorderColor = DarkBorder, cursorColor = Blue400
                        ),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Live preview: distance + calories
                if (liveCalories > 0 || liveDistance > 0f) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (liveDistance > 0f) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Straighten, null, tint = Green500, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("%.2f km".format(liveDistance), color = Green500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        if (liveCalories > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, null, tint = Orange500, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("~$liveCalories kcal", color = Orange500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Save/Update button
                Button(
                    onClick = {
                        val speed = speedText.replace(",", ".").toFloatOrNull() ?: 0f
                        val duration = durationText.toIntOrNull() ?: 0
                        val incline = inclineText.replace(",", ".").toFloatOrNull() ?: 0f
                        if (speed > 0f && duration > 0) {
                            val editing = editingSession
                            if (editing != null) {
                                onUpdateSession(editing, speed, duration, incline)
                                editingSession = null
                            } else {
                                onAddSession(selectedType, speed, duration, incline)
                            }
                            speedText = ""
                            durationText = ""
                            inclineText = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (editingSession != null) Orange500 else Blue600
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        if (editingSession != null) Icons.Default.Save else Icons.Default.Add,
                        null, modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (editingSession != null) "Mettre a jour" else "Enregistrer",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Session history
        if (sessions.isNotEmpty()) {
            Text("Historique", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(8.dp))

            sessions.forEach { session ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (editingSession?.id == session.id) Blue600.copy(alpha = 0.15f) else DarkCard
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (session.type == "walk") Icons.Default.DirectionsWalk else Icons.Default.DirectionsRun,
                                null,
                                tint = if (session.type == "walk") Green500 else Blue400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (session.type == "walk") "Marche" else "Course",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                            )
                            Spacer(Modifier.weight(1f))
                            Text(dateFormat.format(Date(session.date)), fontSize = 13.sp, color = TextMuted)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("%.1f km".format(session.distanceKm), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Distance", fontSize = 11.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${session.durationMinutes} min", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Duree", fontSize = 11.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("%.1f km/h".format(session.avgSpeedKmh), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Vitesse", fontSize = 11.sp, color = TextMuted)
                            }
                            if (session.type == "walk" && session.inclinePercent > 0) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("%.1f%%".format(session.inclinePercent), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Inclinaison", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = Orange500, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${session.caloriesBurned} kcal", color = Orange500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { editingSession = session }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, "Modifier", tint = Blue400, modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = { sessionToDelete = session }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, "Supprimer", tint = Red500, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Supprimer cette seance cardio?", color = TextPrimary) },
            text = { Text("Cette action est irreversible.", color = TextSecondary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    onDeleteSession(session)
                    sessionToDelete = null
                }) { Text("Supprimer", color = Red500) }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) { Text("Annuler", color = TextSecondary) }
            }
        )
    }
}

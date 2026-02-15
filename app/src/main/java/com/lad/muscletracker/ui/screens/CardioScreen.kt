package com.lad.muscletracker.ui.screens

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
    onAddSession: (type: String, distanceKm: Float, durationMinutes: Int, inclinePercent: Float) -> Unit,
    onDeleteSession: (CardioSession) -> Unit,
    onBack: () -> Unit
) {
    var sessionToDelete by remember { mutableStateOf<CardioSession?>(null) }
    var selectedType by remember { mutableStateOf("walk") }
    var distanceText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var inclineText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.FRANCE) }

    val liveCalories by remember(selectedType, distanceText, durationText, inclineText, userWeightKg) {
        derivedStateOf {
            val distance = distanceText.toFloatOrNull() ?: 0f
            val duration = durationText.toIntOrNull() ?: 0
            val incline = inclineText.toFloatOrNull() ?: 0f

            if (duration <= 0 || distance <= 0f) {
                0
            } else {
                val met = if (selectedType == "walk") {
                    3.5f + 0.18f * incline
                } else {
                    val speed = distance / (duration / 60f)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Cardio",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Weekly stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: weekly calories
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Orange500.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$weeklyCalories kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange500,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Brulees cette semaine",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Right: weekly distance
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Green500.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "%.1f km".format(weeklyDistance),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green500,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Distance semaine",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Add session card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = DarkCard
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Nouvelle session",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Type selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "walk",
                        onClick = { selectedType = "walk" },
                        label = { Text("Marche") },
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
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue600,
                            selectedLabelColor = TextPrimary,
                            labelColor = TextSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Distance
                OutlinedTextField(
                    value = distanceText,
                    onValueChange = { distanceText = it },
                    label = { Text("Distance (km)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue400
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duree (min)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue400
                    ),
                    singleLine = true
                )

                // Incline (only for walk)
                if (selectedType == "walk") {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inclineText,
                        onValueChange = { inclineText = it },
                        label = { Text("Inclinaison (%)", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder,
                            cursorColor = Blue400
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live calorie preview
                if (liveCalories > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Orange500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "~$liveCalories kcal",
                            color = Orange500,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Save button
                Button(
                    onClick = {
                        val distance = distanceText.toFloatOrNull() ?: 0f
                        val duration = durationText.toIntOrNull() ?: 0
                        val incline = inclineText.toFloatOrNull() ?: 0f
                        if (distance > 0f && duration > 0) {
                            onAddSession(selectedType, distance, duration, incline)
                            distanceText = ""
                            durationText = ""
                            inclineText = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Enregistrer",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Session history
        if (sessions.isNotEmpty()) {
            Text(
                text = "Historique",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            sessions.forEach { session ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Type icon + date row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (session.type == "walk")
                                    Icons.Default.DirectionsWalk
                                else
                                    Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = if (session.type == "walk") Green500 else Blue400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (session.type == "walk") "Marche" else "Course",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = dateFormat.format(Date(session.date)),
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "%.1f km".format(session.distanceKm),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Distance",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${session.durationMinutes} min",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Duree",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "%.1f km/h".format(session.avgSpeedKmh),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Vitesse",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            if (session.type == "walk") {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f%%".format(session.inclinePercent),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Inclinaison",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calories + delete row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Orange500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${session.caloriesBurned} kcal",
                                color = Orange500,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { sessionToDelete = session },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = Red500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(16.dp))
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

package com.lad.muscletracker.ui.screens

import androidx.activity.compose.BackHandler
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.entity.UserProfile
import com.lad.muscletracker.data.entity.WeightEntry
import com.lad.muscletracker.ui.theme.*
import com.lad.muscletracker.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profile: UserProfile?,
    calorieResult: WorkoutViewModel.CalorieResult?,
    weightHistory: List<WeightEntry>,
    onSaveProfile: (UserProfile) -> Unit,
    onAddWeight: (Float, Float?) -> Unit,
    onDeleteWeight: (WeightEntry) -> Unit,
    onExportData: (onResult: (String) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    val context = LocalContext.current
    var exportJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null && exportJson != null) {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(exportJson!!.toByteArray())
            }
            exportJson = null
            Toast.makeText(context, "Export reussi!", Toast.LENGTH_SHORT).show()
        }
    }

    // Form state
    var weightKg by remember { mutableStateOf("") }
    var heightCm by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var activityLevel by remember { mutableStateOf("moderate") }
    var goal by remember { mutableStateOf("maintenance") }

    // Weight tracking form state
    var newWeight by remember { mutableStateOf("") }
    var newBodyFat by remember { mutableStateOf("") }

    // Initialize form state from profile
    LaunchedEffect(profile) {
        profile?.let {
            weightKg = it.weightKg.toString()
            heightCm = it.heightCm.toString()
            age = it.age.toString()
            gender = it.gender
            activityLevel = it.activityLevel
            goal = it.goal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Profil & Nutrition",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Profile form card ──
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = Blue400
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Informations personnelles",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Weight
                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    label = { Text("Poids (kg)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue500
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Height
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = { Text("Taille (cm)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue500
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // Age
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue500
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Gender
                Text("Sexe", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = gender == "male",
                        onClick = { gender = "male" },
                        label = { Text("Homme") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue600,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = gender == "female",
                        onClick = { gender = "female" },
                        label = { Text("Femme") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue600,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Activity level
                Text("Niveau d'activite", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                val activityOptions = listOf(
                    "Sedentaire" to "sedentary",
                    "Leger" to "light",
                    "Modere" to "moderate",
                    "Actif" to "active",
                    "Tres actif" to "very_active"
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activityOptions.forEach { (label, value) ->
                        FilterChip(
                            selected = activityLevel == value,
                            onClick = { activityLevel = value },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue600,
                                selectedLabelColor = TextPrimary,
                                containerColor = DarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Goal
                Text("Objectif", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = goal == "deficit",
                        onClick = { goal = "deficit" },
                        label = { Text("Deficit") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Red500,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = goal == "maintenance",
                        onClick = { goal = "maintenance" },
                        label = { Text("Maintenance") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green500,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = goal == "bulk",
                        onClick = { goal = "bulk" },
                        label = { Text("Prise de masse") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple500,
                            selectedLabelColor = TextPrimary,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        val w = weightKg.toFloatOrNull() ?: 70f
                        val h = heightCm.toFloatOrNull() ?: 175f
                        val a = age.toIntOrNull() ?: 25
                        onSaveProfile(
                            UserProfile(
                                id = 1,
                                weightKg = w,
                                heightCm = h,
                                age = a,
                                gender = gender,
                                activityLevel = activityLevel,
                                goal = goal
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sauvegarder",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Calorie result card ──
        if (calorieResult != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${calorieResult.targetCalories} kcal/jour",
                        color = Orange500,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        calorieResult.goalLabel,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // BMR | TDEE | Cible mini stats row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CalorieStatBox(
                            label = "BMR",
                            value = "${calorieResult.bmr}",
                            color = Blue400,
                            modifier = Modifier.weight(1f)
                        )
                        CalorieStatBox(
                            label = "TDEE",
                            value = "${calorieResult.tdee}",
                            color = Green500,
                            modifier = Modifier.weight(1f)
                        )
                        CalorieStatBox(
                            label = "Cible",
                            value = "${calorieResult.targetCalories}",
                            color = Orange500,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Macros section
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroItem(
                                label = "Proteines",
                                value = "${calorieResult.proteinG}g",
                                color = Blue400
                            )
                            MacroItem(
                                label = "Lipides",
                                value = "${calorieResult.fatG}g",
                                color = Orange500
                            )
                            MacroItem(
                                label = "Glucides",
                                value = "${calorieResult.carbsG}g",
                                color = Green500
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // ── Weight tracking section ──
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Scale,
                contentDescription = null,
                tint = Blue400
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Suivi du poids",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        // Weight entry form
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text("Poids (kg)", color = TextMuted, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue500
                    ),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = newBodyFat,
                    onValueChange = { newBodyFat = it },
                    label = { Text("BF %", color = TextMuted, fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = Blue500
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val w = newWeight.toFloatOrNull()
                        if (w != null) {
                            val bf = newBodyFat.toFloatOrNull()
                            onAddWeight(w, bf)
                            newWeight = ""
                            newBodyFat = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Ajouter",
                        tint = TextPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Weight history list
        if (weightHistory.isEmpty()) {
            Text(
                "Aucune entree de poids enregistree",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            weightHistory.forEach { entry ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                dateFormat.format(Date(entry.date)),
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "${entry.weightKg} kg",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (entry.bodyFatPercent != null) {
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "${entry.bodyFatPercent}% BF",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { onDeleteWeight(entry) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Supprimer",
                                tint = Red500
                            )
                        }
                    }
                }
            }
        }

        // ── Export section ──
        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                onExportData { json ->
                    exportJson = json
                    exportLauncher.launch("muscletracker_export.json")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.FileDownload, contentDescription = null, tint = Blue400)
            Spacer(Modifier.width(8.dp))
            Text("Exporter les donnees (JSON)", color = TextPrimary)
        }

        // Bottom padding for scrolling clearance
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun CalorieStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun MacroItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

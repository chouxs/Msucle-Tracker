package com.lad.muscletracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.dao.WeeklyMuscleVolume
import com.lad.muscletracker.data.entity.Workout
import com.lad.muscletracker.data.entity.WorkoutTemplate
import com.lad.muscletracker.ui.theme.*
import com.lad.muscletracker.viewmodel.VolumeLandmarks
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    lastWorkout: Workout?,
    totalWorkouts: Int,
    weeklyWorkoutCount: Int,
    todayTemplates: List<WorkoutTemplate>,
    allTemplates: List<WorkoutTemplate>,
    personalRecords: List<com.lad.muscletracker.data.dao.PersonalRecord>,
    weeklyVolume: List<WeeklyMuscleVolume> = emptyList(),
    workoutStreak: Int = 0,
    onStartWorkoutFromTemplate: (WorkoutTemplate) -> Unit,
    onStartFreeWorkout: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenVolume: () -> Unit,
    onOpenCoach: () -> Unit,
    onOpenSupplements: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenCardio: () -> Unit,
    onOpenCalendar: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            "Muscle Unrecord",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Arnold Split Programme",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(20.dp))

        // Today's template
        val dayName = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lundi"
            Calendar.TUESDAY -> "Mardi"
            Calendar.WEDNESDAY -> "Mercredi"
            Calendar.THURSDAY -> "Jeudi"
            Calendar.FRIDAY -> "Vendredi"
            Calendar.SATURDAY -> "Samedi"
            Calendar.SUNDAY -> "Dimanche"
            else -> ""
        }

        if (todayTemplates.isNotEmpty()) {
            Text(
                "Seance du jour — $dayName",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            todayTemplates.forEach { template ->
                Button(
                    onClick = { onStartWorkoutFromTemplate(template) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(template.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(template.description, fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        } else {
            Text(
                "$dayName — Jour de repos",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
        }

        // Free workout button
        OutlinedButton(
            onClick = onStartFreeWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Seance libre", fontSize = 14.sp)
        }

        Spacer(Modifier.height(20.dp))

        // Other templates section (all templates except today's)
        val todayIds = todayTemplates.map { it.id }.toSet()
        val otherTemplates = allTemplates.filter { it.id !in todayIds }

        if (otherTemplates.isNotEmpty()) {
            var showOtherTemplates by remember { mutableStateOf(false) }

            Surface(
                onClick = { showOtherTemplates = !showOtherTemplates },
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Autres seances",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (showOtherTemplates) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (showOtherTemplates) {
                Spacer(Modifier.height(8.dp))
                otherTemplates.forEach { template ->
                    val templateDayName = when (template.dayOfWeek) {
                        2 -> "Lun"
                        3 -> "Mar"
                        4 -> "Mer"
                        5 -> "Jeu"
                        6 -> "Ven"
                        7 -> "Sam"
                        1 -> "Dim"
                        else -> "Libre"
                    }
                    OutlinedButton(
                        onClick = { onStartWorkoutFromTemplate(template) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            templateDayName,
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(template.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(template.description, fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total",
                value = "$totalWorkouts",
                icon = Icons.Default.CalendarMonth,
                color = Blue500,
                modifier = Modifier.weight(1f),
                onClick = onOpenHistory
            )
            StatCard(
                title = "Cette semaine",
                value = "$weeklyWorkoutCount",
                icon = Icons.Default.TrendingUp,
                color = Green500,
                modifier = Modifier.weight(1f),
                onClick = onOpenCalendar
            )
            StatCard(
                title = if (workoutStreak <= 1) "semaine" else "semaines",
                value = "$workoutStreak",
                icon = Icons.Default.LocalFireDepartment,
                color = Orange500,
                modifier = Modifier.weight(1f),
                onClick = onOpenCalendar
            )
        }

        Spacer(Modifier.height(20.dp))

        // Last workout card
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Derniere seance",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (lastWorkout != null) {
                Surface(
                    onClick = onOpenHistory,
                    shape = RoundedCornerShape(8.dp),
                    color = Blue500.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Voir tout", color = Blue400, fontSize = 11.sp)
                        Spacer(Modifier.width(2.dp))
                        Icon(Icons.Default.ChevronRight, null, tint = Blue400, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (lastWorkout != null) {
            Surface(
                onClick = onOpenHistory,
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Blue400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            lastWorkout.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
                        Text(
                            dateFormat.format(Date(lastWorkout.date)),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        if (lastWorkout.durationSeconds > 0) {
                            val min = lastWorkout.durationSeconds / 60
                            Text(
                                "${min} min",
                                color = Blue400,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        "Aucune seance enregistree",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Personal Records
        if (personalRecords.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Records personnels",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    onClick = onOpenCoach,
                    shape = RoundedCornerShape(8.dp),
                    color = Orange500.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Voir tout", color = Orange500, fontSize = 11.sp)
                        Spacer(Modifier.width(2.dp))
                        Icon(Icons.Default.ChevronRight, null, tint = Orange500, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            Surface(
                onClick = onOpenCoach,
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    personalRecords.take(3).forEach { pr ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Orange500, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(pr.exerciseName, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text("${pr.maxWeight}kg", color = Orange500, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    if (personalRecords.size > 3) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "+${personalRecords.size - 3} autres records",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Weekly volume recap
        if (weeklyVolume.isNotEmpty()) {
            val volumeMap = weeklyVolume.associateBy { it.muscleGroup }
            val allGroups = VolumeLandmarks.landmarks.keys.toList()
            val belowMev = allGroups.filter { group ->
                val current = volumeMap[group]?.totalSets ?: 0
                val mev = VolumeLandmarks.landmarks[group]?.mev ?: 0
                current < mev
            }

            Text(
                "Recap volume hebdo",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    allGroups.forEach { group ->
                        val current = volumeMap[group]?.totalSets ?: 0
                        val landmark = VolumeLandmarks.landmarks[group] ?: return@forEach
                        val ratio = if (landmark.mrv > 0) current.toFloat() / landmark.mrv else 0f
                        val barColor = when {
                            current < landmark.mev -> Red500
                            current >= landmark.mrv -> Orange500
                            else -> Green500
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                group,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.width(60.dp)
                            )
                            Box(modifier = Modifier.weight(1f).height(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkBorder,
                                    modifier = Modifier.fillMaxSize()
                                ) {}
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = barColor,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                ) {}
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "$current/${landmark.mav}",
                                color = barColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp)
                            )
                        }
                    }

                    if (belowMev.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Red500.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Red500, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Volume insuffisant: ${belowMev.joinToString(", ")}",
                                    color = Red500,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Navigation grid
        Text(
            "Navigation",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))

        // Row 1: Coach + Volume
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                label = "Coach",
                icon = Icons.Default.Psychology,
                color = Orange500,
                onClick = onOpenCoach,
                modifier = Modifier.weight(1f)
            )
            NavCard(
                label = "Volume",
                icon = Icons.Default.BarChart,
                color = Green500,
                onClick = onOpenVolume,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Row 2: Cardio + Profil
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                label = "Cardio",
                icon = Icons.Default.DirectionsRun,
                color = Blue400,
                onClick = onOpenCardio,
                modifier = Modifier.weight(1f)
            )
            NavCard(
                label = "Profil",
                icon = Icons.Default.Person,
                color = Purple500,
                onClick = onOpenProfile,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Row 3: Historique + Progression
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                label = "Historique",
                icon = Icons.Default.History,
                color = Blue500,
                onClick = onOpenHistory,
                modifier = Modifier.weight(1f)
            )
            NavCard(
                label = "Progression",
                icon = Icons.Default.ShowChart,
                color = Green500,
                onClick = onOpenProgress,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Row 4: Supplements + Calendrier
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                label = "Supplements",
                icon = Icons.Default.Medication,
                color = Red500,
                onClick = onOpenSupplements,
                modifier = Modifier.weight(1f)
            )
            NavCard(
                label = "Calendrier",
                icon = Icons.Default.CalendarMonth,
                color = Purple500,
                onClick = onOpenCalendar,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(30.dp))

        // Copyright
        Text(
            "\u00A9 Unrecord 2026 — Muscle Unrecord",
            color = TextMuted.copy(alpha = 0.4f),
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun NavCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(title, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

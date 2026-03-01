package com.lad.muscletracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lad.muscletracker.data.entity.Workout
import com.lad.muscletracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    selectedMonth: Long,
    monthWorkouts: List<Workout>,
    totalWorkouts: Int,
    weeklyWorkoutCount: Int,
    onChangeMonth: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val cal = remember(selectedMonth) {
        Calendar.getInstance().apply { timeInMillis = selectedMonth }
    }

    val monthNames = listOf(
        "Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"
    )
    val monthTitle = "${monthNames[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"

    // Group workouts by day of month
    val workoutsByDay = remember(monthWorkouts) {
        val map = mutableMapOf<Int, MutableList<Workout>>()
        monthWorkouts.forEach { workout ->
            val wCal = Calendar.getInstance()
            wCal.timeInMillis = workout.date
            val day = wCal.get(Calendar.DAY_OF_MONTH)
            map.getOrPut(day) { mutableListOf() }.add(workout)
        }
        map
    }

    var selectedDay by remember(selectedMonth) { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                "Calendrier",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        // Month navigation
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onChangeMonth(-1) }) {
                    Icon(Icons.Default.ChevronLeft, "Mois precedent", tint = TextPrimary)
                }
                Text(
                    monthTitle,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onChangeMonth(1) }) {
                    Icon(Icons.Default.ChevronRight, "Mois suivant", tint = TextPrimary)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Calendar grid
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Day headers (L M M J V S D)
                val dayHeaders = listOf("L", "M", "M", "J", "V", "S", "D")
                Row(modifier = Modifier.fillMaxWidth()) {
                    dayHeaders.forEach { header ->
                        Text(
                            header,
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Calculate first day offset and days in month
                val tempCal = Calendar.getInstance()
                tempCal.timeInMillis = selectedMonth
                tempCal.set(Calendar.DAY_OF_MONTH, 1)

                // Convert Sunday=1..Saturday=7 to Monday=0..Sunday=6
                val firstDayOfWeek = when (tempCal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    Calendar.SUNDAY -> 6
                    else -> 0
                }
                val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

                // Build weeks
                val totalCells = firstDayOfWeek + daysInMonth
                val weeks = (totalCells + 6) / 7

                for (week in 0 until weeks) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (dayOfWeek in 0..6) {
                            val cellIndex = week * 7 + dayOfWeek
                            val dayNumber = cellIndex - firstDayOfWeek + 1

                            if (dayNumber in 1..daysInMonth) {
                                val hasWorkout = workoutsByDay.containsKey(dayNumber)
                                val isSelected = selectedDay == dayNumber

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Blue600.copy(alpha = 0.3f)
                                            else androidx.compose.ui.graphics.Color.Transparent
                                        )
                                        .clickable { selectedDay = dayNumber },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "$dayNumber",
                                            color = if (hasWorkout) Blue400 else TextSecondary,
                                            fontSize = 14.sp,
                                            fontWeight = if (hasWorkout) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (hasWorkout) {
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(Blue400)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats for the month
        val completedMonthWorkouts = monthWorkouts.filter { it.isCompleted }
        val totalMonthWorkouts = completedMonthWorkouts.size
        val totalMonthMinutes = completedMonthWorkouts.sumOf { it.durationSeconds } / 60
        val avgDurationMin = if (totalMonthWorkouts > 0) totalMonthMinutes / totalMonthWorkouts else 0

        // Count workout days per week in this month
        val weeksWithWorkouts = remember(monthWorkouts) {
            val weeks = mutableSetOf<Int>()
            completedMonthWorkouts.forEach { w ->
                val wCal = Calendar.getInstance()
                wCal.timeInMillis = w.date
                weeks.add(wCal.get(Calendar.WEEK_OF_YEAR))
            }
            weeks.size
        }
        val avgPerWeek = if (weeksWithWorkouts > 0) "%.1f".format(totalMonthWorkouts.toFloat() / weeksWithWorkouts) else "0"

        Text(
            "Statistiques",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Ce mois",
                value = "$totalMonthWorkouts",
                icon = Icons.Default.FitnessCenter,
                color = Blue500,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Cette semaine",
                value = "$weeklyWorkoutCount",
                icon = Icons.Default.DateRange,
                color = Green500,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total",
                value = "$totalWorkouts",
                icon = Icons.Default.EmojiEvents,
                color = Orange500,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Duree totale",
                value = if (totalMonthMinutes >= 60) "${totalMonthMinutes / 60}h${totalMonthMinutes % 60}" else "${totalMonthMinutes}m",
                icon = Icons.Default.Timer,
                color = Blue400,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Moy/seance",
                value = "${avgDurationMin}m",
                icon = Icons.Default.AvTimer,
                color = Purple500,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Moy/semaine",
                value = avgPerWeek,
                icon = Icons.Default.TrendingUp,
                color = Green500,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Selected day workouts
        val selectedDayWorkouts = selectedDay?.let { workoutsByDay[it] }

        if (selectedDay != null) {
            Text(
                "Seances du $selectedDay ${monthNames[cal.get(Calendar.MONTH)]}",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            if (selectedDayWorkouts.isNullOrEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("Jour de repos", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
                selectedDayWorkouts.forEach { workout ->
                    Surface(
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
                                null,
                                tint = Blue400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    workout.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    dateFormat.format(Date(workout.date)),
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            if (workout.durationSeconds > 0) {
                                Text(
                                    "${workout.durationSeconds / 60} min",
                                    color = Blue400,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

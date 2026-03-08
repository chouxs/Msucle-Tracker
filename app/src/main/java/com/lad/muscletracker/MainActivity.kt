package com.lad.muscletracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lad.muscletracker.ui.screens.*
import com.lad.muscletracker.ui.theme.MuscleTrackerTheme
import com.lad.muscletracker.viewmodel.WorkoutViewModel

enum class Screen {
    HOME, WORKOUT, HISTORY, PROGRESS, VOLUME, COACH, SUPPLEMENTS, PROFILE, CARDIO, CALENDAR
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()

        setContent {
            MuscleTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

@Composable
fun MainContent(viewModel: WorkoutViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
    val lastWorkout by viewModel.lastWorkout.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val currentWorkoutSets by viewModel.currentWorkoutSets.collectAsStateWithLifecycle()
    val currentWorkoutId by viewModel.currentWorkoutId.collectAsStateWithLifecycle()
    val restTimerSeconds by viewModel.restTimerSeconds.collectAsStateWithLifecycle()
    val isRestTimerRunning by viewModel.isRestTimerRunning.collectAsStateWithLifecycle()
    val exerciseProgress by viewModel.exerciseProgress.collectAsStateWithLifecycle()
    val todayTemplates by viewModel.todayTemplates.collectAsStateWithLifecycle()
    val allTemplates by viewModel.allTemplates.collectAsStateWithLifecycle()
    val weeklyWorkoutCount by viewModel.weeklyWorkoutCount.collectAsStateWithLifecycle()
    val currentTemplateExercises by viewModel.currentTemplateExercises.collectAsStateWithLifecycle()
    val progressionHints by viewModel.progressionHints.collectAsStateWithLifecycle()
    val warmupHints by viewModel.warmupHints.collectAsStateWithLifecycle()
    val liveCoachFeedback by viewModel.liveCoachFeedback.collectAsStateWithLifecycle()
    val weeklyVolume by viewModel.weeklyVolume.collectAsStateWithLifecycle()
    val coachTargets by viewModel.coachTargets.collectAsStateWithLifecycle()
    val personalRecords by viewModel.allPersonalRecords.collectAsStateWithLifecycle()
    val supplementsWithReminders by viewModel.allSupplementsWithReminders.collectAsStateWithLifecycle()

    val isEditingWorkout by viewModel.isEditingWorkout.collectAsStateWithLifecycle()
    val workoutElapsedSeconds by viewModel.workoutElapsedSeconds.collectAsStateWithLifecycle()

    // Profile & Calories
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val calorieResult by viewModel.calorieResult.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()

    // Cardio
    val allCardioSessions by viewModel.allCardioSessions.collectAsStateWithLifecycle()
    val weeklyCardioCalories by viewModel.weeklyCardioCalories.collectAsStateWithLifecycle()
    val weeklyCardioDistance by viewModel.weeklyCardioDistance.collectAsStateWithLifecycle()

    val workoutStreak by viewModel.workoutStreak.collectAsStateWithLifecycle()
    val injectedFavorites by viewModel.injectedFavorites.collectAsStateWithLifecycle()

    // Calendar
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val monthWorkouts by viewModel.monthWorkouts.collectAsStateWithLifecycle()

    var selectedExerciseIdForProgress by remember { mutableStateOf<Long?>(null) }
    var isStartingWorkout by remember { mutableStateOf(false) }

    // Navigate to WORKOUT screen whenever there's an active workout
    // Handles: new workout, editing, AND rotation/config change recovery
    LaunchedEffect(currentWorkoutId) {
        if (currentWorkoutId != null) {
            currentScreen = Screen.WORKOUT
            isStartingWorkout = false
        }
    }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                lastWorkout = lastWorkout,
                totalWorkouts = allWorkouts.size,
                weeklyWorkoutCount = weeklyWorkoutCount,
                todayTemplates = todayTemplates,
                allTemplates = allTemplates,
                personalRecords = personalRecords,
                weeklyVolume = weeklyVolume,
                workoutStreak = workoutStreak,
                onStartWorkoutFromTemplate = { template ->
                    if (!isStartingWorkout) {
                        isStartingWorkout = true
                        viewModel.startWorkoutFromTemplate(template)
                    }
                },
                onStartFreeWorkout = {
                    if (!isStartingWorkout) {
                        isStartingWorkout = true
                        viewModel.startNewWorkout("Seance ${allWorkouts.size + 1}")
                    }
                },
                onOpenHistory = { currentScreen = Screen.HISTORY },
                onOpenProgress = {
                    selectedExerciseIdForProgress = null
                    currentScreen = Screen.PROGRESS
                },
                onOpenVolume = { currentScreen = Screen.VOLUME },
                onOpenCoach = { currentScreen = Screen.COACH },
                onOpenSupplements = { currentScreen = Screen.SUPPLEMENTS },
                onOpenProfile = { currentScreen = Screen.PROFILE },
                onOpenCardio = { currentScreen = Screen.CARDIO },
                onOpenCalendar = { currentScreen = Screen.CALENDAR }
            )
        }

        Screen.WORKOUT -> {
            if (currentWorkoutId != null) {
                val returnScreen = if (isEditingWorkout) Screen.HISTORY else Screen.HOME
                WorkoutScreen(
                    sets = currentWorkoutSets,
                    exercises = allExercises,
                    templateExercises = currentTemplateExercises,
                    restTimerSeconds = restTimerSeconds,
                    isRestTimerRunning = isRestTimerRunning,
                    progressionHints = progressionHints,
                    warmupHints = warmupHints,
                    liveCoachFeedback = liveCoachFeedback,
                    isEditing = isEditingWorkout,
                    workoutElapsedSeconds = workoutElapsedSeconds,
                    injectedFavorites = injectedFavorites,
                    onAddSet = { exerciseId, weight, reps, setType ->
                        viewModel.addSet(exerciseId, weight, reps, setType)
                    },
                    onDeleteSet = { viewModel.deleteSet(it) },
                    onEditSet = { setId, weight, reps -> viewModel.updateSet(setId, weight, reps) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onStartTimer = { viewModel.startRestTimer(it) },
                    onStopTimer = { viewModel.stopRestTimer() },
                    onFinish = {
                        viewModel.finishWorkout()
                        currentScreen = returnScreen
                    },
                    onBack = {
                        viewModel.finishWorkout()
                        currentScreen = returnScreen
                    }
                )
            } else if (!isStartingWorkout) {
                // Only redirect to HOME if we're not in the middle of creating a workout
                currentScreen = Screen.HOME
            } else {
                // Show loading while workout is being created
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        Screen.HISTORY -> {
            HistoryScreen(
                workouts = allWorkouts,
                getSetsForWorkout = { viewModel.getSetsForWorkout(it) },
                onDeleteWorkout = { viewModel.deleteWorkout(it) },
                onEditWorkout = { workout ->
                    viewModel.resumeWorkout(workout.id)
                },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.PROGRESS -> {
            ProgressScreen(
                exercises = allExercises,
                progressData = exerciseProgress,
                selectedExerciseId = selectedExerciseIdForProgress,
                onSelectExercise = {
                    selectedExerciseIdForProgress = it
                    viewModel.selectExerciseForProgress(it)
                },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.VOLUME -> {
            VolumeScreen(
                weeklyVolume = weeklyVolume,
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.COACH -> {
            CoachScreen(
                coachTargets = coachTargets,
                personalRecords = personalRecords,
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.SUPPLEMENTS -> {
            SupplementsScreen(
                supplementsWithReminders = supplementsWithReminders,
                onAddSupplement = { name, dosage -> viewModel.addSupplement(name, dosage) },
                onEditSupplement = { supplement, name, dosage -> viewModel.updateSupplement(supplement, name, dosage) },
                onDeleteSupplement = { viewModel.deleteSupplement(it) },
                onAddReminder = { supplementId, hour, minute, label ->
                    viewModel.addReminder(supplementId, hour, minute, label)
                },
                onDeleteReminder = { viewModel.deleteReminder(it) },
                onToggleReminder = { viewModel.toggleReminder(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.PROFILE -> {
            ProfileScreen(
                profile = userProfile,
                calorieResult = calorieResult,
                weightHistory = weightHistory,
                onSaveProfile = { viewModel.saveProfile(it) },
                onAddWeight = { weight, bodyFat -> viewModel.addWeightEntry(weight, bodyFat) },
                onDeleteWeight = { viewModel.deleteWeightEntry(it) },
                onExportData = { onResult -> viewModel.exportAllData(onResult) },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.CARDIO -> {
            CardioScreen(
                sessions = allCardioSessions,
                weeklyCalories = weeklyCardioCalories,
                weeklyDistance = weeklyCardioDistance,
                userWeightKg = userProfile?.weightKg ?: 70f,
                onAddSession = { type, speed, duration, incline ->
                    viewModel.addCardioSession(type, speed, duration, incline)
                },
                onUpdateSession = { session, speed, duration, incline ->
                    viewModel.updateCardioSession(session, speed, duration, incline)
                },
                onDeleteSession = { viewModel.deleteCardioSession(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.CALENDAR -> {
            CalendarScreen(
                selectedMonth = selectedMonth,
                monthWorkouts = monthWorkouts,
                totalWorkouts = allWorkouts.count { it.isCompleted },
                weeklyWorkoutCount = weeklyWorkoutCount,
                onChangeMonth = { viewModel.changeMonth(it) },
                onBack = { currentScreen = Screen.HOME }
            )
        }
    }
}

package com.lad.muscletracker.viewmodel

import android.app.Application
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lad.muscletracker.data.AppDatabase
import com.lad.muscletracker.data.dao.*
import com.lad.muscletracker.data.entity.*
import com.lad.muscletracker.data.repository.WorkoutRepository
import com.lad.muscletracker.notification.SupplementAlarmScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

object VolumeLandmarks {
    data class Landmarks(val mev: Int, val mav: Int, val mrv: Int)

    val landmarks = mapOf(
        "Pecs" to Landmarks(8, 14, 20),
        "Dos" to Landmarks(8, 14, 20),
        "Epaules" to Landmarks(6, 12, 18),
        "Bras" to Landmarks(4, 10, 16),
        "Avant-bras" to Landmarks(2, 6, 12),
        "Jambes" to Landmarks(6, 12, 20),
        "Abdos" to Landmarks(0, 8, 16)
    )
}

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkoutRepository(
            db.exerciseDao(), db.workoutDao(), db.setDao(),
            db.templateDao(), db.templateExerciseDao(),
            db.supplementDao(), db.goalDao(),
            db.userProfileDao(), db.cardioDao()
        )
    }

    // Current workout state
    private val _currentWorkoutId = MutableStateFlow<Long?>(null)
    val currentWorkoutId: StateFlow<Long?> = _currentWorkoutId

    private val _workoutStartTime = MutableStateFlow<Long>(0)

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds

    private val _isRestTimerRunning = MutableStateFlow(false)
    val isRestTimerRunning: StateFlow<Boolean> = _isRestTimerRunning

    private val _selectedExerciseId = MutableStateFlow<Long?>(null)

    private val _currentTemplateId = MutableStateFlow<Long?>(null)

    private val _isEditingWorkout = MutableStateFlow(false)
    val isEditingWorkout: StateFlow<Boolean> = _isEditingWorkout

    private val _workoutElapsedSeconds = MutableStateFlow(0)
    val workoutElapsedSeconds: StateFlow<Int> = _workoutElapsedSeconds
    private var elapsedTimerJob: Job? = null
    private var restTimerJob: Job? = null

    private val vibrator = try {
        application.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    } catch (_: Exception) { null }

    // Progression hints for current workout
    private val _progressionHints = MutableStateFlow<Map<Long, String>>(emptyMap())
    val progressionHints: StateFlow<Map<Long, String>> = _progressionHints

    private val _warmupHints = MutableStateFlow<Map<Long, String>>(emptyMap())
    val warmupHints: StateFlow<Map<Long, String>> = _warmupHints

    // Data flows
    val allExercises = repository.getAllExercises().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val favoriteExercises = repository.getFavoriteExercises().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _injectedFavorites = MutableStateFlow<List<com.lad.muscletracker.data.entity.Exercise>>(emptyList())
    val injectedFavorites: StateFlow<List<com.lad.muscletracker.data.entity.Exercise>> = _injectedFavorites

    val muscleGroups = repository.getAllMuscleGroups().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allWorkouts = repository.getAllWorkouts().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val lastWorkout = repository.getLastWorkout().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // Weekly workout count
    val weeklyWorkoutCount: StateFlow<Int> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val weekEnd = cal.timeInMillis
        repository.getWeeklyWorkoutCount(weekStart, weekEnd).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )
    }

    // Templates
    val allTemplates = repository.getAllTemplates().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val todayTemplates: StateFlow<List<WorkoutTemplate>> = run {
        val todayDow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) // Sunday=1, Monday=2...
        repository.getTemplatesForDay(todayDow).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTemplateExercises: StateFlow<List<TemplateExerciseWithDetails>> = _currentTemplateId
        .flatMapLatest { id ->
            if (id != null) repository.getExercisesForTemplate(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentWorkoutSets: StateFlow<List<SetWithExerciseName>> = _currentWorkoutId
        .filterNotNull()
        .flatMapLatest { repository.getSetsForWorkout(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseProgress = _selectedExerciseId
        .filterNotNull()
        .flatMapLatest { repository.getProgressForExercise(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Supplements
    val allSupplementsWithReminders = repository.getAllSupplementsWithReminders().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Goals
    val allGoals = repository.getAllGoals().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val activeGoals = repository.getActiveGoals().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Coach data
    data class CoachExerciseTarget(
        val exerciseId: Long,
        val exerciseName: String,
        val muscleGroup: String,
        val exerciseType: String,
        val targetRepsMin: Int,
        val targetRepsMax: Int,
        val lastWeight: Float,
        val lastReps: Int,
        val lastDate: Long,
        val nextWeight: Float,
        val nextInstruction: String,
        val personalRecord: Float?,
        val isNewPR: Boolean
    )

    val allPersonalRecords = repository.getAllPersonalRecords().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val coachTargets: StateFlow<List<CoachExerciseTarget>> = repository.getAllExercisesWithLastPerformance()
        .combine(repository.getAllPersonalRecords()) { exercises, prs ->
            val prMap = prs.associateBy { it.exerciseId }
            exercises.map { ex ->
                val suggestion = getProgressionSuggestion(
                    ex.exerciseId, ex.targetRepsMin, ex.targetRepsMax, ex.exerciseType
                )
                val pr = prMap[ex.exerciseId]
                CoachExerciseTarget(
                    exerciseId = ex.exerciseId,
                    exerciseName = ex.exerciseName,
                    muscleGroup = ex.muscleGroup,
                    exerciseType = ex.exerciseType,
                    targetRepsMin = ex.targetRepsMin,
                    targetRepsMax = ex.targetRepsMax,
                    lastWeight = ex.lastWeight,
                    lastReps = ex.lastReps,
                    lastDate = ex.lastDate,
                    nextWeight = suggestion?.first ?: ex.lastWeight,
                    nextInstruction = suggestion?.second ?: "Pas encore de donnees",
                    personalRecord = pr?.maxWeight,
                    isNewPR = pr != null && ex.lastWeight >= pr.maxWeight
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly volume
    val weeklyVolume: StateFlow<List<WeeklyMuscleVolume>> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val weekEnd = cal.timeInMillis
        repository.getWeeklyVolumeByMuscle(weekStart, weekEnd).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    fun getSetsForWorkout(workoutId: Long) = repository.getSetsForWorkout(workoutId)

    // Streak counter (consecutive weeks with at least 1 workout)
    val workoutStreak: StateFlow<Int> = repository.getAllWorkoutDates()
        .map { dates -> calculateWeekStreak(dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateWeekStreak(workoutDates: List<Long>): Int {
        if (workoutDates.isEmpty()) return 0
        val weekSet = mutableSetOf<Long>()
        workoutDates.forEach { date -> weekSet.add(getWeekStart(date)) }
        val sortedWeeks = weekSet.sorted().reversed()
        val currentWeekStart = getWeekStart(System.currentTimeMillis())
        var checkWeek = currentWeekStart
        var streak = 0
        for (week in sortedWeeks) {
            if (week == checkWeek) {
                streak++
                val cal = Calendar.getInstance()
                cal.timeInMillis = checkWeek
                cal.add(Calendar.WEEK_OF_YEAR, -1)
                checkWeek = getWeekStart(cal.timeInMillis)
            } else if (week < checkWeek) {
                break
            }
        }
        return streak
    }

    private fun getWeekStart(timeMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Calendar
    private val _selectedMonth = MutableStateFlow(getMonthStart(System.currentTimeMillis()))
    val selectedMonth: StateFlow<Long> = _selectedMonth

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthWorkouts: StateFlow<List<Workout>> = _selectedMonth.flatMapLatest { monthStart ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = monthStart
        cal.add(Calendar.MONTH, 1)
        val monthEnd = cal.timeInMillis
        repository.getWorkoutsByMonth(monthStart, monthEnd)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun changeMonth(offset: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = _selectedMonth.value
        cal.add(Calendar.MONTH, offset)
        _selectedMonth.value = getMonthStart(cal.timeInMillis)
    }

    private fun getMonthStart(timeMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Auto-restore unfinished workout on app start
    init {
        viewModelScope.launch {
            val unfinished = repository.getUnfinishedWorkout()
            if (unfinished != null) {
                _currentWorkoutId.value = unfinished.id
                _currentTemplateId.value = unfinished.templateId
                _workoutStartTime.value = unfinished.date
                startElapsedTimer()
                unfinished.templateId?.let { loadProgressionHints(it) }
            }
        }
    }

    // Workout management
    fun startNewWorkout(name: String) {
        viewModelScope.launch {
            val workout = Workout(name = name)
            val id = repository.createWorkout(workout)
            val created = repository.getWorkoutById(id)
            _currentWorkoutId.value = id
            _currentTemplateId.value = null
            _workoutStartTime.value = created?.date ?: System.currentTimeMillis()
            _progressionHints.value = emptyMap()
            startElapsedTimer()
        }
    }

    fun startWorkoutFromTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            val workout = Workout(name = template.name, templateId = template.id)
            val id = repository.createWorkout(workout)
            val created = repository.getWorkoutById(id)
            _currentWorkoutId.value = id
            _currentTemplateId.value = template.id
            _workoutStartTime.value = created?.date ?: System.currentTimeMillis()
            startElapsedTimer()
            // Load progression hints for all exercises in this template
            loadProgressionHints(template.id)
            // Inject favorite exercises not in template (LRU rotation)
            injectFavoriteExercises(template.id)
        }
    }

    private suspend fun injectFavoriteExercises(templateId: Long) {
        val templateExs = repository.getExercisesForTemplate(templateId).first()
        val templateExIds = templateExs.map { it.exerciseId }.toSet()
        val muscleGroups = templateExs.map { it.muscleGroup }.distinct()

        val injected = mutableListOf<com.lad.muscletracker.data.entity.Exercise>()
        val maxPerGroup = 2

        for (group in muscleGroups) {
            val favorites = repository.getFavoritesByGroup(group)
            val newFavorites = favorites.filter { it.id !in templateExIds }
            if (newFavorites.isEmpty()) continue

            // Sort by last usage date (LRU first = oldest date first)
            val sorted = newFavorites.map { fav ->
                fav to repository.getLastUsedDate(fav.id)
            }.sortedBy { it.second }.map { it.first }

            injected.addAll(sorted.take(maxPerGroup))
        }

        _injectedFavorites.value = injected
    }

    private fun startElapsedTimer() {
        elapsedTimerJob?.cancel()
        val startTime = _workoutStartTime.value
        if (startTime <= 0L) return
        elapsedTimerJob = viewModelScope.launch {
            while (_currentWorkoutId.value != null) {
                val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                _workoutElapsedSeconds.value = elapsed
                delay(1000)
            }
        }
    }

    private suspend fun loadProgressionHints(templateId: Long) {
        val hints = mutableMapOf<Long, String>()
        val warmups = mutableMapOf<Long, String>()
        val templateExercises = repository.getExercisesForTemplate(templateId).first()
        for (te in templateExercises) {
            val suggestion = getProgressionSuggestion(
                te.exerciseId, te.targetRepsMin, te.targetRepsMax, te.exerciseType
            )
            if (suggestion != null) {
                hints[te.exerciseId] = suggestion.second
                // Generate warmup for compound exercises
                if (te.exerciseType == "compound" && suggestion.first > 20f) {
                    warmups[te.exerciseId] = generateWarmup(suggestion.first)
                } else if (suggestion.first > 0f) {
                    val light = roundWeight(suggestion.first * 0.5f)
                    warmups[te.exerciseId] = "${light}kg x12"
                }
            } else {
                // No previous data - guide user based on exercise name
                val name = te.exerciseName.lowercase()
                val isBarbell = name.contains("barre") || name.contains("squat barre") ||
                    name.contains("developpe couche barre") || name.contains("militaire") ||
                    name.contains("souleve de terre")
                val isDumbbell = name.contains("haltere") || name.contains("goblet") || name.contains("arnold")
                val isMachine = name.contains("machine") || name.contains("presse") ||
                    name.contains("poulie") || name.contains("cable") || name.contains("smith") ||
                    name.contains("hack") || name.contains("leg ext") || name.contains("leg curl") ||
                    name.contains("pec deck") || name.contains("butterfly")
                val isBodyweight = name.contains("traction") || name.contains("dips") || name.contains("pompe")

                hints[te.exerciseId] = when {
                    isBarbell -> "1ere fois: commence barre a vide (20kg) et monte progressivement"
                    isDumbbell -> "1ere fois: commence avec des halteres legers (5-8kg)"
                    isMachine -> "1ere fois: commence avec la charge minimale de la machine"
                    isBodyweight -> "1ere fois: commence au poids de corps, vise ${te.targetRepsMin} reps"
                    te.exerciseType == "compound" -> "1ere fois: commence leger et monte progressivement"
                    else -> "1ere fois: commence leger, vise ${te.targetRepsMin}-${te.targetRepsMax} reps"
                }
            }
        }
        _progressionHints.value = hints
        _warmupHints.value = warmups
    }

    private fun generateWarmup(workingWeight: Float): String {
        val sets = mutableListOf<String>()
        if (workingWeight > 40f) {
            sets.add("${roundWeight(20f).toInt()}kg x12")
            sets.add("${roundWeight(workingWeight * 0.5f).toInt()}kg x8")
            sets.add("${roundWeight(workingWeight * 0.75f).toInt()}kg x4")
        } else if (workingWeight > 20f) {
            sets.add("${roundWeight(workingWeight * 0.5f).toInt()}kg x10")
            sets.add("${roundWeight(workingWeight * 0.75f).toInt()}kg x6")
        } else {
            sets.add("${roundWeight(workingWeight * 0.5f).toInt()}kg x12")
        }
        return sets.joinToString(" → ")
    }

    private fun roundWeight(weight: Float): Float {
        return (Math.round(weight / 2.5f) * 2.5f)
    }

    fun resumeWorkout(workoutId: Long) {
        viewModelScope.launch {
            val workout = repository.getWorkoutById(workoutId) ?: return@launch
            _currentWorkoutId.value = workoutId
            _isEditingWorkout.value = true
            _currentTemplateId.value = workout.templateId
            _workoutStartTime.value = workout.date
            startElapsedTimer()
            workout.templateId?.let { loadProgressionHints(it) }
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            val id = _currentWorkoutId.value ?: return@launch
            val workout = repository.getWorkoutById(id) ?: return@launch
            val duration = ((System.currentTimeMillis() - workout.date) / 1000).toInt()
            repository.updateWorkout(workout.copy(durationSeconds = duration, isCompleted = true))
            _currentWorkoutId.value = null
            _currentTemplateId.value = null
            _isEditingWorkout.value = false
            _workoutStartTime.value = 0
            _progressionHints.value = emptyMap()
            _warmupHints.value = emptyMap()
            _injectedFavorites.value = emptyList()
            elapsedTimerJob?.cancel()
            _workoutElapsedSeconds.value = 0
            stopRestTimer()
        }
    }

    fun addSet(exerciseId: Long, weight: Float, reps: Int, setType: String = "working") {
        viewModelScope.launch {
            val workoutId = _currentWorkoutId.value ?: return@launch
            val currentSets = currentWorkoutSets.value
            val exerciseSets = currentSets.filter { it.exerciseId == exerciseId }
            val setNumber = exerciseSets.size + 1
            repository.addSet(
                WorkoutSet(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    setNumber = setNumber,
                    weight = weight,
                    reps = reps,
                    setType = setType
                )
            )
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch { repository.deleteSet(setId) }
    }

    fun updateSet(setId: Long, weight: Float, reps: Int) {
        viewModelScope.launch { repository.updateSet(setId, weight, reps) }
    }

    fun toggleFavorite(exerciseId: Long) {
        viewModelScope.launch { repository.toggleFavorite(exerciseId) }
    }

    fun selectExerciseForProgress(exerciseId: Long) {
        _selectedExerciseId.value = exerciseId
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch { repository.deleteWorkout(workout) }
    }

    // Double progression for strength/hypertrophy:
    // - Stay in target rep range (e.g. 6-10 compounds, 10-15 isolation)
    // - Add reps until ceiling → then increase weight, reset to floor
    // - Compounds: +2.5kg, Isolation: +1.25kg
    suspend fun getProgressionSuggestion(
        exerciseId: Long,
        targetRepsMin: Int,
        targetRepsMax: Int,
        exerciseType: String = "compound"
    ): Pair<Float, String>? {
        val lastPerf = repository.getLastPerformance(exerciseId, 4)
        if (lastPerf.isEmpty()) return null

        val lastWeight = lastPerf.first().weight
        val lastReps = lastPerf.first().reps
        val allReachedMax = lastPerf.all { it.reps >= targetRepsMax }

        // Weight increment: compound +2.5kg, isolation +1.25kg (small weights under 20kg also +1.25kg)
        val increment = when {
            exerciseType == "isolation" && lastWeight < 30f -> 1.25f
            exerciseType == "isolation" -> 2.5f
            lastWeight < 20f -> 1.25f
            else -> 2.5f
        }

        return when {
            // All sets hit ceiling → INCREASE WEIGHT, reset to floor reps
            allReachedMax -> {
                val newWeight = roundWeight(lastWeight + increment)
                Pair(newWeight, "Augmente! ${newWeight}kg x $targetRepsMin reps ($targetRepsMin-$targetRepsMax)")
            }
            // Last reps at or above max but not all sets → stabilize all sets at ceiling
            lastReps >= targetRepsMax -> {
                Pair(lastWeight, "${lastWeight}kg → stabilise toutes tes series a $targetRepsMax reps puis monte")
            }
            // In range → add 1 rep (strength progression)
            lastReps >= targetRepsMin -> {
                Pair(lastWeight, "${lastWeight}kg x ${lastReps + 1} reps (objectif: $targetRepsMax pour monter)")
            }
            // Below minimum → weight too heavy or need to build up
            else -> {
                Pair(lastWeight, "${lastWeight}kg x $targetRepsMin reps min (force ton range $targetRepsMin-$targetRepsMax)")
            }
        }
    }

    // Supplement management
    private val appContext = application.applicationContext

    fun addSupplement(name: String, dosage: String) {
        viewModelScope.launch {
            repository.addSupplement(Supplement(name = name, dosage = dosage))
        }
    }

    fun updateSupplement(supplement: Supplement, name: String, dosage: String) {
        viewModelScope.launch {
            repository.updateSupplement(supplement.copy(name = name, dosage = dosage))
        }
    }

    fun deleteSupplement(supplement: Supplement) {
        viewModelScope.launch {
            // Cancel all alarms for this supplement's reminders
            val swr = allSupplementsWithReminders.value.find { it.supplement.id == supplement.id }
            swr?.reminders?.forEach { reminder ->
                SupplementAlarmScheduler.cancelReminder(appContext, reminder.id)
            }
            repository.deleteSupplement(supplement)
        }
    }

    fun addReminder(supplementId: Long, hour: Int, minute: Int, label: String) {
        viewModelScope.launch {
            val reminder = SupplementReminder(
                supplementId = supplementId,
                hour = hour, minute = minute,
                timeLabel = label
            )
            val id = repository.addReminder(reminder)
            // Schedule the alarm
            val supplement = repository.getSupplementById(supplementId)
            if (supplement != null) {
                SupplementAlarmScheduler.scheduleReminder(
                    appContext,
                    reminder.copy(id = id),
                    supplement.name,
                    supplement.dosage
                )
            }
        }
    }

    fun deleteReminder(reminder: SupplementReminder) {
        viewModelScope.launch {
            SupplementAlarmScheduler.cancelReminder(appContext, reminder.id)
            repository.deleteReminder(reminder)
        }
    }

    fun toggleReminder(reminder: SupplementReminder) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.updateReminder(updated)
            if (updated.isEnabled) {
                // Re-schedule
                val supplement = repository.getSupplementById(reminder.supplementId)
                if (supplement != null) {
                    SupplementAlarmScheduler.scheduleReminder(
                        appContext, updated, supplement.name, supplement.dosage
                    )
                }
            } else {
                // Cancel
                SupplementAlarmScheduler.cancelReminder(appContext, reminder.id)
            }
        }
    }

    // Goal management
    fun addGoal(title: String, exerciseId: Long?, targetWeight: Float?, targetReps: Int?) {
        viewModelScope.launch {
            repository.addGoal(
                Goal(title = title, exerciseId = exerciseId, targetWeight = targetWeight, targetReps = targetReps)
            )
        }
    }

    fun markGoalAchieved(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isAchieved = true, achievedAt = System.currentTimeMillis()))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    // Profile & Calories
    data class CalorieResult(
        val bmr: Int,
        val tdee: Int,
        val targetCalories: Int,
        val goalLabel: String,
        val proteinG: Int,
        val fatG: Int,
        val carbsG: Int
    )

    val userProfile = repository.getProfile().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val weightHistory = repository.getAllWeightEntries().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val calorieResult: StateFlow<CalorieResult?> = userProfile.map { profile ->
        profile?.let { calculateCalories(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun calculateCalories(profile: UserProfile): CalorieResult {
        val bmr = if (profile.gender == "male") {
            (10 * profile.weightKg) + (6.25 * profile.heightCm) - (5 * profile.age) + 5
        } else {
            (10 * profile.weightKg) + (6.25 * profile.heightCm) - (5 * profile.age) - 161
        }

        val multiplier = when (profile.activityLevel) {
            "sedentary" -> 1.2
            "light" -> 1.375
            "moderate" -> 1.55
            "active" -> 1.725
            "very_active" -> 1.9
            else -> 1.55
        }

        val tdee = (bmr * multiplier).toInt()
        val (target, label) = when (profile.goal) {
            "deficit" -> (tdee - 400) to "Deficit"
            "bulk" -> (tdee + 350) to "Prise de masse"
            else -> tdee to "Maintenance"
        }

        val proteinG = (profile.weightKg * 2).toInt()
        val fatG = (profile.weightKg * 0.8).toInt()
        val proteinCal = proteinG * 4
        val fatCal = fatG * 9
        val carbsG = ((target - proteinCal - fatCal) / 4).coerceAtLeast(0)

        return CalorieResult(bmr.toInt(), tdee, target, label, proteinG, fatG, carbsG)
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch { repository.saveProfile(profile) }
    }

    fun addWeightEntry(weightKg: Float, bodyFat: Float?) {
        viewModelScope.launch {
            repository.addWeightEntry(WeightEntry(weightKg = weightKg, bodyFatPercent = bodyFat))
        }
    }

    fun deleteWeightEntry(entry: WeightEntry) {
        viewModelScope.launch { repository.deleteWeightEntry(entry) }
    }

    // Cardio
    val allCardioSessions = repository.getAllCardioSessions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private fun getWeekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val weekStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val weekEnd = cal.timeInMillis
        return weekStart to weekEnd
    }

    val weeklyCardioCalories: StateFlow<Int> = run {
        val (weekStart, weekEnd) = getWeekRange()
        repository.getWeeklyCaloriesBurned(weekStart, weekEnd).map { it ?: 0 }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        )
    }

    val weeklyCardioDistance: StateFlow<Float> = run {
        val (weekStart, weekEnd) = getWeekRange()
        repository.getWeeklyDistance(weekStart, weekEnd).map { it ?: 0f }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0f
        )
    }

    fun calculateCardioCalories(
        type: String, distanceKm: Float, durationMinutes: Int,
        inclinePercent: Float, userWeightKg: Float
    ): Int {
        val avgSpeedKmh = if (durationMinutes > 0) distanceKm / (durationMinutes / 60f) else 0f
        val met = when (type) {
            "walk" -> 3.5f + (0.18f * inclinePercent)
            "run" -> when {
                avgSpeedKmh <= 8f -> 6.0f
                avgSpeedKmh <= 9.5f -> 8.3f
                avgSpeedKmh <= 11f -> 9.8f
                avgSpeedKmh <= 12.5f -> 11.0f
                avgSpeedKmh <= 14f -> 12.8f
                avgSpeedKmh <= 16f -> 14.5f
                else -> 16.0f
            }
            else -> 5.0f
        }
        return ((met * userWeightKg * durationMinutes * 3.5f) / 200f).toInt()
    }

    fun addCardioSession(type: String, speedKmh: Float, durationMinutes: Int, inclinePercent: Float) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            val weightKg = profile?.weightKg ?: 70f
            val distanceKm = speedKmh * (durationMinutes / 60f)
            val calories = calculateCardioCalories(type, distanceKm, durationMinutes, inclinePercent, weightKg)
            repository.addCardioSession(
                CardioSession(
                    type = type, distanceKm = distanceKm, durationMinutes = durationMinutes,
                    inclinePercent = inclinePercent, caloriesBurned = calories, avgSpeedKmh = speedKmh
                )
            )
        }
    }

    fun updateCardioSession(session: CardioSession, speedKmh: Float, durationMinutes: Int, inclinePercent: Float) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce()
            val weightKg = profile?.weightKg ?: 70f
            val distanceKm = speedKmh * (durationMinutes / 60f)
            val calories = calculateCardioCalories(session.type, distanceKm, durationMinutes, inclinePercent, weightKg)
            repository.updateCardioSession(
                session.copy(
                    distanceKm = distanceKm, durationMinutes = durationMinutes,
                    inclinePercent = inclinePercent, caloriesBurned = calories, avgSpeedKmh = speedKmh
                )
            )
        }
    }

    fun deleteCardioSession(session: CardioSession) {
        viewModelScope.launch { repository.deleteCardioSession(session) }
    }

    // JSON Export
    fun exportAllData(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val workouts = repository.getAllWorkoutsOnce()
                val sets = repository.getAllSetsOnce()
                val exercises = allExercises.value
                val profile = repository.getProfileOnce()
                val weights = weightHistory.value
                val cardio = allCardioSessions.value
                val supplements = allSupplementsWithReminders.value

                val setsGrouped = sets.groupBy { it.workoutId }

                val data = mapOf(
                    "exportDate" to System.currentTimeMillis(),
                    "version" to 1,
                    "workouts" to workouts.map { w ->
                        mapOf(
                            "id" to w.id, "name" to w.name, "date" to w.date,
                            "durationSeconds" to w.durationSeconds,
                            "sets" to (setsGrouped[w.id] ?: emptyList()).map { s ->
                                mapOf(
                                    "exerciseName" to s.exerciseName, "muscleGroup" to s.muscleGroup,
                                    "setNumber" to s.setNumber, "weight" to s.weight,
                                    "reps" to s.reps, "setType" to s.setType
                                )
                            }
                        )
                    },
                    "exercises" to exercises.map { e ->
                        mapOf("id" to e.id, "name" to e.name, "muscleGroup" to e.muscleGroup)
                    },
                    "profile" to profile?.let {
                        mapOf(
                            "weightKg" to it.weightKg, "heightCm" to it.heightCm,
                            "age" to it.age, "gender" to it.gender,
                            "activityLevel" to it.activityLevel, "goal" to it.goal
                        )
                    },
                    "weightEntries" to weights.map { w ->
                        mapOf("date" to w.date, "weightKg" to w.weightKg, "bodyFatPercent" to w.bodyFatPercent)
                    },
                    "cardioSessions" to cardio.map { c ->
                        mapOf(
                            "date" to c.date, "type" to c.type, "distanceKm" to c.distanceKm,
                            "durationMinutes" to c.durationMinutes, "inclinePercent" to c.inclinePercent,
                            "caloriesBurned" to c.caloriesBurned
                        )
                    },
                    "supplements" to supplements.map { swr ->
                        mapOf(
                            "name" to swr.supplement.name, "dosage" to swr.supplement.dosage,
                            "reminders" to swr.reminders.map { r ->
                                mapOf("hour" to r.hour, "minute" to r.minute, "label" to r.timeLabel)
                            }
                        )
                    }
                )

                val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                onResult(gson.toJson(data))
            } catch (e: Exception) {
                onResult("{\"error\": \"${e.message}\"}")
            }
        }
    }

    // Rest timer
    fun startRestTimer(seconds: Int = 90) {
        restTimerJob?.cancel()
        _restTimerSeconds.value = seconds
        _isRestTimerRunning.value = true
        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0 && _isRestTimerRunning.value) {
                delay(1000)
                if (_isRestTimerRunning.value) {
                    _restTimerSeconds.value -= 1
                    if (_restTimerSeconds.value == 0) {
                        try {
                            vibrator?.vibrate(
                                VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300), -1)
                            )
                        } catch (_: Exception) { }
                    }
                }
            }
            _isRestTimerRunning.value = false
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _isRestTimerRunning.value = false
        _restTimerSeconds.value = 0
    }
}

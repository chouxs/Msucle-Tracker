package com.lad.muscletracker.data.repository

import com.lad.muscletracker.data.dao.*
import com.lad.muscletracker.data.entity.*

class WorkoutRepository(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val setDao: SetDao,
    private val templateDao: TemplateDao,
    private val templateExerciseDao: TemplateExerciseDao,
    private val supplementDao: SupplementDao,
    private val goalDao: GoalDao,
    private val userProfileDao: UserProfileDao,
    private val cardioDao: CardioDao
) {
    // Exercises
    fun getAllExercises() = exerciseDao.getAllExercises()
    fun getExercisesByGroup(group: String) = exerciseDao.getExercisesByGroup(group)
    fun getExercisesByDayType(dayType: String) = exerciseDao.getExercisesByDayType(dayType)
    fun getAllMuscleGroups() = exerciseDao.getAllMuscleGroups()
    suspend fun addExercise(exercise: Exercise) = exerciseDao.insert(exercise)
    suspend fun toggleFavorite(exerciseId: Long) = exerciseDao.toggleFavorite(exerciseId)
    fun getFavoriteExercises() = exerciseDao.getFavoriteExercises()
    suspend fun getFavoritesByGroup(group: String) = exerciseDao.getFavoritesByGroup(group)

    // Workouts
    fun getAllWorkouts() = workoutDao.getAllWorkouts()
    fun getLastWorkout() = workoutDao.getLastWorkout()
    suspend fun getWorkoutById(id: Long) = workoutDao.getWorkoutById(id)
    suspend fun createWorkout(workout: Workout) = workoutDao.insert(workout)
    suspend fun updateWorkout(workout: Workout) = workoutDao.update(workout)
    suspend fun deleteWorkout(workout: Workout) = workoutDao.delete(workout)
    fun getWeeklyWorkoutCount(weekStart: Long, weekEnd: Long) = workoutDao.getWeeklyWorkoutCount(weekStart, weekEnd)
    fun getAllWorkoutDates() = workoutDao.getAllWorkoutDates()
    suspend fun getAllWorkoutsOnce() = workoutDao.getAllWorkoutsOnce()
    suspend fun getAllSetsOnce() = setDao.getAllSetsOnce()
    fun getWorkoutsByMonth(monthStart: Long, monthEnd: Long) = workoutDao.getWorkoutsByMonth(monthStart, monthEnd)
    suspend fun getUnfinishedWorkout() = workoutDao.getUnfinishedWorkout()

    // Sets
    fun getSetsForWorkout(workoutId: Long) = setDao.getSetsForWorkout(workoutId)
    fun getProgressForExercise(exerciseId: Long) = setDao.getProgressForExercise(exerciseId)
    fun getWeeklyVolumeByMuscle(weekStart: Long, weekEnd: Long) = setDao.getWeeklyVolumeByMuscle(weekStart, weekEnd)
    suspend fun getLastPerformance(exerciseId: Long, setCount: Int = 4) = setDao.getLastPerformance(exerciseId, setCount)
    suspend fun addSet(set: WorkoutSet) = setDao.insert(set)
    suspend fun deleteSet(setId: Long) = setDao.deleteById(setId)
    suspend fun updateSet(setId: Long, weight: Float, reps: Int, setType: String = "working") = setDao.updateSet(setId, weight, reps, setType)
    fun getAllExercisesWithLastPerformance() = setDao.getAllExercisesWithLastPerformance()
    fun getAllPersonalRecords() = setDao.getAllPersonalRecords()
    fun getTotalVolume() = setDao.getTotalVolume()
    suspend fun getLastUsedDate(exerciseId: Long) = setDao.getLastUsedDate(exerciseId)

    // Templates
    fun getAllTemplates() = templateDao.getAllTemplates()
    fun getTemplatesForDay(dayOfWeek: Int) = templateDao.getTemplatesForDay(dayOfWeek)
    suspend fun getTemplateById(id: Long) = templateDao.getById(id)
    fun getExercisesForTemplate(templateId: Long) = templateExerciseDao.getExercisesForTemplate(templateId)

    // Supplements
    fun getAllSupplementsWithReminders() = supplementDao.getAllWithReminders()
    suspend fun addSupplement(supplement: Supplement) = supplementDao.insertSupplement(supplement)
    suspend fun addReminder(reminder: SupplementReminder) = supplementDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: SupplementReminder) = supplementDao.updateReminder(reminder)
    suspend fun updateSupplement(supplement: Supplement) = supplementDao.updateSupplement(supplement)
    suspend fun deleteSupplement(supplement: Supplement) = supplementDao.deleteSupplement(supplement)
    suspend fun deleteReminder(reminder: SupplementReminder) = supplementDao.deleteReminder(reminder)
    suspend fun getAllEnabledReminders() = supplementDao.getAllEnabledReminders()
    suspend fun getSupplementById(id: Long) = supplementDao.getSupplementById(id)

    // Goals
    fun getAllGoals() = goalDao.getAllGoals()
    fun getActiveGoals() = goalDao.getActiveGoals()
    suspend fun addGoal(goal: Goal) = goalDao.insert(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.update(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    // User Profile
    fun getProfile() = userProfileDao.getProfile()
    suspend fun getProfileOnce() = userProfileDao.getProfileOnce()
    suspend fun saveProfile(profile: UserProfile) = userProfileDao.insertOrUpdate(profile)
    fun getAllWeightEntries() = userProfileDao.getAllWeightEntries()
    fun getLatestWeight() = userProfileDao.getLatestWeight()
    suspend fun addWeightEntry(entry: WeightEntry) = userProfileDao.insertWeightEntry(entry)
    suspend fun deleteWeightEntry(entry: WeightEntry) = userProfileDao.deleteWeightEntry(entry)

    // Cardio
    fun getAllCardioSessions() = cardioDao.getAllSessions()
    fun getWeeklyCaloriesBurned(start: Long, end: Long) = cardioDao.getWeeklyCaloriesBurned(start, end)
    fun getWeeklyDistance(start: Long, end: Long) = cardioDao.getWeeklyDistance(start, end)
    suspend fun addCardioSession(session: CardioSession) = cardioDao.insert(session)
    suspend fun updateCardioSession(session: CardioSession) = cardioDao.update(session)
    suspend fun deleteCardioSession(session: CardioSession) = cardioDao.delete(session)
}

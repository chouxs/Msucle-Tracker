package com.lad.muscletracker.data

import com.lad.muscletracker.data.entity.Exercise
import com.lad.muscletracker.data.entity.WorkoutTemplate
import com.lad.muscletracker.data.entity.TemplateExercise

object SeedData {

    fun getDefaultExercises(): List<Exercise> = listOf(
        // === PUSH (Lundi) ===
        Exercise(name = "Developpe couche barre", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 150, exerciseType = "compound", dayType = "push"),
        Exercise(name = "Developpe militaire", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound", dayType = "push"),
        Exercise(name = "Developpe incline halteres", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "push,upper"),
        Exercise(name = "Ecarte poulie", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 12, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "push"),
        Exercise(name = "Elevations laterales", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 12, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "push,upper"),
        Exercise(name = "Extension triceps poulie", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 12, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "push"),

        // === PULL (Mardi) ===
        Exercise(name = "Rowing barre", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 150, exerciseType = "compound", dayType = "pull"),
        Exercise(name = "Traction pronation", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound", dayType = "pull,fullbody"),
        Exercise(name = "Tirage horizontal cable", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "pull"),
        Exercise(name = "Face pull", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 15, targetRepsMax = 20, restSeconds = 60, exerciseType = "isolation", dayType = "pull"),
        Exercise(name = "Curl biceps barre", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 75, exerciseType = "isolation", dayType = "pull"),
        Exercise(name = "Curl marteau", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 75, exerciseType = "isolation", dayType = "pull"),

        // === LEGS (Mercredi) ===
        Exercise(name = "Squat barre", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 150, exerciseType = "compound", dayType = "legs"),
        Exercise(name = "Souleve de terre roumain", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 8, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound", dayType = "legs,lower"),
        Exercise(name = "Fentes / Squat bulgare", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "legs"),
        Exercise(name = "Leg curl allonge", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "legs"),
        Exercise(name = "Mollets debout", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation", dayType = "legs"),

        // === UPPER (Jeudi) ===
        Exercise(name = "Traction supination", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound", dayType = "upper"),
        Exercise(name = "Developpe epaules halteres", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "upper,fullbody"),
        Exercise(name = "Rowing haltere", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "upper"),
        Exercise(name = "Curl pupitre", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 60, exerciseType = "isolation", dayType = "upper"),
        Exercise(name = "Barre au front", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 60, exerciseType = "isolation", dayType = "upper"),

        // === LOWER (Vendredi) ===
        Exercise(name = "Presse a cuisses", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 150, exerciseType = "compound", dayType = "lower"),
        Exercise(name = "Leg extension", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 12, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "lower"),
        Exercise(name = "Leg curl assis", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation", dayType = "lower"),
        Exercise(name = "Mollets assis", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 12, targetRepsMax = 20, restSeconds = 60, exerciseType = "isolation", dayType = "lower"),
        Exercise(name = "Crunch cable", muscleGroup = "Abdos", targetSets = 4, targetRepsMin = 12, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation", dayType = "lower"),

        // === FULL BODY (Weekend) ===
        Exercise(name = "Developpe couche halteres", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 8, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "fullbody"),
        Exercise(name = "Squat goblet", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 120, exerciseType = "compound", dayType = "fullbody"),
        Exercise(name = "Curl biceps superset", muscleGroup = "Bras", targetSets = 2, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 60, exerciseType = "isolation", dayType = "fullbody"),
        Exercise(name = "Extension triceps superset", muscleGroup = "Bras", targetSets = 2, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 60, exerciseType = "isolation", dayType = "fullbody")
    )

    fun getDefaultTemplates(): List<WorkoutTemplate> = listOf(
        WorkoutTemplate(name = "Push", dayOfWeek = 2, description = "Pecs, Epaules, Triceps"),   // Monday (Calendar.MONDAY=2)
        WorkoutTemplate(name = "Pull", dayOfWeek = 3, description = "Dos, Biceps, Arriere epaule"), // Tuesday
        WorkoutTemplate(name = "Legs", dayOfWeek = 4, description = "Quadriceps, Ischio, Mollets"), // Wednesday
        WorkoutTemplate(name = "Upper", dayOfWeek = 5, description = "Haut du corps - Compose"),   // Thursday
        WorkoutTemplate(name = "Lower", dayOfWeek = 6, description = "Bas du corps + Abdos"),       // Friday
        WorkoutTemplate(name = "Full Body", dayOfWeek = 0, description = "Seance complete optionnelle") // Any day
    )

    // Map exercise names to their template day types
    private val templateExerciseMap = mapOf(
        "Push" to listOf(
            "Developpe couche barre", "Developpe militaire", "Developpe incline halteres",
            "Ecarte poulie", "Elevations laterales", "Extension triceps poulie"
        ),
        "Pull" to listOf(
            "Rowing barre", "Traction pronation", "Tirage horizontal cable",
            "Face pull", "Curl biceps barre", "Curl marteau"
        ),
        "Legs" to listOf(
            "Squat barre", "Souleve de terre roumain", "Fentes / Squat bulgare",
            "Leg curl allonge", "Mollets debout"
        ),
        "Upper" to listOf(
            "Developpe incline halteres", "Traction supination", "Developpe epaules halteres",
            "Rowing haltere", "Elevations laterales", "Curl pupitre", "Barre au front"
        ),
        "Lower" to listOf(
            "Presse a cuisses", "Souleve de terre roumain", "Leg extension",
            "Leg curl assis", "Mollets assis", "Crunch cable"
        ),
        "Full Body" to listOf(
            "Developpe couche halteres", "Traction pronation", "Squat goblet",
            "Developpe epaules halteres", "Curl biceps superset", "Extension triceps superset"
        )
    )

    suspend fun populateDatabase(database: AppDatabase) {
        val exerciseDao = database.exerciseDao()
        val templateDao = database.templateDao()
        val templateExerciseDao = database.templateExerciseDao()

        // Insert exercises
        val exercises = getDefaultExercises()
        exerciseDao.insertAll(exercises)

        // Re-fetch to get generated IDs
        val allExercises = mutableMapOf<String, Long>()
        // We need a simple query - use a workaround since we can't easily get all at once in suspend
        exercises.forEach { ex ->
            val inserted = exerciseDao.getByName(ex.name)
            if (inserted != null) allExercises[ex.name] = inserted.id
        }

        // Insert templates
        val templates = getDefaultTemplates()
        val templateIds = mutableMapOf<String, Long>()
        templates.forEach { template ->
            val id = templateDao.insert(template)
            templateIds[template.name] = id
        }

        // Link exercises to templates
        templateExerciseMap.forEach { (templateName, exerciseNames) ->
            val templateId = templateIds[templateName] ?: return@forEach
            exerciseNames.forEachIndexed { index, exerciseName ->
                val exerciseId = allExercises[exerciseName] ?: return@forEachIndexed
                val exercise = exercises.find { it.name == exerciseName } ?: return@forEachIndexed
                templateExerciseDao.insert(
                    TemplateExercise(
                        templateId = templateId,
                        exerciseId = exerciseId,
                        sortOrder = index,
                        targetSets = exercise.targetSets,
                        targetRepsMin = exercise.targetRepsMin,
                        targetRepsMax = exercise.targetRepsMax
                    )
                )
            }
        }
    }
}

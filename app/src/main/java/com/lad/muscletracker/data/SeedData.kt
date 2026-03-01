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
        Exercise(name = "Extension triceps superset", muscleGroup = "Bras", targetSets = 2, targetRepsMin = 10, targetRepsMax = 12, restSeconds = 60, exerciseType = "isolation", dayType = "fullbody"),

        // === EXTRA PECS ===
        Exercise(name = "Developpe decline barre", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Developpe decline halteres", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Ecarte halteres", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Ecarte incline halteres", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Pec deck / Butterfly", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Dips pecs", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Pompes", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Pullover haltere", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Developpe couche Smith", muscleGroup = "Pecs", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Cable crossover", muscleGroup = "Pecs", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA DOS ===
        Exercise(name = "Tirage vertical poitrine", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Rowing T-barre", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Rowing Pendlay", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Tirage bras tendus", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Rowing machine", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Souleve de terre classique", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Hyperextension", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Good morning", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Shrugs barre", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Shrugs halteres", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Tirage vertical prise serree", muscleGroup = "Dos", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Pullover cable", muscleGroup = "Dos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA EPAULES ===
        Exercise(name = "Elevations frontales", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Oiseau / Reverse fly", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Elevation laterale cable", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Arnold press", muscleGroup = "Epaules", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Tirage menton", muscleGroup = "Epaules", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Developpe militaire Smith", muscleGroup = "Epaules", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Oiseau poulie basse", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "L-fly couche", muscleGroup = "Epaules", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA BRAS - Biceps ===
        Exercise(name = "Curl incline halteres", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Curl concentre", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Curl barre EZ", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Curl cable", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Curl inverse", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Curl spider", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Curl 21s", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA BRAS - Triceps ===
        Exercise(name = "Dips triceps", muscleGroup = "Bras", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Extension triceps corde", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Kickback triceps", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Extension triceps overhead", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Pompes diamant", muscleGroup = "Bras", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Developpe couche prise serree", muscleGroup = "Bras", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Extension triceps haltere", muscleGroup = "Bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === AVANT-BRAS ===
        Exercise(name = "Curl poignet", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Reverse curl poignet", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Farmer walk", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 90, exerciseType = "compound"),
        Exercise(name = "Wrist roller", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Flexion poignet barre", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Extension poignet barre", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),
        Exercise(name = "Pince / Gripper", muscleGroup = "Avant-bras", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 60, exerciseType = "isolation"),

        // === EXTRA JAMBES - Quadriceps ===
        Exercise(name = "Hack squat", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Squat front", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Sissy squat", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Fentes marchees", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Step-up", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Squat Smith", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Fentes halteres", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),

        // === EXTRA JAMBES - Ischio/Fessiers ===
        Exercise(name = "Souleve de terre sumo", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Hip thrust", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Glute ham raise", muscleGroup = "Jambes", targetSets = 4, targetRepsMin = 6, targetRepsMax = 10, restSeconds = 120, exerciseType = "compound"),
        Exercise(name = "Nordic curl", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Leg curl debout", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Kickback fessier cable", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Abduction machine", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Adduction machine", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA JAMBES - Mollets ===
        Exercise(name = "Mollets presse", muscleGroup = "Jambes", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),

        // === EXTRA ABDOS ===
        Exercise(name = "Crunch classique", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Releve de jambes", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Planche", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Ab wheel", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Russian twist", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Gainage lateral", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Releve de jambes suspendu", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Sit-up", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Mountain climbers", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Crunch oblique", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Rotation tronc poulie", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation"),
        Exercise(name = "Dragon flag", muscleGroup = "Abdos", targetSets = 3, targetRepsMin = 10, targetRepsMax = 15, restSeconds = 75, exerciseType = "isolation")
    )

    fun getDefaultTemplates(): List<WorkoutTemplate> = listOf(
        WorkoutTemplate(name = "Chest + Back", dayOfWeek = 2, description = "Pecs et Dos - Arnold Split"),
        WorkoutTemplate(name = "Arms + Shoulders", dayOfWeek = 3, description = "Bras et Epaules"),
        WorkoutTemplate(name = "Legs", dayOfWeek = 4, description = "Jambes completes"),
        WorkoutTemplate(name = "Chest + Back", dayOfWeek = 5, description = "Pecs et Dos - Arnold Split"),
        WorkoutTemplate(name = "Shoulders + Arms", dayOfWeek = 6, description = "Epaules et Bras"),
        WorkoutTemplate(name = "Full Body", dayOfWeek = 7, description = "Seance complete optionnelle"),
        WorkoutTemplate(name = "Full Body", dayOfWeek = 1, description = "Seance complete optionnelle")
    )

    // Map exercise names to their template day types
    private val templateExerciseMap = mapOf(
        "Chest + Back" to listOf(
            "Developpe couche barre", "Developpe incline halteres", "Ecarte poulie",
            "Pec deck / Butterfly", "Traction pronation", "Rowing barre",
            "Tirage horizontal cable", "Souleve de terre classique"
        ),
        "Arms + Shoulders" to listOf(
            "Developpe militaire", "Elevations laterales", "Face pull",
            "Curl biceps barre", "Curl marteau", "Extension triceps poulie", "Barre au front",
            "Curl poignet"
        ),
        "Legs" to listOf(
            "Squat barre", "Presse a cuisses", "Souleve de terre roumain",
            "Leg extension", "Leg curl allonge", "Mollets debout", "Crunch cable"
        ),
        "Shoulders + Arms" to listOf(
            "Arnold press", "Elevation laterale cable", "Oiseau / Reverse fly",
            "Curl incline halteres", "Curl pupitre", "Extension triceps corde", "Dips triceps",
            "Reverse curl poignet"
        ),
        "Full Body" to listOf(
            "Developpe couche halteres", "Traction pronation", "Squat goblet",
            "Developpe epaules halteres", "Curl biceps barre", "Extension triceps poulie", "Crunch cable"
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
        exercises.forEach { ex ->
            val inserted = exerciseDao.getByName(ex.name)
            if (inserted != null) allExercises[ex.name] = inserted.id
        }

        // Insert templates - track by name+dow since names can repeat
        val templates = getDefaultTemplates()
        val templateIdsByKey = mutableMapOf<String, Long>()
        templates.forEach { template ->
            val id = templateDao.insert(template)
            templateIdsByKey["${template.name}_${template.dayOfWeek}"] = id
        }

        // Link exercises to templates
        templateExerciseMap.forEach { (templateName, exerciseNames) ->
            // Find all templates with this name
            val matchingKeys = templateIdsByKey.keys.filter { it.startsWith("${templateName}_") }
            matchingKeys.forEach { key ->
                val templateId = templateIdsByKey[key] ?: return@forEach
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
}

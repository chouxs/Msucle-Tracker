package com.lad.muscletracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lad.muscletracker.data.dao.*
import com.lad.muscletracker.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class, Workout::class, WorkoutSet::class,
        WorkoutTemplate::class, TemplateExercise::class,
        Supplement::class, SupplementReminder::class,
        Goal::class,
        UserProfile::class, WeightEntry::class, CardioSession::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun setDao(): SetDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun supplementDao(): SupplementDao
    abstract fun goalDao(): GoalDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun cardioDao(): CardioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to exercises
                db.execSQL("ALTER TABLE exercises ADD COLUMN targetSets INTEGER NOT NULL DEFAULT 3")
                db.execSQL("ALTER TABLE exercises ADD COLUMN targetRepsMin INTEGER NOT NULL DEFAULT 6")
                db.execSQL("ALTER TABLE exercises ADD COLUMN targetRepsMax INTEGER NOT NULL DEFAULT 12")
                db.execSQL("ALTER TABLE exercises ADD COLUMN restSeconds INTEGER NOT NULL DEFAULT 90")
                db.execSQL("ALTER TABLE exercises ADD COLUMN exerciseType TEXT NOT NULL DEFAULT 'compound'")
                db.execSQL("ALTER TABLE exercises ADD COLUMN dayType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE exercises ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")

                // Add templateId to workouts
                db.execSQL("ALTER TABLE workouts ADD COLUMN templateId INTEGER")

                // Create new tables
                db.execSQL("""CREATE TABLE IF NOT EXISTS workout_templates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    dayOfWeek INTEGER NOT NULL,
                    description TEXT NOT NULL DEFAULT '',
                    isDefault INTEGER NOT NULL DEFAULT 1
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS template_exercises (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    templateId INTEGER NOT NULL,
                    exerciseId INTEGER NOT NULL,
                    sortOrder INTEGER NOT NULL,
                    targetSets INTEGER NOT NULL DEFAULT 3,
                    targetRepsMin INTEGER NOT NULL DEFAULT 6,
                    targetRepsMax INTEGER NOT NULL DEFAULT 12,
                    FOREIGN KEY (templateId) REFERENCES workout_templates(id) ON DELETE CASCADE,
                    FOREIGN KEY (exerciseId) REFERENCES exercises(id) ON DELETE CASCADE
                )""")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_template_exercises_templateId ON template_exercises(templateId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_template_exercises_exerciseId ON template_exercises(exerciseId)")

                db.execSQL("""CREATE TABLE IF NOT EXISTS supplements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    dosage TEXT NOT NULL DEFAULT '',
                    notes TEXT NOT NULL DEFAULT ''
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS supplement_reminders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    supplementId INTEGER NOT NULL,
                    timeLabel TEXT NOT NULL DEFAULT '',
                    hour INTEGER NOT NULL,
                    minute INTEGER NOT NULL,
                    isEnabled INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY (supplementId) REFERENCES supplements(id) ON DELETE CASCADE
                )""")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_supplement_reminders_supplementId ON supplement_reminders(supplementId)")

                db.execSQL("""CREATE TABLE IF NOT EXISTS goals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    exerciseId INTEGER,
                    title TEXT NOT NULL,
                    targetWeight REAL,
                    targetReps INTEGER,
                    isAchieved INTEGER NOT NULL DEFAULT 0,
                    createdAt INTEGER NOT NULL,
                    achievedAt INTEGER
                )""")

                // Deactivate old exercises
                db.execSQL("UPDATE exercises SET isActive = 0")

                // Insert new PPL exercises
                val exercises = listOf(
                    "('Developpe couche barre','Pecs',0,4,6,10,150,'compound','push',1)",
                    "('Developpe militaire','Epaules',0,3,6,10,120,'compound','push',1)",
                    "('Developpe incline halteres','Pecs',0,3,8,12,120,'compound','push,upper',1)",
                    "('Ecarte poulie','Pecs',0,3,12,15,75,'isolation','push',1)",
                    "('Elevations laterales','Epaules',0,3,12,15,75,'isolation','push,upper',1)",
                    "('Extension triceps poulie','Bras',0,3,12,15,75,'isolation','push',1)",
                    "('Rowing barre','Dos',0,4,6,10,150,'compound','pull',1)",
                    "('Traction pronation','Dos',0,3,6,10,120,'compound','pull,fullbody',1)",
                    "('Tirage horizontal cable','Dos',0,3,8,12,120,'compound','pull',1)",
                    "('Face pull','Epaules',0,3,15,20,60,'isolation','pull',1)",
                    "('Curl biceps barre','Bras',0,3,10,12,75,'isolation','pull',1)",
                    "('Curl marteau','Bras',0,3,10,12,75,'isolation','pull',1)",
                    "('Squat barre','Jambes',0,4,6,10,150,'compound','legs',1)",
                    "('Souleve de terre roumain','Jambes',0,3,8,10,120,'compound','legs,lower',1)",
                    "('Fentes / Squat bulgare','Jambes',0,3,8,12,120,'compound','legs',1)",
                    "('Leg curl allonge','Jambes',0,3,10,15,75,'isolation','legs',1)",
                    "('Mollets debout','Jambes',0,4,10,15,60,'isolation','legs',1)",
                    "('Traction supination','Dos',0,3,6,10,120,'compound','upper',1)",
                    "('Developpe epaules halteres','Epaules',0,3,8,12,120,'compound','upper,fullbody',1)",
                    "('Rowing haltere','Dos',0,3,8,12,120,'compound','upper',1)",
                    "('Curl pupitre','Bras',0,3,10,12,60,'isolation','upper',1)",
                    "('Barre au front','Bras',0,3,10,12,60,'isolation','upper',1)",
                    "('Presse a cuisses','Jambes',0,4,8,12,150,'compound','lower',1)",
                    "('Leg extension','Jambes',0,3,12,15,75,'isolation','lower',1)",
                    "('Leg curl assis','Jambes',0,3,10,15,75,'isolation','lower',1)",
                    "('Mollets assis','Jambes',0,4,12,20,60,'isolation','lower',1)",
                    "('Crunch cable','Abdos',0,4,12,15,60,'isolation','lower',1)",
                    "('Developpe couche halteres','Pecs',0,3,8,12,120,'compound','fullbody',1)",
                    "('Squat goblet','Jambes',0,3,10,12,120,'compound','fullbody',1)",
                    "('Curl biceps superset','Bras',0,2,10,12,60,'isolation','fullbody',1)",
                    "('Extension triceps superset','Bras',0,2,10,12,60,'isolation','fullbody',1)"
                )
                exercises.forEach { values ->
                    db.execSQL("INSERT INTO exercises (name,muscleGroup,isCustom,targetSets,targetRepsMin,targetRepsMax,restSeconds,exerciseType,dayType,isActive) VALUES $values")
                }

                // Insert templates
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Push',2,'Pecs, Epaules, Triceps',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Pull',3,'Dos, Biceps, Arriere epaule',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Legs',4,'Quadriceps, Ischio, Mollets',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Upper',5,'Haut du corps - Compose',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Lower',6,'Bas du corps + Abdos',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Full Body',0,'Seance complete optionnelle',1)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS user_profile (
                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                    weightKg REAL NOT NULL DEFAULT 70.0,
                    heightCm REAL NOT NULL DEFAULT 175.0,
                    age INTEGER NOT NULL DEFAULT 25,
                    gender TEXT NOT NULL DEFAULT 'male',
                    activityLevel TEXT NOT NULL DEFAULT 'moderate',
                    goal TEXT NOT NULL DEFAULT 'maintenance'
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS weight_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date INTEGER NOT NULL,
                    weightKg REAL NOT NULL,
                    bodyFatPercent REAL
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS cardio_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    date INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    distanceKm REAL NOT NULL,
                    durationMinutes INTEGER NOT NULL,
                    inclinePercent REAL NOT NULL DEFAULT 0.0,
                    caloriesBurned INTEGER NOT NULL DEFAULT 0,
                    avgSpeedKmh REAL NOT NULL DEFAULT 0.0,
                    notes TEXT NOT NULL DEFAULT ''
                )""")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workout_sets ADD COLUMN setType TEXT NOT NULL DEFAULT 'working'")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val newExercises = listOf(
                    // Pecs
                    Triple("Developpe decline barre", "Pecs", "compound"),
                    Triple("Developpe decline halteres", "Pecs", "compound"),
                    Triple("Ecarte halteres", "Pecs", "isolation"),
                    Triple("Ecarte incline halteres", "Pecs", "isolation"),
                    Triple("Pec deck / Butterfly", "Pecs", "isolation"),
                    Triple("Dips pecs", "Pecs", "compound"),
                    Triple("Pompes", "Pecs", "compound"),
                    Triple("Pullover haltere", "Pecs", "isolation"),
                    Triple("Developpe couche Smith", "Pecs", "compound"),
                    Triple("Cable crossover", "Pecs", "isolation"),

                    // Dos
                    Triple("Tirage vertical poitrine", "Dos", "compound"),
                    Triple("Rowing T-barre", "Dos", "compound"),
                    Triple("Rowing Pendlay", "Dos", "compound"),
                    Triple("Tirage bras tendus", "Dos", "isolation"),
                    Triple("Rowing machine", "Dos", "compound"),
                    Triple("Souleve de terre classique", "Dos", "compound"),
                    Triple("Hyperextension", "Dos", "isolation"),
                    Triple("Good morning", "Dos", "compound"),
                    Triple("Shrugs barre", "Dos", "isolation"),
                    Triple("Shrugs halteres", "Dos", "isolation"),
                    Triple("Tirage vertical prise serree", "Dos", "compound"),
                    Triple("Pullover cable", "Dos", "isolation"),

                    // Epaules
                    Triple("Elevations frontales", "Epaules", "isolation"),
                    Triple("Oiseau / Reverse fly", "Epaules", "isolation"),
                    Triple("Elevation laterale cable", "Epaules", "isolation"),
                    Triple("Arnold press", "Epaules", "compound"),
                    Triple("Tirage menton", "Epaules", "compound"),
                    Triple("Developpe militaire Smith", "Epaules", "compound"),
                    Triple("Oiseau poulie basse", "Epaules", "isolation"),
                    Triple("L-fly couche", "Epaules", "isolation"),

                    // Bras - Biceps
                    Triple("Curl incline halteres", "Bras", "isolation"),
                    Triple("Curl concentre", "Bras", "isolation"),
                    Triple("Curl barre EZ", "Bras", "isolation"),
                    Triple("Curl cable", "Bras", "isolation"),
                    Triple("Curl inverse", "Bras", "isolation"),
                    Triple("Curl spider", "Bras", "isolation"),
                    Triple("Curl 21s", "Bras", "isolation"),

                    // Bras - Triceps
                    Triple("Dips triceps", "Bras", "compound"),
                    Triple("Extension triceps corde", "Bras", "isolation"),
                    Triple("Kickback triceps", "Bras", "isolation"),
                    Triple("Extension triceps overhead", "Bras", "isolation"),
                    Triple("Pompes diamant", "Bras", "compound"),
                    Triple("Developpe couche prise serree", "Bras", "compound"),
                    Triple("Extension triceps haltere", "Bras", "isolation"),

                    // Bras - Avant-bras
                    Triple("Curl poignet", "Bras", "isolation"),
                    Triple("Reverse curl poignet", "Bras", "isolation"),
                    Triple("Farmer walk", "Bras", "compound"),

                    // Jambes - Quadriceps
                    Triple("Hack squat", "Jambes", "compound"),
                    Triple("Squat front", "Jambes", "compound"),
                    Triple("Sissy squat", "Jambes", "isolation"),
                    Triple("Fentes marchees", "Jambes", "compound"),
                    Triple("Step-up", "Jambes", "compound"),
                    Triple("Squat Smith", "Jambes", "compound"),
                    Triple("Fentes halteres", "Jambes", "compound"),

                    // Jambes - Ischio
                    Triple("Souleve de terre sumo", "Jambes", "compound"),
                    Triple("Hip thrust", "Jambes", "compound"),
                    Triple("Glute ham raise", "Jambes", "compound"),
                    Triple("Nordic curl", "Jambes", "isolation"),
                    Triple("Leg curl debout", "Jambes", "isolation"),
                    Triple("Kickback fessier cable", "Jambes", "isolation"),
                    Triple("Abduction machine", "Jambes", "isolation"),
                    Triple("Adduction machine", "Jambes", "isolation"),

                    // Jambes - Mollets
                    Triple("Mollets presse", "Jambes", "isolation"),

                    // Abdos
                    Triple("Crunch classique", "Abdos", "isolation"),
                    Triple("Releve de jambes", "Abdos", "isolation"),
                    Triple("Planche", "Abdos", "isolation"),
                    Triple("Ab wheel", "Abdos", "isolation"),
                    Triple("Russian twist", "Abdos", "isolation"),
                    Triple("Gainage lateral", "Abdos", "isolation"),
                    Triple("Releve de jambes suspendu", "Abdos", "isolation"),
                    Triple("Sit-up", "Abdos", "isolation"),
                    Triple("Mountain climbers", "Abdos", "isolation"),
                    Triple("Crunch oblique", "Abdos", "isolation"),
                    Triple("Rotation tronc poulie", "Abdos", "isolation"),
                    Triple("Dragon flag", "Abdos", "isolation")
                )

                newExercises.forEach { (name, group, type) ->
                    val rest = if (type == "compound") 120 else 75
                    val sets = if (type == "compound") 4 else 3
                    val rMin = if (type == "compound") 6 else 10
                    val rMax = if (type == "compound") 10 else 15
                    db.execSQL(
                        """INSERT INTO exercises (name, muscleGroup, isCustom, targetSets, targetRepsMin, targetRepsMax, restSeconds, exerciseType, dayType, isActive)
                           SELECT ?, ?, 0, $sets, $rMin, $rMax, $rest, ?, '', 1
                           WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = ?)""",
                        arrayOf(name, group, type, name)
                    )
                }
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exercises ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Fix: mark all workouts with sets as completed
                db.execSQL("UPDATE workouts SET isCompleted = 1 WHERE id IN (SELECT DISTINCT workoutId FROM workout_sets) AND isCompleted = 0")

                // Clear old program
                db.execSQL("DELETE FROM template_exercises")
                db.execSQL("DELETE FROM workout_templates")

                // Arnold Split templates
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Chest + Back',2,'Pecs et Dos - Arnold Split',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Arms + Shoulders',3,'Bras et Epaules',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Legs',4,'Jambes completes',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Chest + Back',5,'Pecs et Dos - Arnold Split',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Shoulders + Arms',6,'Epaules et Bras',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Full Body',7,'Seance complete optionnelle',1)")
                db.execSQL("INSERT INTO workout_templates (name,dayOfWeek,description,isDefault) VALUES ('Full Body',1,'Seance complete optionnelle',1)")

                // Helper to link template exercise
                fun link(tName: String, tDow: Int, eName: String, order: Int, sets: Int, rMin: Int, rMax: Int) {
                    db.execSQL("""
                        INSERT INTO template_exercises (templateId, exerciseId, sortOrder, targetSets, targetRepsMin, targetRepsMax)
                        SELECT t.id, e.id, $order, $sets, $rMin, $rMax
                        FROM workout_templates t, exercises e
                        WHERE t.name = ? AND t.dayOfWeek = $tDow AND e.name = ?
                    """, arrayOf(tName, eName))
                }

                // Chest + Back (Monday=2, Thursday=5)
                for (dow in listOf(2, 5)) {
                    link("Chest + Back", dow, "Developpe couche barre", 0, 4, 6, 10)
                    link("Chest + Back", dow, "Developpe incline halteres", 1, 3, 8, 12)
                    link("Chest + Back", dow, "Ecarte poulie", 2, 3, 12, 15)
                    link("Chest + Back", dow, "Cable crossover", 3, 3, 12, 15)
                    link("Chest + Back", dow, "Traction pronation", 4, 4, 6, 10)
                    link("Chest + Back", dow, "Rowing barre", 5, 4, 6, 10)
                    link("Chest + Back", dow, "Tirage horizontal cable", 6, 3, 8, 12)
                    link("Chest + Back", dow, "Souleve de terre classique", 7, 3, 6, 8)
                }

                // Arms + Shoulders (Tuesday=3)
                link("Arms + Shoulders", 3, "Developpe militaire", 0, 4, 6, 10)
                link("Arms + Shoulders", 3, "Elevations laterales", 1, 4, 12, 15)
                link("Arms + Shoulders", 3, "Face pull", 2, 3, 15, 20)
                link("Arms + Shoulders", 3, "Curl biceps barre", 3, 3, 10, 12)
                link("Arms + Shoulders", 3, "Curl marteau", 4, 3, 10, 12)
                link("Arms + Shoulders", 3, "Extension triceps poulie", 5, 3, 12, 15)
                link("Arms + Shoulders", 3, "Barre au front", 6, 3, 10, 12)

                // Legs (Wednesday=4)
                link("Legs", 4, "Squat barre", 0, 4, 6, 10)
                link("Legs", 4, "Presse a cuisses", 1, 4, 8, 12)
                link("Legs", 4, "Souleve de terre roumain", 2, 3, 8, 10)
                link("Legs", 4, "Leg extension", 3, 3, 12, 15)
                link("Legs", 4, "Leg curl allonge", 4, 3, 10, 15)
                link("Legs", 4, "Mollets debout", 5, 4, 10, 15)
                link("Legs", 4, "Crunch cable", 6, 3, 12, 15)

                // Shoulders + Arms (Friday=6)
                link("Shoulders + Arms", 6, "Arnold press", 0, 4, 8, 12)
                link("Shoulders + Arms", 6, "Elevation laterale cable", 1, 3, 12, 15)
                link("Shoulders + Arms", 6, "Oiseau / Reverse fly", 2, 3, 12, 15)
                link("Shoulders + Arms", 6, "Curl incline halteres", 3, 3, 10, 12)
                link("Shoulders + Arms", 6, "Curl pupitre", 4, 3, 10, 12)
                link("Shoulders + Arms", 6, "Extension triceps corde", 5, 3, 12, 15)
                link("Shoulders + Arms", 6, "Dips triceps", 6, 3, 6, 10)

                // Full Body (Saturday=7, Sunday=1)
                for (dow in listOf(7, 1)) {
                    link("Full Body", dow, "Developpe couche halteres", 0, 3, 8, 12)
                    link("Full Body", dow, "Traction pronation", 1, 3, 6, 10)
                    link("Full Body", dow, "Squat goblet", 2, 3, 10, 12)
                    link("Full Body", dow, "Developpe epaules halteres", 3, 3, 8, 12)
                    link("Full Body", dow, "Curl biceps barre", 4, 2, 10, 12)
                    link("Full Body", dow, "Extension triceps poulie", 5, 2, 10, 12)
                    link("Full Body", dow, "Crunch cable", 6, 3, 12, 15)
                }

                // Add missing exercises
                val newExercises = listOf(
                    Triple("Developpe couche machine", "Pecs", "compound"),
                    Triple("Rowing un bras cable", "Dos", "compound"),
                    Triple("Rowing assis machine", "Dos", "compound"),
                    Triple("Curl biceps machine", "Bras", "isolation"),
                    Triple("Extension triceps machine", "Bras", "isolation"),
                    Triple("Elevations laterales machine", "Epaules", "isolation"),
                    Triple("Presse inclinee", "Jambes", "compound"),
                    Triple("Belt squat", "Jambes", "compound"),
                    Triple("Crunch machine", "Abdos", "isolation"),
                    Triple("V-up", "Abdos", "isolation")
                )
                newExercises.forEach { (name, group, type) ->
                    val rest = if (type == "compound") 120 else 75
                    val sets = if (type == "compound") 4 else 3
                    val rMin = if (type == "compound") 6 else 10
                    val rMax = if (type == "compound") 10 else 15
                    db.execSQL(
                        """INSERT INTO exercises (name, muscleGroup, isCustom, targetSets, targetRepsMin, targetRepsMax, restSeconds, exerciseType, dayType, isActive, isFavorite)
                           SELECT ?, ?, 0, $sets, $rMin, $rMax, $rest, ?, '', 1, 0
                           WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = ?)""",
                        arrayOf(name, group, type, name)
                    )
                }
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Move forearm exercises to "Avant-bras" muscle group
                db.execSQL("UPDATE exercises SET muscleGroup = 'Avant-bras' WHERE name IN ('Curl poignet', 'Reverse curl poignet', 'Farmer walk', 'Curl inverse')")

                // 2. Add new forearm exercises
                val forearmExercises = listOf(
                    "Wrist roller" to "isolation",
                    "Flexion poignet barre" to "isolation",
                    "Extension poignet barre" to "isolation",
                    "Pince / Gripper" to "isolation"
                )
                forearmExercises.forEach { (name, type) ->
                    db.execSQL(
                        """INSERT INTO exercises (name, muscleGroup, isCustom, targetSets, targetRepsMin, targetRepsMax, restSeconds, exerciseType, dayType, isActive, isFavorite)
                           SELECT ?, 'Avant-bras', 0, 3, 10, 15, 60, ?, '', 1, 0
                           WHERE NOT EXISTS (SELECT 1 FROM exercises WHERE name = ?)""",
                        arrayOf(name, type, name)
                    )
                }

                // 3. Replace "Cable crossover" with "Pec deck / Butterfly" in Chest+Back templates
                // (Cable crossover = Ecarte poulie are too similar)
                db.execSQL("""
                    UPDATE template_exercises SET exerciseId = (SELECT id FROM exercises WHERE name = 'Pec deck / Butterfly' LIMIT 1)
                    WHERE exerciseId = (SELECT id FROM exercises WHERE name = 'Cable crossover' LIMIT 1)
                    AND templateId IN (SELECT id FROM workout_templates WHERE name = 'Chest + Back')
                """)

                // 4. Add forearm exercise to Arms + Shoulders (Tuesday)
                db.execSQL("""
                    INSERT INTO template_exercises (templateId, exerciseId, sortOrder, targetSets, targetRepsMin, targetRepsMax)
                    SELECT t.id, e.id, 7, 3, 10, 15
                    FROM workout_templates t, exercises e
                    WHERE t.name = 'Arms + Shoulders' AND t.dayOfWeek = 3 AND e.name = 'Curl poignet'
                """)

                // 5. Add forearm exercise to Shoulders + Arms (Friday)
                db.execSQL("""
                    INSERT INTO template_exercises (templateId, exerciseId, sortOrder, targetSets, targetRepsMin, targetRepsMax)
                    SELECT t.id, e.id, 7, 3, 10, 15
                    FROM workout_templates t, exercises e
                    WHERE t.name = 'Shoulders + Arms' AND t.dayOfWeek = 6 AND e.name = 'Reverse curl poignet'
                """)
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    INSERT OR IGNORE INTO exercises (name, muscleGroup, targetSets, targetRepsMin, targetRepsMax, restSeconds, exerciseType, dayType)
                    VALUES ('Iso Lateral High Row', 'Dos', 4, 6, 10, 120, 'compound', '')
                """)
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Hammer Strength plate-loaded machines
                val exercises = listOf(
                    "('Iso Lateral Low Row', 'Dos', 4, 6, 10, 120, 'compound', '')",
                    "('Iso Lateral Wide Pulldown', 'Dos', 4, 6, 10, 120, 'compound', '')",
                    "('Iso Lateral DY Row', 'Dos', 4, 6, 10, 120, 'compound', '')",
                    "('Iso Lateral Bench Press', 'Pecs', 4, 6, 10, 120, 'compound', '')",
                    "('Iso Lateral Incline Press', 'Pecs', 4, 6, 10, 120, 'compound', '')",
                    "('Iso Lateral Shoulder Press', 'Epaules', 4, 6, 10, 120, 'compound', '')",
                    // Matrix / Technogym selectorized machines
                    "('Chest press machine', 'Pecs', 4, 6, 10, 120, 'compound', '')",
                    "('Shoulder press machine', 'Epaules', 4, 6, 10, 120, 'compound', '')",
                    "('Lateral raise machine', 'Epaules', 3, 10, 15, 75, 'isolation', '')",
                    // Jambes
                    "('V-Squat', 'Jambes', 4, 6, 10, 120, 'compound', '')",
                    "('Belt squat', 'Jambes', 4, 6, 10, 120, 'compound', '')"
                )
                exercises.forEach { values ->
                    db.execSQL("INSERT OR IGNORE INTO exercises (name, muscleGroup, targetSets, targetRepsMin, targetRepsMax, restSeconds, exerciseType, dayType) VALUES $values")
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muscle_tracker_db"
                )
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    SeedData.populateDatabase(database)
                }
            }
        }
    }
}

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
    version = 4,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muscle_tracker_db"
                )
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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

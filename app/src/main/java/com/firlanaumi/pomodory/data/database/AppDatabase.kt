package com.firlanaumi.pomodory.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase // Import ini
import com.firlanaumi.pomodory.data.dao.TaskDao
import com.firlanaumi.pomodory.model.Converters
import com.firlanaumi.pomodory.model.Task
import com.firlanaumi.pomodory.model.TaskCategory // Import TaskCategory
import kotlinx.coroutines.CoroutineScope // Import ini
import kotlinx.coroutines.Dispatchers // Import ini
import kotlinx.coroutines.launch // Import ini
import java.util.Calendar // Import ini
import java.util.Date // Import ini

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pomodory_database"
                )
                    .fallbackToDestructiveMigration() // Hapus ini di produksi jika Anda ingin migrasi yang lebih baik
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val taskDao = database.taskDao()
                    prePopulateDatabase(taskDao)
                }
            }
        }

        private suspend fun prePopulateDatabase(taskDao: TaskDao) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 3)

            val tasksToInsert = listOf(
                Task(
                    title = "Tugas Makalah Pancasila",
                    description = "Baca ulang catatan materi Pancasila",
                    duration = "1 sesi, 25 menit",
                    isChecked = false,
                    category = TaskCategory.AKADEMIK,
                    sessions = 1,
                    deadline = calendar.time // Menggunakan tanggal deadline yang dihitung
                ),
                Task(
                    title = "Latsol Struktur Data",
                    description = "Kerjakan latihan soal bab Linked List",
                    duration = "2 sesi, 50 menit",
                    isChecked = false,
                    category = TaskCategory.MENDESAK,
                    sessions = 2,
                    deadline = calendar.time // Menggunakan tanggal deadline yang dihitung
                ),
                Task(
                    title = "Desain Feed IG UKM",
                    description = "Buat 3 desain feed Instagram untuk acara UKM",
                    duration = "2 sesi, 50 menit",
                    isChecked = false, // Misal awalnya belum selesai
                    category = TaskCategory.PROYEK,
                    sessions = 2,
                    deadline = calendar.time // Menggunakan tanggal deadline yang dihitung
                )
            )
            taskDao.insertAllTasks(tasksToInsert)
            println("Database pre-populated with initial tasks.") // Untuk debugging
        }
    }
}
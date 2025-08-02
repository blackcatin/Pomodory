// app/src/main/java/com/firlanaumi/pomodory/model/Task.kt
package com.firlanaumi.pomodory.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.firlanaumi.pomodory.ui.theme.* // Impor semua warna dari tema Anda
import java.util.Date // Penting: untuk deadline

enum class TaskCategory {
    AKADEMIK,
    MENDESAK,
    PROYEK,
    PERSONAL,
    SELF_CARE;

    fun getColor(): Color {
        return when (this) {
            AKADEMIK -> DarkBlue
            MENDESAK -> DarkPeace
            PROYEK -> Yellow
            PERSONAL -> DarkGreen
            SELF_CARE -> Pink
        }
    }
}

// Tambahkan @Entity dan @PrimaryKey
@Entity(tableName = "tasks")
@TypeConverters(Converters::class) // Tambahkan ini untuk mengonversi kategori dan tanggal
data class Task(
    @PrimaryKey(autoGenerate = true) // ID akan otomatis dibuat
    val id: Int = 0, // Default 0 untuk ID yang akan dibuat otomatis
    val title: String,
    val description: String,
    val duration: String,
    var isChecked: Boolean = false, // Default false
    val category: TaskCategory,
    val sessions: Int, // <--- TAMBAHKAN INI
    val deadline: Date? // <--- TAMBAHKAN INI (menggunakan java.util.Date)
)

// Converters untuk Room agar bisa menyimpan TaskCategory dan Date
class Converters {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTaskCategory(categoryName: String): TaskCategory {
        return TaskCategory.valueOf(categoryName)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
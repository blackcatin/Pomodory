package com.firlanaumi.pomodory.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firlanaumi.pomodory.model.Task
import kotlinx.coroutines.flow.Flow // Untuk mengamati perubahan data

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // PERBAIKAN DI SINI
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>> // Mengembalikan Flow untuk observasi real-time

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM tasks WHERE deadline >= :startDate AND deadline <= :endDate ORDER BY deadline ASC")
    fun getTasksByDeadline(startDate: Long, endDate: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :categoryName ORDER BY deadline ASC")
    fun getTasksByCategory(categoryName: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<Task>)
}
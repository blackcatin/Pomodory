package com.firlanaumi.pomodory.data.repository

import com.firlanaumi.pomodory.data.dao.TaskDao
import com.firlanaumi.pomodory.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    fun getTasksByDeadline(startDate: Long, endDate: Long): Flow<List<Task>> {
        return taskDao.getTasksByDeadline(startDate, endDate)
    }

    fun getTasksByCategory(categoryName: String): Flow<List<Task>> {
        return taskDao.getTasksByCategory(categoryName)
    }
}
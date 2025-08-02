package com.firlanaumi.pomodory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firlanaumi.pomodory.data.repository.TaskRepository


class PomodoroTimerViewModelFactory(
    private val repository: TaskRepository,
    private val taskId: Int? // taskId yang akan diteruskan saat membuat ViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memastikan bahwa ViewModel yang diminta adalah PomodoroTimerViewModel
        if (modelClass.isAssignableFrom(PomodoroTimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Membuat dan mengembalikan instance PomodoroTimerViewModel
            return PomodoroTimerViewModel(repository, taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
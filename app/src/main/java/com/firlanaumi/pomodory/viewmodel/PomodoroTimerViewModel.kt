// app/src/main/java/com/firlanaumi/pomodory/viewmodel/PomodoroTimerViewModel.kt
package com.firlanaumi.pomodory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firlanaumi.pomodory.data.repository.TaskRepository
import com.firlanaumi.pomodory.model.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log // Import ini

class PomodoroTimerViewModel(
    private val repository: TaskRepository,
    private val taskId: Int? // Terima taskId di ViewModel
) : ViewModel() {

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    private var timerJob: Job? = null

    private val _remainingTime = MutableStateFlow(0) // Waktu tersisa (detik)
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    private val _elapsedTimeSeconds = MutableStateFlow(0) // Waktu yang sudah berjalan (detik)
    val elapsedTimeSeconds: StateFlow<Int> = _elapsedTimeSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _currentSessionCount = MutableStateFlow(0)
    val currentSessionCount: StateFlow<Int> = _currentSessionCount.asStateFlow()

    private val _isBreakTime = MutableStateFlow(false)
    val isBreakTime: StateFlow<Boolean> = _isBreakTime.asStateFlow()

    init {
        Log.d("PomodoroVM", "ViewModel created for Task ID: $taskId")
        taskId?.let { id ->
            viewModelScope.launch {
                val fetchedTask = repository.getTaskById(id)
                _currentTask.value = fetchedTask
                Log.d("PomodoroVM", "Fetched task: ${fetchedTask?.title} (ID: ${fetchedTask?.id})")

                _currentTask.value?.let { task ->
                    // Setel waktu fokus awal berdasarkan sesi tugas
                    _remainingTime.value = task.sessions * 25 * 60 // Total fokus waktu untuk semua sesi
                    _elapsedTimeSeconds.value = 0 // Reset elapsed time
                    Log.d("PomodoroVM", "Initial remaining time set to ${_remainingTime.value} seconds.")
                } ?: run {
                    Log.e("PomodoroVM", "Task with ID $id not found in DB.")
                }
            }
        } ?: run {
            Log.e("PomodoroVM", "Task ID is null in ViewModel creation.")
        }
    }

    fun toggleTimer() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _isRunning.value) {
                delay(1000)
                _remainingTime.value--
                _elapsedTimeSeconds.value++ // Tambahkan waktu yang berlalu
                if (_remainingTime.value == 0) {
                    _currentSessionCount.value++
                    val currentTaskSessions = currentTask.value?.sessions ?: 1

                    if (_isBreakTime.value) {
                        _isBreakTime.value = false
                        if (_currentSessionCount.value >= currentTaskSessions) {
                            _remainingTime.value = 0
                            _isRunning.value = false
                            Log.d("PomodoroVM", "All sessions completed.")
                            // TODO: Trigger Task Completed actions, update task status in DB
                            currentTask.value?.let { task ->
                                repository.updateTask(task.copy(isChecked = true))
                            }
                        } else {
                            _remainingTime.value = 25 * 60 // Kembali ke fokus default
                            _elapsedTimeSeconds.value = 0 // Reset elapsed time for new session
                            Log.d("PomodoroVM", "Returning to focus time.")
                        }
                    } else {
                        _isBreakTime.value = true
                        _remainingTime.value = if (_currentSessionCount.value % 4 == 0 && _currentSessionCount.value < currentTaskSessions) (30 * 60) else (5 * 60)
                        Log.d("PomodoroVM", "Entering break time: ${_remainingTime.value} seconds.")
                    }
                    _isRunning.value = false
                    // TODO: Trigger notifikasi atau suara
                }
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        Log.d("PomodoroVM", "Timer paused.")
    }

    fun resetTimer() {
        pauseTimer()
        _currentTask.value?.let { task ->
            _remainingTime.value = task.sessions * 25 * 60
            _elapsedTimeSeconds.value = 0 // Reset elapsed time
            _currentSessionCount.value = 0
            _isBreakTime.value = false
            Log.d("PomodoroVM", "Timer reset for task ${task.title}.")
        } ?: run {
            _remainingTime.value = 25 * 60
            _elapsedTimeSeconds.value = 0 // Reset elapsed time
            _currentSessionCount.value = 0
            _isBreakTime.value = false
            Log.d("PomodoroVM", "Timer reset to default (task not found).")
        }
    }

    fun markTaskCompleted() {
        viewModelScope.launch {
            _currentTask.value?.let { task ->
                repository.updateTask(task.copy(isChecked = true))
                Log.d("PomodoroVM", "Task ${task.title} marked as completed.")
            }
        }
    }
}
// app/src/main/java/com/firlanaumi/pomodory/viewmodel/HomeScreenViewModel.kt
package com.firlanaumi.pomodory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firlanaumi.pomodory.data.repository.TaskRepository
import com.firlanaumi.pomodory.model.Task
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel(private val repository: TaskRepository) : ViewModel() {

    // Menggunakan StateFlow untuk mengekspos daftar tugas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // Inisialisasi: Ambil semua tugas dari database saat ViewModel dibuat
    init {
        viewModelScope.launch {
            repository.allTasks.collect { taskList ->
                _tasks.value = taskList
                android.util.Log.d("HomeScreenViewModel", "Fetched ${taskList.size} tasks from DB")
            }
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTaskById(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }
}

// Factory untuk membuat ViewModel dengan Repository
class HomeScreenViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
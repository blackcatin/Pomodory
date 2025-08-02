package com.firlanaumi.pomodory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firlanaumi.pomodory.data.repository.TaskRepository
import com.firlanaumi.pomodory.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Calendar.getInstance())
    val selectedDate: StateFlow<Calendar> = _selectedDate.asStateFlow()

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedDate.flatMapLatest { calendar ->
                val startOfDay = getStartOfDay(calendar.time)
                val endOfDay = getEndOfDay(calendar.time)
                repository.getTasksByDeadline(startOfDay.time, endOfDay.time)
            }.collect { taskList ->
                _tasksForSelectedDate.value = taskList
            }
        }
    }

    fun selectDate(calendar: Calendar) {
        _selectedDate.value = calendar
    }


    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }
}

class CalendarViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.manalejandro.motivame.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(true)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    init {
        loadTasks()
        loadSettings()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.tasks.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            repository.notificationEnabled.collect { enabled ->
                _notificationEnabled.value = enabled
            }
        }
        viewModelScope.launch {
            repository.soundEnabled.collect { enabled ->
                _soundEnabled.value = enabled
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationEnabled(enabled)
            _notificationEnabled.value = enabled
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSoundEnabled(enabled)
            _soundEnabled.value = enabled
        }
    }
}


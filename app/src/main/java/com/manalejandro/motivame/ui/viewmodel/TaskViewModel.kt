package com.manalejandro.motivame.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.widget.MotivameWidget
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

    private val _language = MutableStateFlow("es")
    val language: StateFlow<String> = _language.asStateFlow()

    /** Callback que se invoca tras cualquier cambio en las tareas para reprogramar avisos.
     *  Recibe el valor actual de notificationEnabled para evitar condiciones de carrera con DataStore. */
    var onRescheduleReminders: ((notificationsEnabled: Boolean) -> Unit)? = null

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
        viewModelScope.launch {
            repository.language.collect { lang ->
                _language.value = lang
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            onRescheduleReminders?.invoke(_notificationEnabled.value)
            MotivameWidget.requestUpdate(getApplication())
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            onRescheduleReminders?.invoke(_notificationEnabled.value)
            MotivameWidget.requestUpdate(getApplication())
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            onRescheduleReminders?.invoke(_notificationEnabled.value)
            MotivameWidget.requestUpdate(getApplication())
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationEnabled(enabled)
            _notificationEnabled.value = enabled
            // Pasamos `enabled` directamente para no releer DataStore
            onRescheduleReminders?.invoke(enabled)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSoundEnabled(enabled)
            _soundEnabled.value = enabled
        }
    }

    suspend fun setLanguage(languageCode: String) {
        repository.setLanguage(languageCode)
        _language.value = languageCode
    }
}

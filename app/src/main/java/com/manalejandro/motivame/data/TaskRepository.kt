package com.manalejandro.motivame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "motivame_prefs")

class TaskRepository(private val context: Context) {

    companion object {
        private val TASKS_KEY = stringPreferencesKey("tasks")
        private val NOTIFICATION_ENABLED_KEY = stringPreferencesKey("notification_enabled")
        private val SOUND_ENABLED_KEY = stringPreferencesKey("sound_enabled")

        val DEFAULT_TASKS = listOf(
            Task(
                title = "Hacer ejercicio",
                goals = listOf(
                    "Mejorar mi salud cardiovascular",
                    "Sentirme más energético",
                    "Alcanzar mi peso ideal"
                )
            ),
            Task(
                title = "Estudiar inglés",
                goals = listOf(
                    "Conseguir mejores oportunidades laborales",
                    "Viajar sin limitaciones",
                    "Expandir mi conocimiento"
                )
            ),
            Task(
                title = "Leer 30 minutos",
                goals = listOf(
                    "Desarrollar el hábito de la lectura",
                    "Aprender cosas nuevas",
                    "Reducir el tiempo en redes sociales"
                )
            )
        )
    }

    val tasks: Flow<List<Task>> = context.dataStore.data
        .map { preferences ->
            val tasksJson = preferences[TASKS_KEY]
            if (tasksJson.isNullOrEmpty()) {
                DEFAULT_TASKS
            } else {
                parseTasksFromJson(tasksJson)
            }
        }

    val notificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY]?.toBoolean() ?: true
        }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SOUND_ENABLED_KEY]?.toBoolean() ?: true
        }

    suspend fun saveTasks(tasks: List<Task>) {
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = tasksToJson(tasks)
        }
    }

    suspend fun addTask(task: Task) {
        context.dataStore.edit { preferences ->
            val currentTasks = parseTasksFromJson(preferences[TASKS_KEY] ?: "")
            val updatedTasks = currentTasks + task
            preferences[TASKS_KEY] = tasksToJson(updatedTasks)
        }
    }

    suspend fun updateTask(task: Task) {
        context.dataStore.edit { preferences ->
            val currentTasks = parseTasksFromJson(preferences[TASKS_KEY] ?: "")
            val updatedTasks = currentTasks.map { if (it.id == task.id) task else it }
            preferences[TASKS_KEY] = tasksToJson(updatedTasks)
        }
    }

    suspend fun deleteTask(taskId: String) {
        context.dataStore.edit { preferences ->
            val currentTasks = parseTasksFromJson(preferences[TASKS_KEY] ?: "")
            val updatedTasks = currentTasks.filter { it.id != taskId }
            preferences[TASKS_KEY] = tasksToJson(updatedTasks)
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled.toString()
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled.toString()
        }
    }

    private fun tasksToJson(tasks: List<Task>): String {
        val jsonArray = JSONArray()
        tasks.forEach { task ->
            val jsonObject = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("goals", JSONArray(task.goals))
                put("isActive", task.isActive)
                put("createdAt", task.createdAt)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun parseTasksFromJson(json: String): List<Task> {
        if (json.isEmpty()) return emptyList()

        return try {
            val jsonArray = JSONArray(json)
            List(jsonArray.length()) { index ->
                val jsonObject = jsonArray.getJSONObject(index)
                val goalsArray = jsonObject.getJSONArray("goals")
                val goals = List(goalsArray.length()) { goalsArray.getString(it) }

                Task(
                    id = jsonObject.getString("id"),
                    title = jsonObject.getString("title"),
                    goals = goals,
                    isActive = jsonObject.getBoolean("isActive"),
                    createdAt = jsonObject.getLong("createdAt")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}


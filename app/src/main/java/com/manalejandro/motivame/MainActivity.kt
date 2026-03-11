package com.manalejandro.motivame

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.ui.screens.AddTaskScreen
import com.manalejandro.motivame.ui.screens.MainScreen
import com.manalejandro.motivame.ui.screens.SettingsScreen
import com.manalejandro.motivame.ui.theme.MotivameTheme
import com.manalejandro.motivame.ui.viewmodel.TaskViewModel
import com.manalejandro.motivame.util.LocaleHelper
import com.manalejandro.motivame.worker.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Leer idioma de forma síncrona antes de inflar la UI
        val langCode = runCatching {
            kotlinx.coroutines.runBlocking {
                TaskRepository(newBase).language.first()
            }
        }.getOrDefault("es")
        super.attachBaseContext(LocaleHelper.wrap(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Programar recordatorios para todas las tareas activas
        scheduleAllReminders()

        setContent {
            MotivameTheme {
                MotivameApp(onRescheduleReminders = { enabled -> scheduleAllReminders(enabled) })
            }
        }
    }

    /**
     * Cancela todos los workers anteriores y programa nuevos recordatorios
     * para cada tarea activa usando [ReminderScheduler].
     * @param notificationsEnabled valor ya conocido; si es null se lee de DataStore.
     */
    fun scheduleAllReminders(notificationsEnabled: Boolean? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = TaskRepository(applicationContext)
            val enabled = notificationsEnabled ?: repository.notificationEnabled.first()
            if (!enabled) {
                WorkManager.getInstance(applicationContext).cancelAllWorkByTag("task_reminder")
                return@launch
            }
            val tasks = repository.tasks.first()
            ReminderScheduler.scheduleAll(applicationContext, tasks)
        }
    }
}

@Composable
fun MotivameApp(onRescheduleReminders: (Boolean) -> Unit = {}) {
    val viewModel: TaskViewModel = viewModel()
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf("main") }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Registrar callback para reprogramar avisos cuando cambian las tareas
    LaunchedEffect(viewModel) {
        viewModel.onRescheduleReminders = { enabled -> onRescheduleReminders(enabled) }
    }

    // Interceptar el botón físico Atrás del sistema
    BackHandler(enabled = currentScreen != "main") {
        taskToEdit = null
        currentScreen = "main"
    }
    // En la pantalla principal, minimizar en lugar de cerrar
    BackHandler(enabled = currentScreen == "main") {
        (context as? ComponentActivity)?.moveTaskToBack(true)
    }

    when (currentScreen) {
        "main" -> MainScreen(
            viewModel = viewModel,
            onNavigateToAddTask = {
                taskToEdit = null
                currentScreen = "add_task"
            },
            onNavigateToSettings = { currentScreen = "settings" },
            onEditTask = { task ->
                taskToEdit = task
                currentScreen = "edit_task"
            }
        )
        "add_task" -> AddTaskScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = "main" }
        )
        "edit_task" -> AddTaskScreen(
            viewModel = viewModel,
            onNavigateBack = {
                taskToEdit = null
                currentScreen = "main"
            },
            taskToEdit = taskToEdit
        )
        "settings" -> SettingsScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = "main" }
        )
    }
}
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
import androidx.work.*
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.ui.screens.AddTaskScreen
import com.manalejandro.motivame.ui.screens.MainScreen
import com.manalejandro.motivame.ui.screens.SettingsScreen
import com.manalejandro.motivame.ui.theme.MotivameTheme
import com.manalejandro.motivame.ui.viewmodel.TaskViewModel
import com.manalejandro.motivame.util.LocaleHelper
import com.manalejandro.motivame.worker.DailyReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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
     * para cada tarea activa, distribuyendo los avisos entre las 9:00 y las 21:00.
     * @param notificationsEnabled valor ya conocido, para evitar condición de carrera con DataStore.
     *        Si es null, se lee del DataStore (solo al arrancar la app).
     */
    fun scheduleAllReminders(notificationsEnabled: Boolean? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = TaskRepository(applicationContext)

            // Cancelar todos los workers existentes de recordatorios de tareas
            WorkManager.getInstance(applicationContext)
                .cancelAllWorkByTag("task_reminder")

            val enabled = notificationsEnabled ?: repository.notificationEnabled.first()
            if (!enabled) return@launch

            val tasks = repository.tasks.first()
            tasks.filter { it.isActive }.forEach { task ->
                scheduleRemindersForTask(task)
            }
        }
    }

    private fun scheduleRemindersForTask(task: Task) {
        val reminders = task.dailyReminders.coerceIn(1, 10)
        val cycleDays = task.repeatEveryDays.coerceIn(1, 30)
        val workManager = WorkManager.getInstance(applicationContext)

        // Ventana de notificaciones: 9:00 a 21:00 (720 minutos disponibles)
        val windowStartMinute = 9 * 60   // 540
        val windowEndMinute   = 21 * 60  // 1260
        val windowSize        = windowEndMinute - windowStartMinute  // 720

        // Distribuir los N avisos en días distintos dentro del ciclo.
        // Si reminders <= cycleDays cada aviso va a un día diferente;
        // si hay más avisos que días, se reparten de forma ciclica.
        val dayAssignments = (0 until reminders).map { i -> i % cycleDays }

        // Generar horas aleatorias únicas (en minutos desde medianoche)
        // Para cada aviso elegimos un minuto al azar dentro de [540, 1260)
        // asegurándonos de que no coincida con ningún otro aviso ya asignado.
        val usedMinutes = mutableSetOf<Int>()
        val minuteAssignments = mutableListOf<Int>()

        repeat(reminders) {
            var candidate: Int
            var attempts = 0
            do {
                candidate = windowStartMinute + Random.nextInt(windowSize)
                attempts++
                // Tras muchos intentos (espacio muy saturado) relajamos la condición
                // exigiendo sólo minutos distintos en el mismo día
            } while (usedMinutes.contains(candidate) && attempts < windowSize)
            usedMinutes.add(candidate)
            minuteAssignments.add(candidate)
        }

        for (i in 0 until reminders) {
            val dayOffset    = dayAssignments[i]
            val totalMinutes = minuteAssignments[i]
            val targetHour   = totalMinutes / 60
            val targetMinute = totalMinutes % 60

            val delayMs = calculateDelayToTimeWithDayOffset(targetHour, targetMinute, dayOffset)

            val inputData = workDataOf(DailyReminderWorker.KEY_TASK_ID to task.id)

            val workRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("task_reminder")
                .addTag("task_${task.id}")
                .build()

            workManager.enqueue(workRequest)
        }
    }


    /**
     * Calcula el retardo hasta la hora indicada más un desplazamiento de días.
     * Si la hora ya pasó hoy, se mueve al día siguiente antes de aplicar el offset.
     */
    private fun calculateDelayToTimeWithDayOffset(hour: Int, minute: Int, dayOffset: Int): Long {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si ya pasó esa hora hoy, mover a mañana antes de aplicar el offset
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Aplicar el offset de días adicionales
        if (dayOffset > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        }

        return calendar.timeInMillis - now
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
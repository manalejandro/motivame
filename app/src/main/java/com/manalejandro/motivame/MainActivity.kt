package com.manalejandro.motivame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.ui.screens.AddTaskScreen
import com.manalejandro.motivame.ui.screens.MainScreen
import com.manalejandro.motivame.ui.screens.SettingsScreen
import com.manalejandro.motivame.ui.theme.MotivameTheme
import com.manalejandro.motivame.ui.viewmodel.TaskViewModel
import com.manalejandro.motivame.worker.DailyReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Programar recordatorios para todas las tareas activas
        scheduleAllReminders()

        setContent {
            MotivameTheme {
                MotivameApp(onRescheduleReminders = { scheduleAllReminders() })
            }
        }
    }

    /**
     * Cancela todos los workers anteriores y programa nuevos recordatorios
     * para cada tarea activa, distribuyendo los avisos entre las 9:00 y las 21:00.
     */
    fun scheduleAllReminders() {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = TaskRepository(applicationContext)
            val tasks = repository.tasks.first()
            val notificationEnabled = repository.notificationEnabled.first()

            // Cancelar todos los workers existentes de recordatorios de tareas
            WorkManager.getInstance(applicationContext)
                .cancelAllWorkByTag("task_reminder")

            if (!notificationEnabled) return@launch

            tasks.filter { it.isActive }.forEach { task ->
                scheduleRemindersForTask(task)
            }
        }
    }

    private fun scheduleRemindersForTask(task: Task) {
        val reminders = task.dailyReminders.coerceIn(1, 10)
        val cycleDays = task.repeatEveryDays.coerceIn(1, 30)
        val workManager = WorkManager.getInstance(applicationContext)

        // Ventana de notificaciones: 9:00 a 21:00 (12 horas = 720 minutos)
        val windowStartHour = 9
        val windowEndHour = 21
        val windowMinutes = (windowEndHour - windowStartHour) * 60  // 720 min

        // Distribuir los N avisos a lo largo del ciclo de 'cycleDays' días,
        // repartidos uniformemente para que no coincidan todos el mismo día.
        // Cada aviso cae en un día y hora distintos dentro del ciclo.
        val totalSlots = cycleDays  // un aviso por día máximo
        val step = totalSlots.toDouble() / reminders  // paso fraccionario entre avisos

        for (i in 0 until reminders) {
            // Día dentro del ciclo (0-based), distribuido uniformemente
            val slotIndex = (i * step).toInt()
            val dayOffset = slotIndex % cycleDays

            // Hora dentro de la ventana: distribuida para que los avisos del mismo día
            // no se solapen, o usando posición i para variar la hora entre días
            val offsetMinutes = if (reminders == 1) {
                windowMinutes / 2  // Al mediodía si solo hay 1 aviso
            } else {
                ((windowMinutes * i) / reminders).coerceIn(0, windowMinutes - 30)
            }

            val targetHour = windowStartHour + offsetMinutes / 60
            val targetMinute = offsetMinutes % 60

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
     * Calcula el retardo en milisegundos hasta la próxima ocurrencia de la hora indicada.
     */
    private fun calculateDelayToTime(hour: Int, minute: Int): Long =
        calculateDelayToTimeWithDayOffset(hour, minute, 0)

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
fun MotivameApp(onRescheduleReminders: () -> Unit = {}) {
    val viewModel: TaskViewModel = viewModel()
    var currentScreen by remember { mutableStateOf("main") }

    // Registrar callback para reprogramar avisos cuando cambian las tareas
    LaunchedEffect(viewModel) {
        viewModel.onRescheduleReminders = onRescheduleReminders
    }

    when (currentScreen) {
        "main" -> MainScreen(
            viewModel = viewModel,
            onNavigateToAddTask = { currentScreen = "add_task" },
            onNavigateToSettings = { currentScreen = "settings" }
        )
        "add_task" -> AddTaskScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = "main" }
        )
        "settings" -> SettingsScreen(
            viewModel = viewModel,
            onNavigateBack = { currentScreen = "main" }
        )
    }
}
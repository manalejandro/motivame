package com.manalejandro.motivame.worker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.manalejandro.motivame.data.Task
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Centraliza toda la lógica para programar/cancelar recordatorios con WorkManager.
 * Usa siempre la zona horaria local del dispositivo de forma explícita.
 */
object ReminderScheduler {

    // Ventana de notificaciones: 9:00 a 21:00 (hora local)
    private const val WINDOW_START_MINUTE = 9 * 60   // 540
    private const val WINDOW_END_MINUTE   = 21 * 60  // 1260
    private const val WINDOW_SIZE         = WINDOW_END_MINUTE - WINDOW_START_MINUTE // 720

    /** Encola un único recordatorio con los parámetros exactos dados. */
    fun enqueueOne(
        context: Context,
        taskId: String,
        hour: Int,
        minute: Int,
        cycleDays: Int,
        dayOffset: Int,
        delayMs: Long
    ) {
        val inputData = workDataOf(
            DailyReminderWorker.KEY_TASK_ID         to taskId,
            DailyReminderWorker.KEY_SCHEDULE_HOUR   to hour,
            DailyReminderWorker.KEY_SCHEDULE_MINUTE to minute,
            DailyReminderWorker.KEY_CYCLE_DAYS      to cycleDays
        )
        val workRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("task_reminder")
            .addTag("task_$taskId")
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    /** Cancela todos los workers existentes y programa nuevos para cada tarea activa. */
    fun scheduleAll(context: Context, tasks: List<Task>) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("task_reminder")
        tasks.filter { it.isActive }.forEach { task ->
            scheduleForTask(context, task)
        }
    }

    /** Programa todos los recordatorios para una tarea concreta. */
    fun scheduleForTask(context: Context, task: Task) {
        val reminders  = task.dailyReminders.coerceIn(1, 10)
        val cycleDays  = task.repeatEveryDays.coerceIn(1, 30)
        val workManager = WorkManager.getInstance(context)

        // Distribuir N avisos en días del ciclo (uno por día si reminders ≤ cycleDays)
        val dayAssignments = (0 until reminders).map { i -> i % cycleDays }

        // Generar horas aleatorias únicas dentro de la ventana [9:00, 21:00)
        val usedMinutes = mutableSetOf<Int>()
        val minuteAssignments = mutableListOf<Int>()
        repeat(reminders) {
            var candidate: Int
            var attempts = 0
            do {
                candidate = WINDOW_START_MINUTE + Random.nextInt(WINDOW_SIZE)
                attempts++
            } while (usedMinutes.contains(candidate) && attempts < WINDOW_SIZE)
            usedMinutes.add(candidate)
            minuteAssignments.add(candidate)
        }

        for (i in 0 until reminders) {
            val dayOffset    = dayAssignments[i]
            val totalMinutes = minuteAssignments[i]
            val targetHour   = totalMinutes / 60
            val targetMinute = totalMinutes % 60

            val delayMs = calculateDelay(targetHour, targetMinute, dayOffset)

            val inputData = workDataOf(
                DailyReminderWorker.KEY_TASK_ID          to task.id,
                DailyReminderWorker.KEY_SCHEDULE_HOUR    to targetHour,
                DailyReminderWorker.KEY_SCHEDULE_MINUTE  to targetMinute,
                DailyReminderWorker.KEY_CYCLE_DAYS       to cycleDays
            )

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
     * Calcula el retardo (ms) hasta la hora indicada en HORA LOCAL,
     * usando TimeZone.getDefault() de forma explícita para evitar
     * que el contexto del hilo de background use UTC u otra zona.
     *
     * Si la hora ya pasó hoy, se avanza al día siguiente y luego
     * se aplica el [dayOffset] adicional del ciclo.
     */
    fun calculateDelay(hour: Int, minute: Int, dayOffset: Int): Long {
        val now = System.currentTimeMillis()

        // ✅ Zona horaria local explícita — clave para que funcione
        //    correctamente desde hilos de background y tras reinicio.
        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si esa hora ya pasó hoy, mover al día siguiente
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Desplazamiento adicional del ciclo
        if (dayOffset > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, dayOffset)
        }

        return calendar.timeInMillis - now
    }
}



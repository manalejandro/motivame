package com.manalejandro.motivame.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.notifications.NotificationHelper
import com.manalejandro.motivame.widget.MotivameWidget
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_TASK_ID         = "task_id"
        const val KEY_SCHEDULE_HOUR   = "schedule_hour"
        const val KEY_SCHEDULE_MINUTE = "schedule_minute"
        const val KEY_CYCLE_DAYS      = "cycle_days"
    }

    override suspend fun doWork(): Result {
        val repository = TaskRepository(applicationContext)
        val notificationHelper = NotificationHelper(applicationContext)

        val notificationEnabled = repository.notificationEnabled.first()
        if (!notificationEnabled) return Result.success()

        val soundEnabled = repository.soundEnabled.first()
        val taskId = inputData.getString(KEY_TASK_ID)

        val tasks = repository.tasks.first()

        val taskToNotify = if (taskId != null) {
            tasks.firstOrNull { it.id == taskId && it.isActive }
        } else {
            tasks.firstOrNull { it.isActive }
        }

        taskToNotify?.let {
            notificationHelper.sendTaskReminder(it, soundEnabled)

            // ✅ Auto-reprogramar este mismo aviso para el siguiente ciclo
            val hour      = inputData.getInt(KEY_SCHEDULE_HOUR, -1)
            val minute    = inputData.getInt(KEY_SCHEDULE_MINUTE, -1)
            val cycleDays = inputData.getInt(KEY_CYCLE_DAYS, it.repeatEveryDays)

            if (hour >= 0 && minute >= 0) {
                // El siguiente disparo es exactamente [cycleDays] días después,
                // a la misma hora local — se calcula con dayOffset = 0 porque
                // la hora ya quedó en el futuro (hoy+cycleDays).
                val delayMs = ReminderScheduler.calculateDelay(hour, minute, cycleDays)
                ReminderScheduler.enqueueOne(
                    context        = applicationContext,
                    taskId         = it.id,
                    hour           = hour,
                    minute         = minute,
                    cycleDays      = cycleDays,
                    dayOffset      = 0,
                    delayMs        = delayMs
                )
            }
        }

        // Refrescar el widget con la meta actualizada
        MotivameWidget.requestUpdate(applicationContext)

        return Result.success()
    }
}

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
        const val KEY_TASK_ID = "task_id"
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
        }

        // Refrescar el widget con la meta actualizada
        MotivameWidget.requestUpdate(applicationContext)

        return Result.success()
    }
}


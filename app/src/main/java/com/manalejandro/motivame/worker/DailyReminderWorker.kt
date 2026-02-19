package com.manalejandro.motivame.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.notifications.NotificationHelper
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = TaskRepository(applicationContext)
        val notificationHelper = NotificationHelper(applicationContext)

        val tasks = repository.tasks.first()
        val notificationEnabled = repository.notificationEnabled.first()
        val soundEnabled = repository.soundEnabled.first()

        if (notificationEnabled && tasks.isNotEmpty()) {
            notificationHelper.sendMotivationalReminder(tasks, soundEnabled)
        }

        return Result.success()
    }
}


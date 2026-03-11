package com.manalejandro.motivame.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.manalejandro.motivame.data.TaskRepository
import com.manalejandro.motivame.worker.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Reprograma todos los recordatorios activos cuando el dispositivo arranca
 * o cuando se actualiza la app (acción MY_PACKAGE_REPLACED).
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        CoroutineScope(Dispatchers.IO).launch {
            val repository = TaskRepository(context)
            val enabled = repository.notificationEnabled.first()
            if (!enabled) return@launch

            val tasks = repository.tasks.first()
            ReminderScheduler.scheduleAll(context, tasks)
        }
    }
}


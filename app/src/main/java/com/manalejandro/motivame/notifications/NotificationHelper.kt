package com.manalejandro.motivame.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.manalejandro.motivame.MainActivity
import com.manalejandro.motivame.R
import com.manalejandro.motivame.data.Task
import kotlin.random.Random

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "motivame_channel"
        private const val CHANNEL_NAME = "Recordatorios de Tareas"
        private const val CHANNEL_DESCRIPTION = "Notificaciones para recordarte tus tareas pendientes"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendTaskReminder(task: Task, withSound: Boolean = true) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val motivationalMessage = if (task.goals.isNotEmpty()) {
            task.goals.random()
        } else {
            "¡Recuerda completar esta tarea!"
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ ${task.title}")
            .setContentText(motivationalMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "📝 Tarea: ${task.title}\n\n🎯 Recuerda: $motivationalMessage"
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))

        if (withSound) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notificationBuilder.setSound(defaultSoundUri)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(Random.nextInt(), notificationBuilder.build())
        } catch (e: SecurityException) {
            // El usuario no ha concedido permisos de notificación
        }
    }

    fun sendMotivationalReminder(tasks: List<Task>, withSound: Boolean = true) {
        if (tasks.isEmpty()) return

        val activeTask = tasks.firstOrNull { it.isActive } ?: return
        sendTaskReminder(activeTask, withSound)
    }
}


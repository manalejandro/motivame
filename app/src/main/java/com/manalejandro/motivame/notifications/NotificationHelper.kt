package com.manalejandro.motivame.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.manalejandro.motivame.MainActivity
import com.manalejandro.motivame.R
import com.manalejandro.motivame.data.Task
import kotlin.random.Random

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_SOUND    = "motivame_channel_sound"
        const val CHANNEL_ID_NO_SOUND = "motivame_channel_silent"
    }

    fun sendTaskReminder(task: Task, withSound: Boolean = true) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val motivationalMessage = if (task.goals.isNotEmpty()) task.goals.random()
                                  else context.getString(R.string.notification_default_message)

        // La notificación siempre silenciosa: el sonido lo manejamos nosotros
        // directamente con Ringtone para evitar cualquier interferencia del canal.
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_SOUND)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ ${task.title}")
            .setContentText(motivationalMessage)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    context.getString(R.string.notification_big_text, task.title, motivationalMessage)
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setSilent(true)   // siempre silenciosa a nivel canal

        try {
            NotificationManagerCompat.from(context)
                .notify(Random.nextInt(), notificationBuilder.build())
        } catch (_: SecurityException) { }

        // Reproducir el sonido directamente si está activado
        if (withSound) {
            try {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone: Ringtone = RingtoneManager.getRingtone(context, soundUri)
                ringtone.play()
            } catch (_: Exception) { }
        }
    }

    fun sendMotivationalReminder(tasks: List<Task>, withSound: Boolean = true) {
        if (tasks.isEmpty()) return
        val activeTask = tasks.firstOrNull { it.isActive } ?: return
        sendTaskReminder(activeTask, withSound)
    }
}

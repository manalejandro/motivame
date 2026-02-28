package com.manalejandro.motivame

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.manalejandro.motivame.notifications.NotificationHelper

class MotivameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Limpiar canales obsoletos de versiones anteriores
            nm.deleteNotificationChannel("motivame_channel")
            nm.deleteNotificationChannel(NotificationHelper.CHANNEL_ID_NO_SOUND)

            // Canal con sonido: crear solo si no existe aún
            if (nm.getNotificationChannel(NotificationHelper.CHANNEL_ID_SOUND) == null) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val soundChannel = NotificationChannel(
                    NotificationHelper.CHANNEL_ID_SOUND,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.notification_channel_description)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                    setSound(
                        soundUri,
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                }
                nm.createNotificationChannel(soundChannel)
            }
        }
    }
}

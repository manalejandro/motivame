package com.manalejandro.motivame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.manalejandro.motivame.ui.screens.AddTaskScreen
import com.manalejandro.motivame.ui.screens.MainScreen
import com.manalejandro.motivame.ui.screens.SettingsScreen
import com.manalejandro.motivame.ui.theme.MotivameTheme
import com.manalejandro.motivame.ui.viewmodel.TaskViewModel
import com.manalejandro.motivame.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configurar recordatorios diarios
        setupDailyReminders()

        setContent {
            MotivameTheme {
                MotivameApp()
            }
        }
    }

    private fun setupDailyReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(java.util.Calendar.HOUR_OF_DAY, 9) // 9 AM
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= currentTime) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - currentTime
    }
}

@Composable
fun MotivameApp() {
    val viewModel: TaskViewModel = viewModel()
    var currentScreen by remember { mutableStateOf("main") }

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
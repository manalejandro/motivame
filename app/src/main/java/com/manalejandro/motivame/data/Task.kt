package com.manalejandro.motivame.data

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val goals: List<String>,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val dailyReminders: Int = 3,  // Número de avisos por día (entre 9:00 y 21:00)
    val repeatEveryDays: Int = 3  // Cada cuántos días se repite el ciclo de avisos
)


package com.manalejandro.motivame.data

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val goals: List<String>,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)


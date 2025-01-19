package com.example.studentcard.database.dataclass


data class TodoItem(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = "Medium", // Default priority
    val deadline: String = "",
    val isCompleted: Boolean = false
)
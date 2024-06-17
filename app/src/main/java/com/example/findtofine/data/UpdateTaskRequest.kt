package com.example.findtofine.data

data class UpdateTaskRequest(
    val title: String,
    val startDate: String,
    val finishDate: String,
    val location: String,
    val description: String,
    val items: List<String>
)

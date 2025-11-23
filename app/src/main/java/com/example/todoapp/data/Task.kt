package com.example.todoapp.data

data class Task (                                       // data Classは通常のClassと異なり、便利なメソッドが付与される。(主にデータ保持用)
    val id: String = "",
    val title: String,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueAt: Long? = null,
    val memo: String = ""
)

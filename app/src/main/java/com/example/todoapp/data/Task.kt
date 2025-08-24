package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Task.ktはデータ構造定義　RoomDBで使う、Taskクラスの形を定義(id,title,doneなどなど）
// Roomデータベースに保存される1件のデータの設計図
@Entity(tableName = "tasks")
data class Task (                                       // data Classは通常のClassと異なり、便利なメソッドが付与される。(主にデータ保持用)
@PrimaryKey(autoGenerate = true) val id: Int = 0,       // 自動採番、挿入時の初期値として0を入れる
    val title: String,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

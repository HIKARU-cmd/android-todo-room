package com.example.todoapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// TaskViewModel.ktはデータの橋渡し役、UI(画面）とDBのやりとりを綺麗にわけるために存在
class TaskViewModel(app: Application) : AndroidViewModel(app) {             // 継承したAndroidViewModelにもコンストラクタで初期化したappをわたしている
    private val dao = AppDatabase.get(app).taskDao()                        // TaskViewModel.ktのgetでいんすたんすを作成し、タスク処理関連をまとめたDAOを取り出す

    val tasks: Flow<List<Task>> = dao.observeALL()                          // Flow<List<Task>> タスクの更新に追従できる　非同期データストリーム（次々データが流れてくるイメージ）

    fun add(title: String) = viewModelScope.launch {                        // add関数が呼ばれたら、バックグラウンドでタスクをDBに保存する処理を開始する
        if(title.isBlank()) return@launch                                   // タスク名が空ならこのコルーチンから抜ける
        dao.insert(Task(title = title))
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        dao.deleteById(id)
    }

    fun updateDone(id: Int,done: Boolean) = viewModelScope.launch {
        dao.updateDone(id, done)
    }

    fun updateTitle(id: Int,title: String) = viewModelScope.launch {
        if(title.isBlank()) return@launch                                   // タスク名が空ならこのコルーチンから抜ける
        dao.updateTitle(id, title)
    }
}